package com.router.x.data.model

import kotlinx.serialization.Serializable

enum class RouterProtocol(val displayName: String, val defaultPort: Int) {
    AUTO_DETECT("Auto Detect", 80),
    TPLINK_HTTP("TP-Link HTTP / Omada", 80),
    MIKROTIK_REST("MikroTik RouterOS REST", 443),
    OPENWRT_UBUS("OpenWrt ubus JSON-RPC", 80),
    UNIFI_API("Ubiquiti UniFi OS", 443),
    UPNP_IGD("Universal Plug & Play (UPnP)", 1900),
    SNMP("SNMP v2c/v3", 161),
    SSH_CLI("SSH Linux / DD-WRT Shell", 22),
    SIMULATOR("Cyberpunk Live Simulator", 0)
}

@Serializable
data class RouterCredentials(
    val username: String = "admin",
    val password: String = "",
    val token: String? = null,
    val port: Int = 80,
    val useHttps: Boolean = false
)

@Serializable
data class RouterItem(
    val id: String,
    val name: String,
    val modelName: String,
    val ipAddress: String,
    val firmwareVersion: String = "1.0.3 Build 2025",
    val protocol: RouterProtocol = RouterProtocol.TPLINK_HTTP,
    val isOnline: Boolean = true,
    val isLocal: Boolean = true,
    val uptime: String = "3d 12h 26m",
    val cpuUsage: Int = 12,
    val ramUsage: Int = 38,
    val temperature: Int = 40,
    val credentials: RouterCredentials = RouterCredentials()
)

@Serializable
data class TelemetryData(
    val downloadSpeedMbps: Float = 245f,
    val uploadSpeedMbps: Float = 92f,
    val pingMs: Int = 12,
    val healthPercent: Int = 98,
    val healthStatus: String = "Excellent",
    val connectedDevicesCount: Int = 24,
    val packetLossPercent: Float = 0.2f,
    val jitterMs: Int = 1,
    val cpuUsage: Int = 12,
    val ramUsage: Int = 38,
    val temperature: Int = 40,
    val downloadHistory: List<Float> = listOf(45f, 65f, 110f, 95f, 140f, 125f, 150f, 130f, 145f),
    val uploadHistory: List<Float> = listOf(20f, 35f, 60f, 45f, 85f, 70f, 92f, 65f, 75f)
)

enum class DeviceType {
    PHONE,
    LAPTOP,
    TV,
    DESKTOP,
    SWITCH,
    ACCESS_POINT,
    UNKNOWN
}

@Serializable
data class DeviceItem(
    val id: String,
    val name: String,
    val ipAddress: String,
    val macAddress: String,
    val vendor: String,
    val deviceType: DeviceType,
    val isOnline: Boolean = true,
    val isBlocked: Boolean = false
)

@Serializable
data class WifiConfig(
    val isEnabled: Boolean = true,
    val ssid24G: String = "ROUTER_X_2G",
    val password: String = "cyberpunk2026",
    val channel: String = "Auto",
    val isBandSteeringEnabled: Boolean = true,
    val transmitPowerPercent: Int = 100,
    val securityMode: String = "WPA3/WPA2-Personal"
)

@Serializable
data class TopologyNode(
    val id: String,
    val name: String,
    val subtitle: String,
    val iconType: String, // "INTERNET", "ROUTER", "SWITCH", "AP", "DEVICES"
    val count: Int = 1,
    val statusText: String = "Normal"
)
