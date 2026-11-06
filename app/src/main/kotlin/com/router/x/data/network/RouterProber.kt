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

                if (body.contains("TP-Link", ignoreCase = true) || serverHeader.contains("TP-Link", ignoreCase = true) || body.contains("Archer", ignoreCase = true)) {
                    val model = if (body.contains("Archer", ignoreCase = true)) "TP-Link Archer Series" else "TP-Link Router"
                    return@withContext model to RouterProtocol.TPLINK_HTTP
                }

                if (body.contains("OpenWrt", ignoreCase = true) || body.contains("LuCI", ignoreCase = true)) {
                    return@withContext "OpenWrt / LuCI Router" to RouterProtocol.OPENWRT_UBUS
                }

                if (body.contains("MikroTik", ignoreCase = true) || body.contains("RouterOS", ignoreCase = true)) {
                    return@withContext "MikroTik Router" to RouterProtocol.MIKROTIK_REST
                }

                if (body.contains("UniFi", ignoreCase = true) || body.contains("Ubiquiti", ignoreCase = true)) {
                    return@withContext "UniFi Gateway" to RouterProtocol.UNIFI_API
                }

                if (body.contains("ASUS", ignoreCase = true) || body.contains("RT-", ignoreCase = true)) {
                    return@withContext "ASUS Wireless Router" to RouterProtocol.TPLINK_HTTP
                }

                if (body.contains("Netgear", ignoreCase = true)) {
                    return@withContext "Netgear Router" to RouterProtocol.TPLINK_HTTP
                }

                if (body.contains("Tenda", ignoreCase = true)) {
                    return@withContext "Tenda Wireless Router" to RouterProtocol.TPLINK_HTTP
                }

                if (body.contains("D-Link", ignoreCase = true)) {
                    return@withContext "D-Link Router" to RouterProtocol.TPLINK_HTTP
                }

                return@withContext "Gateway Web Router" to RouterProtocol.UPNP_IGD
            } catch (_: Exception) {}
        }

        return@withContext "Network Gateway Router" to RouterProtocol.UPNP_IGD
    }
}
