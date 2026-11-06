package com.router.x.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.router.x.data.model.RouterItem
import com.router.x.data.model.RouterProtocol
import com.router.x.data.network.NetworkScanner
import com.router.x.data.network.RouterProber
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class RouterStorage(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("router_x_prefs", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    fun loadRouters(): List<RouterItem> {
        val raw = prefs.getString("saved_routers", null)
        if (!raw.isNullOrEmpty()) {
            try {
                return json.decodeFromString<List<RouterItem>>(raw)
            } catch (_: Exception) {}
        }
        return emptyList()
    }

    fun saveRouters(routers: List<RouterItem>) {
        val raw = json.encodeToString(routers)
        prefs.edit().putString("saved_routers", raw).apply()
    }

    suspend fun autoDetectInitialRouter(): RouterItem {
        val wifi = NetworkScanner.getWifiInfo(context)
        val (probedModel, probedProto) = RouterProber.probeRouter(wifi.gatewayIp)

        val routerName = if (wifi.ssid != "Not Connected") "${wifi.ssid} Router" else "Home Router"
        val modelName = probedModel

        return RouterItem(
            id = "router-gateway",
            name = routerName,
            modelName = modelName,
            ipAddress = wifi.gatewayIp,
            firmwareVersion = "Live Firmware",
            protocol = probedProto,
            isOnline = wifi.isConnected,
            isLocal = true,
            uptime = "Active",
            cpuUsage = 15,
            ramUsage = 35,
            temperature = 39
        )
    }

    fun getDeviceAlias(mac: String): String? {
        return prefs.getString("alias_$mac", null)
    }

    fun saveDeviceAlias(mac: String, alias: String) {
        prefs.edit().putString("alias_$mac", alias).apply()
    }

    fun getBlockedMacs(): Set<String> {
        return prefs.getStringSet("blocked_macs", emptySet()) ?: emptySet()
    }

    fun setDeviceBlocked(mac: String, blocked: Boolean) {
        val current = getBlockedMacs().toMutableSet()
        if (blocked) current.add(mac) else current.remove(mac)
        prefs.edit().putStringSet("blocked_macs", current).apply()
    }
}
