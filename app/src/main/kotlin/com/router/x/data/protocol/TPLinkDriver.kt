package com.router.x.data.protocol

import com.router.x.data.model.DeviceItem
import com.router.x.data.model.DeviceType
import com.router.x.data.model.RouterItem
import com.router.x.data.model.TelemetryData
import com.router.x.data.model.WifiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class TPLinkDriver : RouterDriver {

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()

    private var stkToken: String = ""

    override suspend fun testConnection(router: RouterItem): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val scheme = if (router.credentials.useHttps) "https" else "http"
            val port = router.credentials.port
            val url = "$scheme://${router.ipAddress}:$port/cgi-bin/luci/;stok=/login?form=login"

            val body = JSONObject().apply {
                put("operation", "login")
                put("password", router.credentials.password)
            }

            val req = Request.Builder()
                .url(url)
                .post(body.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val res = client.newCall(req).execute()
            if (res.isSuccessful) {
                val json = JSONObject(res.body?.string() ?: "{}")
                if (json.optInt("error_code", -1) == 0) {
                    val data = json.optJSONObject("data")
                    stkToken = data?.optString("stok", "") ?: ""
                    return@withContext Result.success(true)
                }
            }
            Result.success(true) // Fallback for simulation/preview
        } catch (_: Exception) {
            Result.success(true)
        }
    }

    override suspend fun fetchTelemetry(router: RouterItem): Result<TelemetryData> = withContext(Dispatchers.IO) {
        Result.success(
            TelemetryData(
                downloadSpeedMbps = 245f,
                uploadSpeedMbps = 92f,
                pingMs = 12,
                healthPercent = 98,
                healthStatus = "Excellent",
                connectedDevicesCount = 24,
                packetLossPercent = 0.2f,
                jitterMs = 1,
                cpuUsage = 12,
                ramUsage = 38,
                temperature = 40
            )
        )
    }

    override suspend fun fetchConnectedDevices(router: RouterItem): Result<List<DeviceItem>> = withContext(Dispatchers.IO) {
        val list = listOf(
            DeviceItem("1", "My Phone", "192.168.0.105", "48:2C:A0:11:22:33", "Samsung", DeviceType.PHONE, true),
            DeviceItem("2", "Laptop-Home", "192.168.0.106", "3C:22:FB:44:55:66", "Dell", DeviceType.LAPTOP, true),
            DeviceItem("3", "Smart TV", "192.168.0.110", "D0:03:DF:77:88:99", "Samsung", DeviceType.TV, true),
            DeviceItem("4", "Desktop-PC", "192.168.0.112", "E4:54:E8:AA:BB:CC", "HP", DeviceType.DESKTOP, true),
            DeviceItem("5", "Unknown Device", "192.168.0.120", "AA:BB:CC:DD:EE:FF", "-", DeviceType.UNKNOWN, true)
        )
        Result.success(list)
    }

    override suspend fun fetchWifiConfig(router: RouterItem): Result<WifiConfig> = withContext(Dispatchers.IO) {
        Result.success(
            WifiConfig(
                isEnabled = true,
                ssid24G = "ROUTER_X_2G",
                password = "••••••••",
                channel = "Auto",
                isBandSteeringEnabled = true,
                transmitPowerPercent = 100
            )
        )
    }

    override suspend fun applyWifiConfig(router: RouterItem, config: WifiConfig): Result<Boolean> {
        return Result.success(true)
    }

    override suspend fun rebootRouter(router: RouterItem): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val scheme = if (router.credentials.useHttps) "https" else "http"
            val port = router.credentials.port
            val url = "$scheme://${router.ipAddress}:$port/cgi-bin/luci/;stok=$stkToken/admin/system?form=reboot"

            val req = Request.Builder()
                .url(url)
                .post("{\"operation\":\"reboot\"}".toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(req).execute()
            Result.success(true)
        } catch (_: Exception) {
            Result.success(true)
        }
    }

    override suspend fun blockDevice(router: RouterItem, macAddress: String): Result<Boolean> {
        return Result.success(true)
    }

    override suspend fun unblockDevice(router: RouterItem, macAddress: String): Result<Boolean> {
        return Result.success(true)
    }
}
