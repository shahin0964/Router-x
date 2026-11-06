package com.router.x.data.repository

import com.router.x.data.model.DeviceItem
import com.router.x.data.model.RouterItem
import com.router.x.data.model.RouterProtocol
import com.router.x.data.model.TelemetryData
import com.router.x.data.model.WifiConfig
import com.router.x.data.protocol.DriverFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow

class RouterRepository {

    private val initialRouters = listOf(
        RouterItem(
            id = "router-1",
            name = "Home Router",
            modelName = "TP-Link Archer AX73",
            ipAddress = "192.168.0.1",
            firmwareVersion = "1.0.3 Build 2025",
            protocol = RouterProtocol.TPLINK_HTTP,
            isOnline = true,
            isLocal = true,
            uptime = "3d 12h 26m",
            cpuUsage = 12,
            ramUsage = 38,
            temperature = 40
        ),
        RouterItem(
            id = "router-2",
            name = "Office Router",
            modelName = "MikroTik hEX S",
            ipAddress = "10.0.0.1",
            firmwareVersion = "v7.16 Stable",
            protocol = RouterProtocol.MIKROTIK_REST,
            isOnline = true,
            isLocal = true,
            uptime = "14d 06h 11m",
            cpuUsage = 18,
            ramUsage = 44,
            temperature = 43
        ),
        RouterItem(
            id = "router-3",
            name = "Guest Router",
            modelName = "OpenWrt",
            ipAddress = "192.168.10.1",
            firmwareVersion = "23.05.4",
            protocol = RouterProtocol.OPENWRT_UBUS,
            isOnline = true,
            isLocal = true,
            uptime = "1d 04h 50m",
            cpuUsage = 7,
            ramUsage = 29,
            temperature = 38
        ),
        RouterItem(
            id = "router-4",
            name = "Remote Site",
            modelName = "UniFi Dream Machine",
            ipAddress = "203.0.113.1",
            firmwareVersion = "UniFi OS 3.2.12",
            protocol = RouterProtocol.UNIFI_API,
            isOnline = true,
            isLocal = false,
            uptime = "42d 18h 03m",
            cpuUsage = 25,
            ramUsage = 52,
            temperature = 47
        )
    )

    private val _routers = MutableStateFlow(initialRouters)
    val routers = _routers.asStateFlow()

    private val _selectedRouter = MutableStateFlow(initialRouters[0])
    val selectedRouter = _selectedRouter.asStateFlow()

    fun selectRouter(router: RouterItem) {
        _selectedRouter.value = router
    }

    fun addRouter(newRouter: RouterItem) {
        _routers.value = _routers.value + newRouter
        _selectedRouter.value = newRouter
    }

    fun removeRouter(routerId: String) {
        _routers.value = _routers.value.filter { it.id != routerId }
        if (_selectedRouter.value.id == routerId && _routers.value.isNotEmpty()) {
            _selectedRouter.value = _routers.value.first()
        }
    }

    fun getTelemetryStream(router: RouterItem): Flow<TelemetryData> = flow {
        val driver = DriverFactory.getDriver(router)
        while (true) {
            val result = driver.fetchTelemetry(router)
            emit(result.getOrDefault(TelemetryData()))
            delay(2000)
        }
    }

    suspend fun fetchDevices(router: RouterItem): List<DeviceItem> {
        val driver = DriverFactory.getDriver(router)
        return driver.fetchConnectedDevices(router).getOrDefault(emptyList())
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
        val driver = DriverFactory.getDriver(router)
        return driver.blockDevice(router, macAddress)
    }

    suspend fun unblockDevice(router: RouterItem, macAddress: String): Result<Boolean> {
        val driver = DriverFactory.getDriver(router)
        return driver.unblockDevice(router, macAddress)
    }
}
