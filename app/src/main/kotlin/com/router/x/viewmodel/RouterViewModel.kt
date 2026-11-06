package com.router.x.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.router.x.data.model.DeviceItem
import com.router.x.data.model.RouterItem
import com.router.x.data.model.RouterProtocol
import com.router.x.data.model.TelemetryData
import com.router.x.data.model.WifiConfig
import com.router.x.data.repository.RouterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RouterViewModel(
    private val repository: RouterRepository = RouterRepository()
) : ViewModel() {

    val routers: StateFlow<List<RouterItem>> = repository.routers
    val selectedRouter: StateFlow<RouterItem> = repository.selectedRouter

    val telemetry: StateFlow<TelemetryData> = selectedRouter
        .flatMapLatest { router -> repository.getTelemetryStream(router) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TelemetryData()
        )

    private val _devices = MutableStateFlow<List<DeviceItem>>(emptyList())
    val devices: StateFlow<List<DeviceItem>> = _devices.asStateFlow()

    private val _wifiConfig = MutableStateFlow(WifiConfig())
    val wifiConfig: StateFlow<WifiConfig> = _wifiConfig.asStateFlow()

    // Filters and tabs
    val routerSearchQuery = MutableStateFlow("")
    val routerTabFilter = MutableStateFlow("All") // "All", "Local", "Remote"
    val deviceTabFilter = MutableStateFlow("All") // "All", "Online", "Offline"
    val monitoringTimeRange = MutableStateFlow("1h") // "1h", "6h", "24h", "7d"
    val topologyMode = MutableStateFlow("Topology") // "Topology", "Flow"
    val controlCenterTab = MutableStateFlow("Control") // "Control", "Configuration", "Tools"
    val settingsTab = MutableStateFlow("Wireless") // "Wireless", "Network", "Security"

    // Filtered lists
    val filteredRouters = combine(routers, routerSearchQuery, routerTabFilter) { list, query, filter ->
        list.filter { router ->
            val matchesQuery = query.isEmpty() ||
                    router.name.contains(query, ignoreCase = true) ||
                    router.modelName.contains(query, ignoreCase = true) ||
                    router.ipAddress.contains(query, ignoreCase = true)

            val matchesTab = when (filter) {
                "Local" -> router.isLocal
                "Remote" -> !router.isLocal
                else -> true
            }
            matchesQuery && matchesTab
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val filteredDevices = combine(devices, deviceTabFilter) { list, filter ->
        when (filter) {
            "Online" -> list.filter { it.isOnline }
            "Offline" -> list.filter { !it.isOnline }
            else -> list
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        viewModelScope.launch {
            selectedRouter.collect { router ->
                loadRouterData(router)
            }
        }
    }

    fun selectRouter(router: RouterItem) {
        repository.selectRouter(router)
    }

    private fun loadRouterData(router: RouterItem) {
        viewModelScope.launch {
            _devices.value = repository.fetchDevices(router)
            _wifiConfig.value = repository.fetchWifiConfig(router)
        }
    }

    fun addRouter(name: String, model: String, ip: String, protocol: RouterProtocol, isLocal: Boolean) {
        val newRouter = RouterItem(
            id = "router-${System.currentTimeMillis()}",
            name = name,
            modelName = model,
            ipAddress = ip,
            protocol = protocol,
            isOnline = true,
            isLocal = isLocal,
            uptime = "0d 01h 00m",
            cpuUsage = 10,
            ramUsage = 30,
            temperature = 38
        )
        repository.addRouter(newRouter)
    }

    fun rebootRouter(onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val res = repository.reboot(selectedRouter.value)
            onComplete(res.isSuccess)
        }
    }

    fun blockDevice(macAddress: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val res = repository.blockDevice(selectedRouter.value, macAddress)
            if (res.isSuccess) {
                _devices.value = _devices.value.map {
                    if (it.macAddress == macAddress) it.copy(isBlocked = true) else it
                }
            }
            onComplete(res.isSuccess)
        }
    }

    fun unblockDevice(macAddress: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val res = repository.unblockDevice(selectedRouter.value, macAddress)
            if (res.isSuccess) {
                _devices.value = _devices.value.map {
                    if (it.macAddress == macAddress) it.copy(isBlocked = false) else it
                }
            }
            onComplete(res.isSuccess)
        }
    }

    fun renameDevice(macAddress: String, newName: String) {
        _devices.value = _devices.value.map {
            if (it.macAddress == macAddress) it.copy(name = newName) else it
        }
    }

    fun updateWifiConfig(config: WifiConfig, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val res = repository.saveWifiConfig(selectedRouter.value, config)
            if (res.isSuccess) {
                _wifiConfig.value = config
            }
            onComplete(res.isSuccess)
        }
    }
}
