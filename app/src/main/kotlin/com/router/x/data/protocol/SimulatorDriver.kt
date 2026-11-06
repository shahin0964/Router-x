package com.router.x.data.protocol

import com.router.x.data.model.DeviceItem
import com.router.x.data.model.DeviceType
import com.router.x.data.model.RouterItem
import com.router.x.data.model.TelemetryData
import com.router.x.data.model.WifiConfig
import kotlinx.coroutines.delay
import kotlin.random.Random

class SimulatorDriver : RouterDriver {

    private val blockedMacs = mutableSetOf<String>()
    private var currentWifiConfig = WifiConfig(
        isEnabled = true,
        ssid24G = "ROUTER_X_2G",
        password = "••••••••",
        channel = "Auto",
        isBandSteeringEnabled = true,
        transmitPowerPercent = 100
    )

    private val baseDevices = mutableListOf(
        DeviceItem("1", "My Phone", "192.168.0.105", "AA:BB:CC:11:22:33", "Samsung", DeviceType.PHONE, isOnline = true),
        DeviceItem("2", "Laptop-Home", "192.168.0.106", "AA:BB:CC:44:55:66", "Dell", DeviceType.LAPTOP, isOnline = true),
        DeviceItem("3", "Smart TV", "192.168.0.110", "AA:BB:CC:77:88:99", "Samsung", DeviceType.TV, isOnline = true),
        DeviceItem("4", "Desktop-PC", "192.168.0.112", "AA:BB:CC:AA:BB:CC", "HP", DeviceType.DESKTOP, isOnline = true),
        DeviceItem("5", "Unknown Device", "192.168.0.120", "AA:BB:CC:DD:EE:FF", "-", DeviceType.UNKNOWN, isOnline = true),
        DeviceItem("6", "Tablet Air", "192.168.0.125", "11:22:33:44:55:66", "Apple", DeviceType.PHONE, isOnline = true),
        DeviceItem("7", "Office Workstation", "192.168.0.130", "22:33:44:55:66:77", "Lenovo", DeviceType.DESKTOP, isOnline = false),
        DeviceItem("8", "Guest iPhone", "192.168.0.135", "33:44:55:66:77:88", "Apple", DeviceType.PHONE, isOnline = false)
    )

    override suspend fun testConnection(router: RouterItem): Result<Boolean> {
        delay(300)
        return Result.success(true)
    }

    override suspend fun fetchTelemetry(router: RouterItem): Result<TelemetryData> {
        val downloadNoise = Random.nextInt(-10, 12)
        val uploadNoise = Random.nextInt(-5, 6)
        val pingNoise = Random.nextInt(-2, 3)

        val dl = (245f + downloadNoise).coerceAtLeast(150f)
        val ul = (92f + uploadNoise).coerceAtLeast(50f)
        val ping = (12 + pingNoise).coerceAtLeast(8)

        val dlHistory = listOf(
            45f, 65f, 110f, 95f, 140f, 125f, 150f, 130f, dl.coerceAtMost(160f)
        )
        val ulHistory = listOf(
            20f, 35f, 60f, 45f, 85f, 70f, 92f, 65f, ul.coerceAtMost(110f)
        )

        return Result.success(
            TelemetryData(
                downloadSpeedMbps = dl,
                uploadSpeedMbps = ul,
                pingMs = ping,
                healthPercent = 98,
                healthStatus = "Excellent",
                connectedDevicesCount = baseDevices.count { it.isOnline },
                packetLossPercent = 0.2f,
                jitterMs = 1,
                cpuUsage = 12 + Random.nextInt(0, 3),
                ramUsage = 38,
                temperature = 40,
                downloadHistory = dlHistory,
                uploadHistory = ulHistory
            )
        )
    }

    override suspend fun fetchConnectedDevices(router: RouterItem): Result<List<DeviceItem>> {
        delay(200)
        return Result.success(baseDevices.map {
            it.copy(isBlocked = blockedMacs.contains(it.macAddress))
        })
    }

    override suspend fun fetchWifiConfig(router: RouterItem): Result<WifiConfig> {
        delay(150)
        return Result.success(currentWifiConfig)
    }

    override suspend fun applyWifiConfig(router: RouterItem, config: WifiConfig): Result<Boolean> {
        delay(400)
        currentWifiConfig = config
        return Result.success(true)
    }

    override suspend fun rebootRouter(router: RouterItem): Result<Boolean> {
        delay(800)
        return Result.success(true)
    }

    override suspend fun blockDevice(router: RouterItem, macAddress: String): Result<Boolean> {
        delay(250)
        blockedMacs.add(macAddress)
        return Result.success(true)
    }

    override suspend fun unblockDevice(router: RouterItem, macAddress: String): Result<Boolean> {
        delay(250)
        blockedMacs.remove(macAddress)
        return Result.success(true)
    }
}
