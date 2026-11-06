package com.router.x.data.repository

import com.router.x.RouterXApp
import com.router.x.data.model.DeviceItem
import com.router.x.data.model.RouterCredentials
import com.router.x.data.model.RouterItem
import com.router.x.data.model.TelemetryData
import com.router.x.data.model.WifiConfig
import com.router.x.data.network.NetworkScanner
import com.router.x.data.protocol.DriverFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class RouterRepository {

    private val storage = RouterStorage(RouterXApp.instance)
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _routers = MutableStateFlow<List<RouterItem>>(emptyList())
    val routers = _routers.asStateFlow()

    private val _selectedRouter = MutableStateFlow(
        RouterItem(
            id = "default",
            name = "Gateway Router",
            modelName = "Network Gateway",
            ipAddress = "192.168.1.1"
        )
    )
    val selectedRouter = _selectedRouter.asStateFlow()

    init {
        scope.launch {
            val saved = storage.loadRouters()
            if (saved.isNotEmpty()) {
                _routers.value = saved
                _selectedRouter.value = saved.first()
            } else {
                val detected = storage.autoDetectInitialRouter()
                _routers.value = listOf(detected)
                _selectedRouter.value = detected
                storage.saveRouters(listOf(detected))
            }
        }
    }

    fun selectRouter(router: RouterItem) {
        _selectedRouter.value = router
    }

    fun addRouter(newRouter: RouterItem) {
        val updated = _routers.value + newRouter
        _routers.value = updated
        _selectedRouter.value = newRouter
        storage.saveRouters(updated)
    }

    fun removeRouter(routerId: String) {
        val updated = _routers.value.filter { it.id != routerId }
        _routers.value = updated
        if (_selectedRouter.value.id == routerId && updated.isNotEmpty()) {
            _selectedRouter.value = updated.first()
        }
        storage.saveRouters(updated)
    }

    fun getTelemetryStream(router: RouterItem): Flow<TelemetryData> = flow {
        val driver = DriverFactory.getDriver(router)
        while (true) {
            val result = driver.fetchTelemetry(router)
            emit(result.getOrDefault(TelemetryData()))
            delay(1500)
        }
    }

    suspend fun fetchDevices(router: RouterItem): List<DeviceItem> {
        val driver = DriverFactory.getDriver(router)
        val raw = driver.fetchConnectedDevices(router).getOrDefault(emptyList())
        val blocked = storage.getBlockedMacs()
        return raw.map { dev ->
            val alias = storage.getDeviceAlias(dev.macAddress)
            dev.copy(
                name = alias ?: dev.name,
                isBlocked = blocked.contains(dev.macAddress)
            )
        }
    }

    suspend fun fetchWifiConfig(router: RouterItem): WifiConfig {
        val driver = DriverFactory.getDriver(router)
        return driver.fetchWifiConfig(router).getOrDefault(WifiConfig())
    }

    suspend fun saveWifiConfig(router: RouterItem, config: WifiConfig): Result<Boolean> {
        val driver = DriverFactory.getDriver(router)
        return driver.applyWifiConfig(router, config)
    }

    suspend fun reboot(router: RouterItem): Result<Boolean> {
        val driver = DriverFactory.getDriver(router)
        return driver.rebootRouter(router)
    }

    suspend fun blockDevice(router: RouterItem, macAddress: String): Result<Boolean> {
        storage.setDeviceBlocked(macAddress, true)
        val driver = DriverFactory.getDriver(router)
        return driver.blockDevice(router, macAddress)
    }

    suspend fun unblockDevice(router: RouterItem, macAddress: String): Result<Boolean> {
        storage.setDeviceBlocked(macAddress, false)
        val driver = DriverFactory.getDriver(router)
        return driver.unblockDevice(router, macAddress)
    }

    fun renameDevice(macAddress: String, newName: String) {
        storage.saveDeviceAlias(macAddress, newName)
    }

    fun updateRouterAuth(routerId: String, isAuthenticated: Boolean, credentials: RouterCredentials) {
        val updated = _routers.value.map {
            if (it.id == routerId) {
                it.copy(isAuthenticated = isAuthenticated, credentials = credentials)
            } else it
        }
        _routers.value = updated
        if (_selectedRouter.value.id == routerId) {
            _selectedRouter.value = _selectedRouter.value.copy(
                isAuthenticated = isAuthenticated,
                credentials = credentials
            )
        }
        storage.saveRouters(updated)
    }
}
