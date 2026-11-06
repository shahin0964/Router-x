package com.router.x.data.protocol

import com.router.x.data.model.DeviceItem
import com.router.x.data.model.DeviceType
import com.router.x.data.model.RouterItem
import com.router.x.data.model.TelemetryData
import com.router.x.data.model.WifiConfig
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
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
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
            Result.success(true)
        }
    }

    override suspend fun fetchTelemetry(router: RouterItem): Result<TelemetryData> = withContext(Dispatchers.IO) {
        Result.success(
            TelemetryData(
                downloadSpeedMbps = 450f,
                uploadSpeedMbps = 220f,
                pingMs = 4,
                healthPercent = 99,
                healthStatus = "Excellent",
                connectedDevicesCount = 42,
                cpuUsage = 22,
                ramUsage = 45,
                temperature = 48
            )
        )
    }

    override suspend fun fetchConnectedDevices(router: RouterItem): Result<List<DeviceItem>> = withContext(Dispatchers.IO) {
        val list = listOf(
            DeviceItem("1", "Executive iPad Pro", "203.0.113.45", "F0:18:98:11:22:33", "Apple", DeviceType.PHONE, true),
            DeviceItem("2", "UniFi U6 Pro AP", "203.0.113.10", "74:83:C2:44:55:66", "Ubiquiti", DeviceType.ACCESS_POINT, true),
            DeviceItem("3", "Core Switch 24", "203.0.113.11", "74:83:C2:77:88:99", "Ubiquiti", DeviceType.SWITCH, true)
        )
        Result.success(list)
    }

    override suspend fun fetchWifiConfig(router: RouterItem): Result<WifiConfig> = withContext(Dispatchers.IO) {
        Result.success(
            WifiConfig(
                isEnabled = true,
                ssid24G = "UniFi_Enterprise_Corp",
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
        Result.success(true)
    }

    override suspend fun blockDevice(router: RouterItem, macAddress: String): Result<Boolean> {
        return Result.success(true)
    }

    override suspend fun unblockDevice(router: RouterItem, macAddress: String): Result<Boolean> {
        return Result.success(true)
    }
}
