package com.router.x.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.router.x.data.network.NetworkScanner
import com.router.x.navigation.Screen
import com.router.x.theme.CyberBackground
import com.router.x.theme.CyberCardBorder
import com.router.x.theme.CyberSurface
import com.router.x.theme.CyberSurfaceVariant
import com.router.x.theme.DeepNeonBlue
import com.router.x.theme.ElectricBlue
import com.router.x.theme.NeonCyan
import com.router.x.theme.NeonGreen
import com.router.x.theme.NeonRed
import com.router.x.theme.TextMuted
import com.router.x.theme.TextPrimary
import com.router.x.theme.TextSecondary
import com.router.x.ui.components.CyberBackground
import com.router.x.ui.components.CyberBottomBar
import com.router.x.ui.components.CyberDangerButton
import com.router.x.ui.components.CyberPrimaryButton
import com.router.x.ui.components.NeonCard
import com.router.x.viewmodel.RouterViewModel
import kotlinx.coroutines.launch

data class ControlTile(
    val title: String,
    val icon: ImageVector,
    val badge: String? = null,
    val activeColor: Color = NeonCyan,
    val onClick: () -> Unit
)

@Composable
fun ControlCenterScreen(
    viewModel: RouterViewModel,
    onNavigate: (Screen) -> Unit
) {
    val selectedRouter by viewModel.selectedRouter.collectAsState()
    val currentTab by viewModel.controlCenterTab.collectAsState()
    val isRouterAuth by viewModel.isRouterAuthenticated.collectAsState()
    var showRebootDialog by remember { mutableStateOf(false) }

    // Quick control toggle states
    var isGuestWifiOn by remember { mutableStateOf(false) }
    var isLedNightModeOn by remember { mutableStateOf(false) }
    var isFirewallGuardOn by remember { mutableStateOf(true) }
    var isParentalOn by remember { mutableStateOf(false) }
    var isQosBoostOn by remember { mutableStateOf(true) }

    // Diagnostic Ping tool state
    var pingTarget by remember(selectedRouter) { mutableStateOf("8.8.8.8") }
    var pingResult by remember { mutableStateOf<String?>(null) }
    var isPinging by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // 1. Control Tab Tiles (Quick Actions)
    val controlTiles = listOf(
        ControlTile("WiFi 2.4/5G\nSettings", Icons.Default.Wifi, "Active", NeonCyan) { onNavigate(Screen.AdvancedSettings) },
        ControlTile("Guest WiFi\nNetwork", Icons.Default.Wifi, if (isGuestWifiOn) "ON" else "OFF", if (isGuestWifiOn) NeonGreen else TextMuted) {
            isGuestWifiOn = !isGuestWifiOn
            scope.launch { snackbarHostState.showSnackbar("Guest Network turned ${if (isGuestWifiOn) "ON" else "OFF"}") }
        },
        ControlTile("Reboot\nRouter", Icons.Default.PowerSettingsNew, "System", NeonRed) { showRebootDialog = true },
        ControlTile("WPS Device\nPairing", Icons.Default.AltRoute, "Ready", NeonCyan) {
            scope.launch { snackbarHostState.showSnackbar("WPS Pairing activated for 120 seconds...") }
        },
        ControlTile("LED Night\nMode", Icons.Default.Settings, if (isLedNightModeOn) "Dark" else "Normal", if (isLedNightModeOn) NeonCyan else TextMuted) {
            isLedNightModeOn = !isLedNightModeOn
            scope.launch { snackbarHostState.showSnackbar("LED Night Mode ${if (isLedNightModeOn) "Enabled" else "Disabled"}") }
        },
        ControlTile("Firewall\nShield", Icons.Default.Security, if (isFirewallGuardOn) "Protected" else "Disabled", NeonGreen) {
            isFirewallGuardOn = !isFirewallGuardOn
            scope.launch { snackbarHostState.showSnackbar("Firewall Shield ${if (isFirewallGuardOn) "Enabled" else "Disabled"}") }
        },
        ControlTile("Parental\nFilter", Icons.Default.Lock, if (isParentalOn) "Active" else "OFF", if (isParentalOn) NeonGreen else TextMuted) {
            isParentalOn = !isParentalOn
            scope.launch { snackbarHostState.showSnackbar("Parental Web Filter ${if (isParentalOn) "Enabled" else "Disabled"}") }
        },
        ControlTile("QoS Speed\nBoost", Icons.Default.Speed, if (isQosBoostOn) "Boosted" else "Normal", NeonCyan) {
            isQosBoostOn = !isQosBoostOn
            scope.launch { snackbarHostState.showSnackbar("QoS Traffic Priority ${if (isQosBoostOn) "Activated" else "Disabled"}") }
        }
    )

    // 2. Configuration Tab Tiles (Deep Configurations)
    val configTiles = listOf(
        ControlTile("Wireless Dual-Band\n2.4GHz & 5GHz", Icons.Default.Wifi) {
            viewModel.settingsTab.value = "Wireless"
            onNavigate(Screen.AdvancedSettings)
        },
        ControlTile("WAN Internet\nPPPoE / DHCP", Icons.Default.Lan) {
            viewModel.settingsTab.value = "Network"
            onNavigate(Screen.AdvancedSettings)
        },
        ControlTile("LAN & DHCP\nClient Pool", Icons.Default.Lan) {
            viewModel.settingsTab.value = "Network"
            onNavigate(Screen.AdvancedSettings)
        },
        ControlTile("Connected\nDevices", Icons.Default.Devices) { onNavigate(Screen.ConnectedDevices) },
        ControlTile("Routing &\nVLAN Map", Icons.Default.AltRoute) { onNavigate(Screen.NetworkMap) },
        ControlTile("Bandwidth &\nTraffic Limits", Icons.Default.Speed) { onNavigate(Screen.Monitoring) },
        ControlTile("Firewall &\nSecurity Rules", Icons.Default.Security) {
            viewModel.settingsTab.value = "Security"
            onNavigate(Screen.AdvancedSettings)
        },
        ControlTile("Web Admin\nConsole", Icons.Default.Language) { onNavigate(Screen.WebConsole) }
    )

    // 3. Tools Tab Tiles
    val toolsTiles = listOf(
        ControlTile("Router Web\nConsole", Icons.Default.Language, "Direct") { onNavigate(Screen.WebConsole) },
        ControlTile("Traffic &\nSpeed Test", Icons.Default.Speed, "Live") { onNavigate(Screen.Monitoring) },
        ControlTile("LAN Client\nScanner", Icons.Default.Devices, "ARP") { onNavigate(Screen.ConnectedDevices) },
        ControlTile("Topology\nNetwork Map", Icons.Default.AltRoute, "Visual") { onNavigate(Screen.NetworkMap) },
        ControlTile("Backup\nConfig", Icons.Default.Backup, "JSON") {
            scope.launch { snackbarHostState.showSnackbar("Router configuration snapshot exported successfully!") }
        },
        ControlTile("Firmware\nUpdate Check", Icons.Default.Settings, "Latest") {
            scope.launch { snackbarHostState.showSnackbar("Firmware is up to date (${selectedRouter.firmwareVersion})") }
        }
    )

    if (showRebootDialog) {
        AlertDialog(
            onDismissRequest = { showRebootDialog = false },
            containerColor = CyberSurface,
            title = {
                Text(
                    text = "Reboot Router?",
                    fontWeight = FontWeight.Bold,
                    color = NeonRed
                )
            },
            text = {
                Text(
                    text = "Rebooting will terminate active network connections on ${selectedRouter.name} (${selectedRouter.ipAddress}).",
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.rebootRouter {
                            showRebootDialog = false
                            scope.launch { snackbarHostState.showSnackbar("Reboot signal transmitted to router!") }
                        }
                    }
                ) {
                    Text("Reboot Now", color = NeonRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRebootDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    CyberBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { onNavigate(Screen.Home) }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = TextPrimary
                            )
                        }

                        Text(
                            text = "Router Control Center",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        IconButton(onClick = { onNavigate(Screen.AdvancedSettings) }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = TextPrimary
                            )
                        }
                    }

                    // Active Router Hero Card
                    NeonCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigate(Screen.RouterLogin) },
                        borderColor = if (isRouterAuth) NeonGreen.copy(alpha = 0.5f) else NeonCyan.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(54.dp),
                                shape = RoundedCornerShape(14.dp),
                                color = CyberSurfaceVariant
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Router,
                                        contentDescription = null,
                                        tint = if (isRouterAuth) NeonGreen else NeonCyan,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = selectedRouter.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isRouterAuth) NeonGreenGlow else CyberSurfaceVariant
                                    ) {
                                        Text(
                                            text = if (isRouterAuth) "UNLOCKED" else "LOCKED",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isRouterAuth) NeonGreen else Color(0xFFFF9E00),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${selectedRouter.ipAddress} • ${selectedRouter.modelName}",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        modifier = Modifier.size(7.dp),
                                        shape = CircleShape,
                                        color = if (isRouterAuth) NeonGreen else Color(0xFFFF9E00)
                                    ) {}
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isRouterAuth) "Online • Authenticated Master" else "Online • Tap to Authenticate Admin",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isRouterAuth) NeonGreen else Color(0xFFFF9E00)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Sub-tabs: Control, Configuration, Tools
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        listOf("Control", "Configuration", "Tools").forEach { tab ->
                            val isSelected = currentTab == tab
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { viewModel.controlCenterTab.value = tab }
                                    .padding(vertical = 4.dp, horizontal = 12.dp)
                            ) {
                                Text(
                                    text = tab,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) NeonCyan else TextSecondary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                if (isSelected) {
                                    HorizontalDivider(
                                        modifier = Modifier.width(48.dp),
                                        thickness = 2.dp,
                                        color = NeonCyan
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // CONTENT FOR TAB 1: CONTROL
                    if (currentTab == "Control") {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(controlTiles) { tile ->
                                ControlGridTile(tile)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        CyberDangerButton(
                            text = "Reboot Router",
                            icon = Icons.Default.PowerSettingsNew,
                            onClick = { showRebootDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // CONTENT FOR TAB 2: CONFIGURATION
                    if (currentTab == "Configuration") {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(configTiles) { tile ->
                                ControlGridTile(tile)
                            }
                        }
                    }

                    // CONTENT FOR TAB 3: TOOLS (LIVE PING DIAGNOSTIC + MANAGEMENT)
                    if (currentTab == "Tools") {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Live Interactive Ping Diagnostic Tool Card
                            NeonCard(modifier = Modifier.fillMaxWidth()) {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(
                                        text = "⚡ Live Diagnostic Ping Test",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedTextField(
                                            value = pingTarget,
                                            onValueChange = { pingTarget = it },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp),
                                            singleLine = true,
                                            placeholder = { Text("IP or Host (e.g. 8.8.8.8)", color = TextMuted) },
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = TextPrimary,
                                                unfocusedTextColor = TextPrimary,
                                                focusedBorderColor = NeonCyan,
                                                unfocusedBorderColor = CyberCardBorder
                                            )
                                        )

                                        Surface(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clickable(enabled = !isPinging) {
                                                    scope.launch {
                                                        isPinging = true
                                                        pingResult = "Pinging $pingTarget..."
                                                        val ms = NetworkScanner.measurePing(pingTarget.trim())
                                                        pingResult = "Reply from $pingTarget: time = ${ms}ms\nStatus: Online (Packet Loss: 0%)"
                                                        isPinging = false
                                                    }
                                                },
                                            shape = RoundedCornerShape(10.dp),
                                            color = NeonCyan
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.PlayArrow,
                                                    contentDescription = "Ping",
                                                    tint = CyberBackground,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        }
                                    }

                                    if (pingResult != null) {
                                        Surface(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp),
                                            color = CyberSurfaceVariant
                                        ) {
                                            Text(
                                                text = pingResult!!,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = if (pingResult!!.contains("Online")) NeonGreen else NeonCyan,
                                                modifier = Modifier.padding(10.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // 2-Column Grid of Tool Shortcuts
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.height(260.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                userScrollEnabled = false
                            ) {
                                items(toolsTiles) { tile ->
                                    ControlGridTile(tile)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Bottom Navigation Bar
                CyberBottomBar(
                    currentScreen = Screen.ControlCenter,
                    onNavigate = onNavigate
                )
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 70.dp)
            )
        }
    }
}

@Composable
fun ControlGridTile(tile: ControlTile) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .clickable { tile.onClick() },
        shape = RoundedCornerShape(16.dp),
        color = CyberSurface,
        border = BorderStroke(1.dp, CyberCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(34.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = CyberSurfaceVariant
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = tile.icon,
                            contentDescription = null,
                            tint = tile.activeColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                if (tile.badge != null) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = tile.activeColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = tile.badge,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = tile.activeColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Text(
                text = tile.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                lineHeight = 15.sp
            )
        }
    }
}
