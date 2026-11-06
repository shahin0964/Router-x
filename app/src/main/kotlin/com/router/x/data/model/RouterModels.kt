package com.router.x.data.model

import kotlinx.serialization.Serializable

enum class RouterProtocol(val displayName: String, val defaultPort: Int, val defaultIp: String) {
    AUTO_DETECT("Auto Detect Gateway", 80, "192.168.1.1"),
    TPLINK_HTTP("TP-Link Archer / TL-WR", 80, "192.168.0.1"),
    TENDA_HTTP("Tenda AC / F / TX Series", 80, "192.168.0.1"),
    ASUS_WRT("ASUS RT / TUF / ROG AsusWRT", 80, "192.168.50.1"),
    BDCOM_GPON("BDCOM GPON/EPON Optical Router", 80, "192.168.1.1"),
    NETGEAR_GENIE("Netgear Nighthawk / Genie", 80, "192.168.1.1"),
    DLINK_HNAP("D-Link DIR / EXO Series", 80, "192.168.0.1"),
    HUAWEI_ZTE("Huawei / ZTE Optical Gateway", 80, "192.168.100.1"),
    MIKROTIK_REST("MikroTik RouterOS", 8728, "192.168.88.1"),
    OPENWRT_UBUS("OpenWrt / LuCI Smart Router", 80, "192.168.1.1"),
    UNIFI_API("Ubiquiti UniFi OS", 443, "192.168.1.1"),
    UPNP_IGD("Universal Plug & Play (UPnP)", 1900, "192.168.1.1"),
    SNMP("SNMP v2c/v3 Management", 161, "192.168.1.1"),
    SSH_CLI("SSH Linux / DD-WRT Shell", 22, "192.168.1.1")
}

@Serializable
data class NetworkConfig(
    val wanType: String = "DHCP (Dynamic IP)",
    val gatewayIp: String = "192.168.1.1",
    val subnetMask: String = "255.255.255.0",
    val primaryDns: String = "8.8.8.8",
    val secondaryDns: String = "1.1.1.1",
    val dhcpEnabled: Boolean = true,
    val dhcpStartIp: String = "192.168.1.100",
    val dhcpEndIp: String = "192.168.1.200",
    val leaseTimeMinutes: Int = 120
)

@Serializable
data class SecurityConfig(
    val adminUsername: String = "admin",
    val firewallEnabled: Boolean = true,
    val dosProtectionEnabled: Boolean = true,
    val wpsEnabled: Boolean = true,
    val remoteManagementEnabled: Boolean = false,
    val remotePort: Int = 8080,
    val blockWanPing: Boolean = true
)

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
    val isAuthenticated: Boolean = false,
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
