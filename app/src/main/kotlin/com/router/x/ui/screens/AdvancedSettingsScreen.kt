package com.router.x.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.router.x.data.model.WifiConfig
import com.router.x.navigation.Screen
import com.router.x.theme.CyberCardBorder
import com.router.x.theme.CyberSurface
import com.router.x.theme.CyberSurfaceVariant
import com.router.x.theme.NeonCyan
import com.router.x.theme.NeonGreen
import com.router.x.theme.TextMuted
import com.router.x.theme.TextPrimary
import com.router.x.theme.TextSecondary
import com.router.x.ui.components.CyberBackground
import com.router.x.ui.components.CyberOutlinedButton
import com.router.x.ui.components.CyberPrimaryButton
import com.router.x.ui.components.NeonCard
import com.router.x.viewmodel.RouterViewModel
import kotlinx.coroutines.launch

@Composable
fun AdvancedSettingsScreen(
    viewModel: RouterViewModel,
    onNavigate: (Screen) -> Unit
) {
    val selectedRouter by viewModel.selectedRouter.collectAsState()
    val wifiConfig by viewModel.wifiConfig.collectAsState()
    val currentTab by viewModel.settingsTab.collectAsState()
    val isRouterAuth by viewModel.isRouterAuthenticated.collectAsState()

    // Wireless State
    var isWifiEnabled by remember(wifiConfig) { mutableStateOf(wifiConfig.isEnabled) }
    var ssid by remember(wifiConfig) { mutableStateOf(wifiConfig.ssid24G) }
    var ssid5G by remember(wifiConfig) { mutableStateOf(if (wifiConfig.ssid24G.contains("5G")) wifiConfig.ssid24G else "${wifiConfig.ssid24G}_5G") }
    var password by remember(wifiConfig) { mutableStateOf(wifiConfig.password) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var channel by remember(wifiConfig) { mutableStateOf(wifiConfig.channel) }
    var isBandSteeringEnabled by remember(wifiConfig) { mutableStateOf(wifiConfig.isBandSteeringEnabled) }
    var transmitPower by remember(wifiConfig) { mutableFloatStateOf(wifiConfig.transmitPowerPercent.toFloat()) }
    var expandedChannelDropdown by remember { mutableStateOf(false) }

    // Network State
    var wanType by remember { mutableStateOf("DHCP (Dynamic IP)") }
    var expandedWanDropdown by remember { mutableStateOf(false) }
    var pppoeUsername by remember { mutableStateOf("") }
    var pppoePassword by remember { mutableStateOf("") }
    var gatewayIp by remember(selectedRouter) { mutableStateOf(selectedRouter.ipAddress) }
    var subnetMask by remember { mutableStateOf("255.255.255.0") }
    var primaryDns by remember { mutableStateOf("8.8.8.8") }
    var secondaryDns by remember { mutableStateOf("1.1.1.1") }
    var isDhcpServerEnabled by remember { mutableStateOf(true) }
    var dhcpRangeStart by remember(selectedRouter) { mutableStateOf(selectedRouter.ipAddress.substringBeforeLast(".") + ".100") }
    var dhcpRangeEnd by remember(selectedRouter) { mutableStateOf(selectedRouter.ipAddress.substringBeforeLast(".") + ".200") }

    // Security State
    var adminUsername by remember(selectedRouter) { mutableStateOf(selectedRouter.credentials.username.ifEmpty { "admin" }) }
    var adminCurrentPassword by remember { mutableStateOf("") }
    var adminNewPassword by remember { mutableStateOf("") }
    var isFirewallEnabled by remember { mutableStateOf(true) }
    var isDosProtectionEnabled by remember { mutableStateOf(true) }
    var isWpsEnabled by remember { mutableStateOf(true) }
    var isRemoteManagementEnabled by remember { mutableStateOf(false) }
    var isBlockWanPingEnabled by remember { mutableStateOf(true) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    CyberBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
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
                        text = "Advanced Settings",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.size(48.dp))
                }

                // Interactive Tabs: Wireless, Network, Security
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    listOf("Wireless", "Network", "Security").forEach { tab ->
                        val isSelected = currentTab == tab
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { viewModel.settingsTab.value = tab }
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

                Spacer(modifier = Modifier.height(16.dp))

                // TAB 1: WIRELESS CONFIGURATION
                if (currentTab == "Wireless") {
                    NeonCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "WiFi Configuration",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )

                                Switch(
                                    checked = isWifiEnabled,
                                    onCheckedChange = { isWifiEnabled = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = TextPrimary,
                                        checkedTrackColor = NeonGreen,
                                        uncheckedThumbColor = TextMuted,
                                        uncheckedTrackColor = CyberSurfaceVariant
                                    )
                                )
                            }

                            // 2.4GHz SSID Field
                            Column {
                                Text(text = "Primary SSID (2.4GHz)", fontSize = 12.sp, color = TextSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = ssid,
                                    onValueChange = { ssid = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = NeonCyan,
                                        unfocusedBorderColor = CyberCardBorder
                                    )
                                )
                            }

                            // 5GHz SSID Field
                            Column {
                                Text(text = "High-Speed SSID (5GHz)", fontSize = 12.sp, color = TextSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = ssid5G,
                                    onValueChange = { ssid5G = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = NeonCyan,
                                        unfocusedBorderColor = CyberCardBorder
                                    )
                                )
                            }

                            // Password Field
                            Column {
                                Text(text = "WiFi Password", fontSize = 12.sp, color = TextSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    placeholder = { Text("Enter WiFi password", color = TextMuted) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    trailingIcon = {
                                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                            Icon(
                                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                contentDescription = null,
                                                tint = TextMuted
                                            )
                                        }
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = NeonCyan,
                                        unfocusedBorderColor = CyberCardBorder
                                    )
                                )
                            }

                            // Channel Dropdown
                            Column {
                                Text(text = "WiFi Channel", fontSize = 12.sp, color = TextSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box {
                                    OutlinedTextField(
                                        value = channel,
                                        onValueChange = {},
                                        readOnly = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { expandedChannelDropdown = true },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary,
                                            focusedBorderColor = NeonCyan,
                                            unfocusedBorderColor = CyberCardBorder
                                        )
                                    )
                                    DropdownMenu(
                                        expanded = expandedChannelDropdown,
                                        onDismissRequest = { expandedChannelDropdown = false },
                                        containerColor = CyberSurface
                                    ) {
                                        listOf("Auto", "1 (2.412 GHz)", "6 (2.437 GHz)", "11 (2.462 GHz)", "36 (5.180 GHz)", "44 (5.220 GHz)", "149 (5.745 GHz)").forEach { ch ->
                                            DropdownMenuItem(
                                                text = { Text(ch, color = TextPrimary) },
                                                onClick = {
                                                    channel = ch.substringBefore(" ")
                                                    expandedChannelDropdown = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            // Band Steering Switch
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "Band Steering (Smart Connect)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                    Text(text = "Auto-switch between 2.4G & 5G", fontSize = 11.sp, color = TextMuted)
                                }
                                Switch(
                                    checked = isBandSteeringEnabled,
                                    onCheckedChange = { isBandSteeringEnabled = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = TextPrimary,
                                        checkedTrackColor = NeonGreen,
                                        uncheckedThumbColor = TextMuted,
                                        uncheckedTrackColor = CyberSurfaceVariant
                                    )
                                )
                            }

                            // Transmit Power Slider
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Transmit Power", fontSize = 12.sp, color = TextSecondary)
                                    Text(text = "${transmitPower.toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Slider(
                                    value = transmitPower,
                                    onValueChange = { transmitPower = it },
                                    valueRange = 10f..100f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = NeonCyan,
                                        activeTrackColor = NeonCyan,
                                        inactiveTrackColor = CyberSurfaceVariant
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    CyberPrimaryButton(
                        text = "Apply WiFi Changes",
                        icon = Icons.Default.Check,
                        onClick = {
                            val newConfig = WifiConfig(
                                isEnabled = isWifiEnabled,
                                ssid24G = ssid,
                                password = password,
                                channel = channel,
                                isBandSteeringEnabled = isBandSteeringEnabled,
                                transmitPowerPercent = transmitPower.toInt()
                            )
                            viewModel.updateWifiConfig(newConfig) { success ->
                                scope.launch {
                                    snackbarHostState.showSnackbar("WiFi configuration applied successfully!")
                                }
                            }
                        }
                    )
                }

                // TAB 2: NETWORK (WAN, LAN, DHCP, DNS)
                if (currentTab == "Network") {
                    NeonCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Text(
                                text = "WAN / Internet Connection",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            // WAN Connection Type
                            Column {
                                Text(text = "Connection Type", fontSize = 12.sp, color = TextSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box {
                                    OutlinedTextField(
                                        value = wanType,
                                        onValueChange = {},
                                        readOnly = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { expandedWanDropdown = true },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary,
                                            focusedBorderColor = NeonCyan,
                                            unfocusedBorderColor = CyberCardBorder
                                        )
                                    )
                                    DropdownMenu(
                                        expanded = expandedWanDropdown,
                                        onDismissRequest = { expandedWanDropdown = false },
                                        containerColor = CyberSurface
                                    ) {
                                        listOf("DHCP (Dynamic IP)", "PPPoE Dial-up", "Static IP").forEach { mode ->
                                            DropdownMenuItem(
                                                text = { Text(mode, color = TextPrimary) },
                                                onClick = {
                                                    wanType = mode
                                                    expandedWanDropdown = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            if (wanType.contains("PPPoE")) {
                                Column {
                                    Text(text = "ISP Username", fontSize = 12.sp, color = TextSecondary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = pppoeUsername,
                                        onValueChange = { pppoeUsername = it },
                                        placeholder = { Text("ISP account login", color = TextMuted) },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary,
                                            focusedBorderColor = NeonCyan,
                                            unfocusedBorderColor = CyberCardBorder
                                        )
                                    )
                                }

                                Column {
                                    Text(text = "ISP Password", fontSize = 12.sp, color = TextSecondary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = pppoePassword,
                                        onValueChange = { pppoePassword = it },
                                        placeholder = { Text("ISP account password", color = TextMuted) },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true,
                                        visualTransformation = PasswordVisualTransformation(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary,
                                            focusedBorderColor = NeonCyan,
                                            unfocusedBorderColor = CyberCardBorder
                                        )
                                    )
                                }
                            }

                            // DNS Servers
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Primary DNS", fontSize = 12.sp, color = TextSecondary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = primaryDns,
                                        onValueChange = { primaryDns = it },
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary,
                                            focusedBorderColor = NeonCyan,
                                            unfocusedBorderColor = CyberCardBorder
                                        )
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Secondary DNS", fontSize = 12.sp, color = TextSecondary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = secondaryDns,
                                        onValueChange = { secondaryDns = it },
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary,
                                            focusedBorderColor = NeonCyan,
                                            unfocusedBorderColor = CyberCardBorder
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    NeonCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Text(
                                text = "LAN & DHCP Server",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            // Router Gateway IP
                            Column {
                                Text(text = "Gateway Router IP Address", fontSize = 12.sp, color = TextSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = gatewayIp,
                                    onValueChange = { gatewayIp = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = NeonCyan,
                                        unfocusedBorderColor = CyberCardBorder
                                    )
                                )
                            }

                            // Subnet Mask
                            Column {
                                Text(text = "Subnet Mask", fontSize = 12.sp, color = TextSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = subnetMask,
                                    onValueChange = { subnetMask = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = NeonCyan,
                                        unfocusedBorderColor = CyberCardBorder
                                    )
                                )
                            }

                            // DHCP Server Toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "DHCP Server", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                    Text(text = "Auto-assign IPs to devices", fontSize = 11.sp, color = TextMuted)
                                }
                                Switch(
                                    checked = isDhcpServerEnabled,
                                    onCheckedChange = { isDhcpServerEnabled = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = TextPrimary,
                                        checkedTrackColor = NeonGreen,
                                        uncheckedThumbColor = TextMuted,
                                        uncheckedTrackColor = CyberSurfaceVariant
                                    )
                                )
                            }

                            // DHCP Pool Range
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Pool Start", fontSize = 12.sp, color = TextSecondary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = dhcpRangeStart,
                                        onValueChange = { dhcpRangeStart = it },
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary,
                                            focusedBorderColor = NeonCyan,
                                            unfocusedBorderColor = CyberCardBorder
                                        )
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Pool End", fontSize = 12.sp, color = TextSecondary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = dhcpRangeEnd,
                                        onValueChange = { dhcpRangeEnd = it },
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary,
                                            focusedBorderColor = NeonCyan,
                                            unfocusedBorderColor = CyberCardBorder
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    CyberPrimaryButton(
                        text = "Apply Network Settings",
                        icon = Icons.Default.Check,
                        onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Network & DHCP configuration updated successfully!")
                            }
                        }
                    )
                }

                // TAB 3: SECURITY (ADMIN PASSWORD, FIREWALL, DOS, WPS)
                if (currentTab == "Security") {
                    NeonCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Text(
                                text = "Router Admin Account",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            // Admin Username
                            Column {
                                Text(text = "Admin Username", fontSize = 12.sp, color = TextSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = adminUsername,
                                    onValueChange = { adminUsername = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = NeonCyan,
                                        unfocusedBorderColor = CyberCardBorder
                                    )
                                )
                            }

                            // Current Password
                            Column {
                                Text(text = "Current Admin Password", fontSize = 12.sp, color = TextSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = adminCurrentPassword,
                                    onValueChange = { adminCurrentPassword = it },
                                    placeholder = { Text("Default: admin", color = TextMuted) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    visualTransformation = PasswordVisualTransformation(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = NeonCyan,
                                        unfocusedBorderColor = CyberCardBorder
                                    )
                                )
                            }

                            // New Password
                            Column {
                                Text(text = "New Admin Password", fontSize = 12.sp, color = TextSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = adminNewPassword,
                                    onValueChange = { adminNewPassword = it },
                                    placeholder = { Text("Min 8 characters", color = TextMuted) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    visualTransformation = PasswordVisualTransformation(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = NeonCyan,
                                        unfocusedBorderColor = CyberCardBorder
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    NeonCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Text(
                                text = "Threat Defense & Access Control",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            // SPI Firewall
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "SPI Firewall Protection", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                    Text(text = "Inspect all incoming packets", fontSize = 11.sp, color = TextMuted)
                                }
                                Switch(
                                    checked = isFirewallEnabled,
                                    onCheckedChange = { isFirewallEnabled = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = TextPrimary,
                                        checkedTrackColor = NeonGreen,
                                        uncheckedThumbColor = TextMuted,
                                        uncheckedTrackColor = CyberSurfaceVariant
                                    )
                                )
                            }

                            // DoS Defense
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "DoS Attack Prevention", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                    Text(text = "Block SYN/ICMP Flood attacks", fontSize = 11.sp, color = TextMuted)
                                }
                                Switch(
                                    checked = isDosProtectionEnabled,
                                    onCheckedChange = { isDosProtectionEnabled = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = TextPrimary,
                                        checkedTrackColor = NeonGreen,
                                        uncheckedThumbColor = TextMuted,
                                        uncheckedTrackColor = CyberSurfaceVariant
                                    )
                                )
                            }

                            // Block WAN Ping
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Block WAN Ping", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                    Text(text = "Make router invisible from internet", fontSize = 11.sp, color = TextMuted)
                                }
                                Switch(
                                    checked = isBlockWanPingEnabled,
                                    onCheckedChange = { isBlockWanPingEnabled = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = TextPrimary,
                                        checkedTrackColor = NeonGreen,
                                        uncheckedThumbColor = TextMuted,
                                        uncheckedTrackColor = CyberSurfaceVariant
                                    )
                                )
                            }

                            // WPS Status
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "WPS (Wi-Fi Protected Setup)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                    Text(text = "Enable push-button device pairing", fontSize = 11.sp, color = TextMuted)
                                }
                                Switch(
                                    checked = isWpsEnabled,
                                    onCheckedChange = { isWpsEnabled = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = TextPrimary,
                                        checkedTrackColor = NeonGreen,
                                        uncheckedThumbColor = TextMuted,
                                        uncheckedTrackColor = CyberSurfaceVariant
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    CyberPrimaryButton(
                        text = "Apply Security Shield",
                        icon = Icons.Default.Check,
                        onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Security rules and admin credentials updated!")
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Security Notice Card
                NeonCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigate(Screen.RouterLogin) },
                    backgroundColor = CyberSurfaceVariant,
                    borderColor = if (isRouterAuth) NeonGreen else CyberCardBorder
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (isRouterAuth) Icons.Default.Check else Icons.Default.Security,
                                contentDescription = null,
                                tint = if (isRouterAuth) NeonGreen else NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isRouterAuth) "Connected & Authenticated" else "Connected Gateway (${selectedRouter.ipAddress})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isRouterAuth) NeonGreen else TextPrimary
                                )
                                Text(
                                    text = if (isRouterAuth) "Master admin session active" else "Tap here to login with admin password",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }
                        }
                        if (!isRouterAuth) {
                            Text(
                                text = "Login →",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Router Web Console Direct Access
                CyberOutlinedButton(
                    text = "Open Router Web Console",
                    icon = Icons.Default.Language,
                    onClick = { onNavigate(Screen.WebConsole) },
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = NeonCyan
                )

                Spacer(modifier = Modifier.height(20.dp))
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )
        }
    }
}
