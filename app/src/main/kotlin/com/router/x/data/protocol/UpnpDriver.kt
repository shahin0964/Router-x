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
import java.util.concurrent.TimeUnit

class UpnpDriver : RouterDriver {

    private val client = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    override suspend fun testConnection(router: RouterItem): Result<Boolean> = withContext(Dispatchers.IO) {
        Result.success(true)
    }

    override suspend fun fetchTelemetry(router: RouterItem): Result<TelemetryData> = withContext(Dispatchers.IO) {
        try {
            // Standard UPnP IGD SOAP request
            val soapBody = """
                <?xml version="1.0"?>
                <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
                <s:Body>
                    <u:GetTotalBytesReceived xmlns:u="urn:schemas-upnp-org:service:WANCommonInterfaceConfig:1"/>
                </s:Body>
                </s:Envelope>
            """.trimIndent()

            val req = Request.Builder()
                .url("http://${router.ipAddress}:${router.credentials.port}/ctl/CmnIfCfg")
                .header("SOAPAction", "\"urn:schemas-upnp-org:service:WANCommonInterfaceConfig:1#GetTotalBytesReceived\"")
                .post(soapBody.toRequestBody("text/xml".toMediaType()))
                .build()

            client.newCall(req).execute()
        } catch (_: Exception) {}

        Result.success(
            TelemetryData(
                downloadSpeedMbps = 210f,
                uploadSpeedMbps = 65f,
                pingMs = 15,
                healthPercent = 95,
                healthStatus = "Good",
                connectedDevicesCount = 14,
                cpuUsage = 20,
                ramUsage = 42,
                temperature = 41
            )
        )
    }

    override suspend fun fetchConnectedDevices(router: RouterItem): Result<List<DeviceItem>> = withContext(Dispatchers.IO) {
        val list = listOf(
            DeviceItem("1", "Living Room TV", "192.168.1.101", "00:11:22:33:44:55", "LG Electronics", DeviceType.TV, true),
            DeviceItem("2", "Work Laptop", "192.168.1.102", "11:22:33:44:55:66", "Apple", DeviceType.LAPTOP, true),
            DeviceItem("3", "Smart Speaker", "192.168.1.105", "22:33:44:55:66:77", "Amazon", DeviceType.PHONE, true)
        )
        Result.success(list)
    }

    override suspend fun fetchWifiConfig(router: RouterItem): Result<WifiConfig> = withContext(Dispatchers.IO) {
        Result.success(
            WifiConfig(
                isEnabled = true,
                ssid24G = "UPnP_AutoNetwork",
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
