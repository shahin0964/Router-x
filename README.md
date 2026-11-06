# ROUTER X 🌐⚡
> **One App. Complete Control.**  
> **Universal Router Management | Local • Remote • Secure**

Package: `com.router.x`  
Style: **Cyberpunk + Premium Dark + Neon Blue/Cyan**  
Built with **100% Kotlin & Jetpack Compose (Latest Architecture)**

---

## 📸 8 Core UI Screens (Pixel-Perfect from Concept)

1. **Splash Screen (`SplashScreen.kt`)**
   - Holographic glowing "X" logo with concentric WiFi broadcast waves.
   - Tagline: *"One App. Complete Control. Universal Router Management (Local • Remote • Secure)"*
   - Central 3D cyber router hologram over digital planetary grid.
   - Real-time progress bar: *"Initializing Network Engine... 92%"*.

2. **Home Dashboard (`HomeScreen.kt`)**
   - Header with quick navigation, logo, and active notification alerts.
   - **Network Health**: 98% circular radial gauge with heartbeat ECG pulse waveform and "Excellent" status badge.
   - **Real-time KPI Metrics**: Download (245 Mbps Cyan), Upload (92 Mbps Magenta), Devices (24 Blue).
   - **Live Traffic Waveform Chart**: Dual smooth cubic Bezier neon curves with vertical gradient fill.
   - **Active Router Card**: TP-Link Archer AX73, IP `192.168.0.1`, Uptime `3d 12h 26m`, live CPU & RAM meters.
   - **Quick Actions**: WiFi, Reboot Router, Devices, More.
   - **Cyber Bottom Navigation Bar** with glowing neon active state.

3. **My Routers (`RoutersScreen.kt`)**
   - Search bar with instant fuzzy filtering.
   - Filter Tabs: `All`, `Local`, `Remote`.
   - Cards for multiple routers (TP-Link Archer AX73, MikroTik hEX S, OpenWrt, UniFi Dream Machine) with live online/connected badges.
   - **"+ Add Router"**: Wizard to connect any router by IP, Model, and Protocol.

4. **Router Control Center (`ControlCenterScreen.kt`)**
   - Hero Active Router status card.
   - Sub-tabs: `Control`, `Configuration`, `Tools`.
   - **2-Column Grid with 8 Core Modules**:
     1. WiFi Settings
     2. LAN & DHCP
     3. Firewall & Security
     4. Routing & VLAN
     5. VPN Management
     6. Bandwidth Control (QoS)
     7. Backup & Restore
     8. Advanced Settings
   - Prominent **"Reboot Router"** red glowing gradient danger button with confirmation modal.

5. **Monitoring (`MonitoringScreen.kt`)**
   - Time-range selector: `1h`, `6h`, `24h`, `7d`.
   - Download, Upload, and Ping (12 ms) metric cards.
   - Interactive live traffic waveform chart.
   - **Router Resources**: 3 animated circular gauges for **CPU (12%)**, **RAM (38%)**, and **Temp (40°C)**.
   - **Network Details**: Connected Devices count, Packet Loss (0.2%), Jitter (1 ms).

6. **Connected Devices (`ConnectedDevicesScreen.kt`)**
   - Filter pills: `All (24)`, `Online (22)`, `Offline (2)`.
   - Device list with distinct device icons (Phone, Laptop, Smart TV, Desktop, Unknown).
   - Vendor details, IP address, MAC address, and status indicators.
   - **Bottom Actions**: `🚫 Block Device` (pushes MAC filter to router) and `✏️ Rename Device`.

7. **Network Map (`NetworkMapScreen.kt`)**
   - Mode toggles: `Topology` vs `Flow`.
   - Interactive Node Diagram:
     - 🌐 **Internet Gateway** (Top cyber globe)
     - Connecting neon pulse branches
     - 📡 **Central Router** (`TP-Link AX73 / 192.168.0.1`) with radar pulse aura
     - Connected Subnodes: **Devices (24)**, **Switch (1)**, **AP (2)**
   - Bottom status pill: *"✓ Network Status: All Systems Normal"*.

8. **Advanced Settings (`AdvancedSettingsScreen.kt`)**
   - Category Tabs: `Wireless`, `Network`, `Security`.
   - WiFi Radio master toggle switch.
   - SSID (2.4GHz) and Password field with visibility toggle.
   - Channel selection dropdown (Auto, 1, 6, 11).
   - Band Steering toggle (Auto-switch 2.4GHz / 5GHz).
   - Transmit Power slider (0% to 100%).
   - Neon gradient **"Apply Changes"** button.

---

## 🛠️ Universal Router Protocol Driver Architecture

Any router can be controlled through ROUTER X via pluggable drivers:

| Router / Protocol | Interface | Supported Features |
| :--- | :--- | :--- |
| **OpenWrt / LEDE** | ubus JSON-RPC (`/ubus`) | HostHints, UCI wireless config, reboot, RAM/CPU stats |
| **MikroTik RouterOS** | RouterOS REST API (`/rest/*`) | System resource, DHCP lease table, interface stats, reboot |
| **TP-Link (Archer / Omada)** | HTTP Web API (`/cgi-bin/luci`) | Session token auth, client list, wireless update, reboot |
| **Ubiquiti UniFi** | UniFi OS REST API | Station stats, AP telemetry, network clients, reboot |
| **UPnP IGD** | SOAP XML (`WANCommonInterfaceConfig`) | Zero-config WAN byte counters, external IP, port forwarding |
| **SNMP v2c/v3** | Standard MIB-II | Interfaces, uptime, traffic octets, ARP neighbor table |
| **Cyberpunk Simulator** | In-Memory Live Engine | Real-time realistic telemetry simulation for offline demo |

---

## 🚀 Building and Running

### Build with Gradle
```bash
./gradlew assembleDebug
```

### Build Release APK
```bash
./gradlew assembleRelease
```
The compiled APK will be at:
`app/build/outputs/apk/debug/app-debug.apk` and `app/build/outputs/apk/release/app-release.apk`.
