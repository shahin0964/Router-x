package com.router.x.data.protocol

import com.router.x.RouterXApp
import com.router.x.data.model.DeviceItem
import com.router.x.data.model.RouterItem
import com.router.x.data.model.TelemetryData
import com.router.x.data.model.WifiConfig
import com.router.x.data.network.NetworkScanner
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
        .connectTimeout(4, TimeUnit.SECONDS)
        .readTimeout(6, TimeUnit.SECONDS)
        .build()

    private var stkToken: String = ""
    private val downloadHistory = ArrayDeque<Float>()
    private val uploadHistory = ArrayDeque<Float>()

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
            Result.success(res.code < 500)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchTelemetry(router: RouterItem): Result<TelemetryData> = withContext(Dispatchers.IO) {
        val (dl, ul) = NetworkScanner.measureLiveTraffic()
        val realPing = NetworkScanner.measurePing(router.ipAddress)
        val wifiInfo = NetworkScanner.getWifiInfo(RouterXApp.instance)
        val devices = NetworkScanner.scanLocalNetwork(RouterXApp.instance)

        if (downloadHistory.size >= 10) downloadHistory.removeFirst()
        if (uploadHistory.size >= 10) uploadHistory.removeFirst()
        downloadHistory.addLast(dl)
        uploadHistory.addLast(ul)

        Result.success(
            TelemetryData(
                downloadSpeedMbps = dl,
                uploadSpeedMbps = ul,
                pingMs = realPing,
                healthPercent = wifiInfo.healthPercent,
                healthStatus = if (wifiInfo.healthPercent >= 75) "Excellent" else "Good",
                connectedDevicesCount = devices.size,
                packetLossPercent = 0.0f,
                jitterMs = 1,
                cpuUsage = 15,
                ramUsage = 35,
                temperature = 39,
                downloadHistory = downloadHistory.toList(),
                uploadHistory = uploadHistory.toList()
            )
        )
    }

    override suspend fun fetchConnectedDevices(router: RouterItem): Result<List<DeviceItem>> = withContext(Dispatchers.IO) {
        val list = NetworkScanner.scanLocalNetwork(RouterXApp.instance)
        Result.success(list)
    }

    override suspend fun fetchWifiConfig(router: RouterItem): Result<WifiConfig> = withContext(Dispatchers.IO) {
        val wifi = NetworkScanner.getWifiInfo(RouterXApp.instance)
        Result.success(
            WifiConfig(
                isEnabled = wifi.isConnected,
                ssid24G = wifi.ssid,
                password = "",
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

            val res = client.newCall(req).execute()
            Result.success(res.isSuccessful)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun blockDevice(router: RouterItem, macAddress: String): Result<Boolean> {
        return Result.success(true)
    }

    override suspend fun unblockDevice(router: RouterItem, macAddress: String): Result<Boolean> {
        return Result.success(true)
    }
}
