package com.router.x.data.network

import com.router.x.data.model.RouterItem
import com.router.x.data.model.RouterProtocol
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.TimeUnit

object RouterProber {

    private val client = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS)
        .readTimeout(4, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    suspend fun probeRouter(ipAddress: String): Pair<String, RouterProtocol> = withContext(Dispatchers.IO) {
        // 1. Probe MikroTik RouterOS API port 8728
        try {
            Socket().use { s ->
                s.connect(InetSocketAddress(ipAddress, 8728), 1000)
                return@withContext "MikroTik RouterOS" to RouterProtocol.MIKROTIK_REST
            }
        } catch (_: Exception) {}

        // 2. Probe HTTP on Port 80 / 443
        val schemes = listOf("http", "https")
        for (scheme in schemes) {
            try {
                val req = Request.Builder().url("$scheme://$ipAddress").build()
                val resp = client.newCall(req).execute()
                val serverHeader = resp.header("Server") ?: ""
                val body = resp.body?.string() ?: ""

                // BDCOM GPON/EPON Detection
                if (body.contains("BDCOM", ignoreCase = true) || serverHeader.contains("BDCOM", ignoreCase = true) ||
                    body.contains("GP1704", ignoreCase = true) || body.contains("P1501", ignoreCase = true) ||
                    (body.contains("EPON", ignoreCase = true) && body.contains("ONU", ignoreCase = true))) {
                    return@withContext "BDCOM GPON/EPON Router" to RouterProtocol.BDCOM_GPON
                }

                // TP-Link
                if (body.contains("TP-Link", ignoreCase = true) || serverHeader.contains("TP-Link", ignoreCase = true) || body.contains("Archer", ignoreCase = true) || body.contains("WR84", ignoreCase = true) || body.contains("WR94", ignoreCase = true)) {
                    val model = if (body.contains("Archer", ignoreCase = true)) "TP-Link Archer Series" else "TP-Link Wireless Router"
                    return@withContext model to RouterProtocol.TPLINK_HTTP
                }

                // Tenda
                if (body.contains("Tenda", ignoreCase = true) || serverHeader.contains("Tenda", ignoreCase = true) || body.contains("tendawifi", ignoreCase = true) || body.contains("Nova", ignoreCase = true)) {
                    return@withContext "Tenda Wireless Router" to RouterProtocol.TENDA_HTTP
                }

                // ASUS
                if (body.contains("ASUS", ignoreCase = true) || body.contains("ASUSWRT", ignoreCase = true) || body.contains("RT-", ignoreCase = true) || body.contains("TUF-", ignoreCase = true)) {
                    return@withContext "ASUS RT / TUF Router" to RouterProtocol.ASUS_WRT
                }

                // Netgear
                if (body.contains("Netgear", ignoreCase = true) || body.contains("Nighthawk", ignoreCase = true) || body.contains("Orbi", ignoreCase = true) || body.contains("routerlogin", ignoreCase = true)) {
                    return@withContext "Netgear Nighthawk Router" to RouterProtocol.NETGEAR_GENIE
                }

                // D-Link
                if (body.contains("D-Link", ignoreCase = true) || body.contains("DIR-", ignoreCase = true) || body.contains("dlink", ignoreCase = true)) {
                    return@withContext "D-Link Wireless Router" to RouterProtocol.DLINK_HNAP
                }

                // Huawei & ZTE Optical ONUs
                if (body.contains("Huawei", ignoreCase = true) || body.contains("EchoLife", ignoreCase = true) || body.contains("HG8145", ignoreCase = true) || body.contains("HG8245", ignoreCase = true) || body.contains("ZTE", ignoreCase = true) || body.contains("ZXHN", ignoreCase = true)) {
                    return@withContext "Huawei / ZTE Optical Gateway" to RouterProtocol.HUAWEI_ZTE
                }

                // OpenWrt / LuCI
                if (body.contains("OpenWrt", ignoreCase = true) || body.contains("LuCI", ignoreCase = true)) {
                    return@withContext "OpenWrt / LuCI Router" to RouterProtocol.OPENWRT_UBUS
                }

                // MikroTik
                if (body.contains("MikroTik", ignoreCase = true) || body.contains("RouterOS", ignoreCase = true)) {
                    return@withContext "MikroTik RouterOS" to RouterProtocol.MIKROTIK_REST
                }

                // UniFi
                if (body.contains("UniFi", ignoreCase = true) || body.contains("Ubiquiti", ignoreCase = true)) {
                    return@withContext "UniFi Gateway" to RouterProtocol.UNIFI_API
                }

                // Extract HTML title if available
                val titleMatch = "<title>(.*?)</title>".toRegex(RegexOption.IGNORE_CASE).find(body)
                val pageTitle = titleMatch?.groupValues?.getOrNull(1)?.trim()
                if (!pageTitle.isNullOrEmpty() && pageTitle.length in 3..40 && !pageTitle.contains("404") && !pageTitle.contains("Error")) {
                    return@withContext pageTitle to RouterProtocol.AUTO_DETECT
                }

                return@withContext "Universal Wi-Fi Router" to RouterProtocol.AUTO_DETECT
            } catch (_: Exception) {}
        }

        return@withContext "Universal Wi-Fi Router" to RouterProtocol.AUTO_DETECT
    }
}
