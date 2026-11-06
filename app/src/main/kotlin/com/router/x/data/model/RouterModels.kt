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
    SSH_CLI("SSH Linux / DD-WRT Shell", 22)
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
    val firmwareVersion: String = "Live Network",
    val protocol: RouterProtocol = RouterProtocol.AUTO_DETECT,
    val isOnline: Boolean = true,
    val isLocal: Boolean = true,
    val uptime: String = "Connected",
    val cpuUsage: Int = 0,
    val ramUsage: Int = 0,
    val temperature: Int = 38,
    val credentials: RouterCredentials = RouterCredentials()
)

@Serializable
data class TelemetryData(
    val downloadSpeedMbps: Float = 0f,
    val uploadSpeedMbps: Float = 0f,
    val pingMs: Int = 0,
    val healthPercent: Int = 100,
    val healthStatus: String = "Scanning...",
    val connectedDevicesCount: Int = 0,
    val packetLossPercent: Float = 0.0f,
    val jitterMs: Int = 1,
    val cpuUsage: Int = 0,
    val ramUsage: Int = 0,
    val temperature: Int = 38,
    val downloadHistory: List<Float> = emptyList(),
    val uploadHistory: List<Float> = emptyList()
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
