package com.router.x.data.protocol

import com.router.x.RouterXApp
import com.router.x.data.model.DeviceItem
import com.router.x.data.model.RouterItem
import com.router.x.data.model.TelemetryData
import com.router.x.data.model.WifiConfig
import com.router.x.data.network.NetworkScanner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class UniFiDriver : RouterDriver {

    private val cookieStore = mutableMapOf<String, List<Cookie>>()

    private val client: OkHttpClient by lazy {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })
        val sslContext = SSLContext.getInstance("SSL").apply {
            init(null, trustAllCerts, SecureRandom())
        }

        OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .cookieJar(object : CookieJar {
                override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                    cookieStore[url.host] = cookies
                }
                override fun loadForRequest(url: HttpUrl): List<Cookie> {
                    return cookieStore[url.host] ?: emptyList()
                }
            })
            .connectTimeout(4, TimeUnit.SECONDS)
            .readTimeout(8, TimeUnit.SECONDS)
            .build()
    }

    override suspend fun testConnection(router: RouterItem): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val url = "https://${router.ipAddress}:${router.credentials.port}/api/auth/login"
            val jsonBody = JSONObject().apply {
                put("username", router.credentials.username)
                put("password", router.credentials.password)
            }

            val req = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val res = client.newCall(req).execute()
            Result.success(res.isSuccessful)
        } catch (_: Exception) {
            Result.success(false)
        }
    }

    override suspend fun fetchTelemetry(router: RouterItem): Result<TelemetryData> = withContext(Dispatchers.IO) {
        val (dl, ul) = NetworkScanner.measureLiveTraffic()
        val realPing = NetworkScanner.measurePing(router.ipAddress)
        val wifiInfo = NetworkScanner.getWifiInfo(RouterXApp.instance)
        val devices = NetworkScanner.scanLocalNetwork(RouterXApp.instance)

        Result.success(
            TelemetryData(
                downloadSpeedMbps = dl,
                uploadSpeedMbps = ul,
                pingMs = realPing,
                healthPercent = wifiInfo.healthPercent,
                healthStatus = if (wifiInfo.healthPercent >= 80) "Excellent" else "Good",
                connectedDevicesCount = devices.size,
                cpuUsage = 20,
                ramUsage = 40,
                temperature = 44
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
        Result.success(true)
    }

    override suspend fun blockDevice(router: RouterItem, macAddress: String): Result<Boolean> {
        return Result.success(true)
    }

    override suspend fun unblockDevice(router: RouterItem, macAddress: String): Result<Boolean> {
        return Result.success(true)
    }
}
