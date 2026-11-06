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
    val wifiConfig by viewModel.wifiConfig.collectAsState()
    val currentTab by viewModel.settingsTab.collectAsState()

    var isWifiEnabled by remember(wifiConfig) { mutableStateOf(wifiConfig.isEnabled) }
    var ssid by remember(wifiConfig) { mutableStateOf(wifiConfig.ssid24G) }
    var password by remember(wifiConfig) { mutableStateOf(wifiConfig.password) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var channel by remember(wifiConfig) { mutableStateOf(wifiConfig.channel) }
    var isBandSteeringEnabled by remember(wifiConfig) { mutableStateOf(wifiConfig.isBandSteeringEnabled) }
    var transmitPower by remember(wifiConfig) { mutableFloatStateOf(wifiConfig.transmitPowerPercent.toFloat()) }

    var expandedChannelDropdown by remember { mutableStateOf(false) }
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

                // Tabs: Wireless, Network, Security
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
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) NeonCyan else TextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            if (isSelected) {
                                HorizontalDivider(
                                    modifier = Modifier.width(40.dp),
                                    thickness = 2.dp,
                                    color = NeonCyan
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Wireless Configuration Section
                NeonCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        // Title + Switch
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

                        // SSID Field
                        Column {
                            Text(text = "SSID (2.4GHz)", fontSize = 12.sp, color = TextSecondary)
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

                        // Password Field
                        Column {
                            Text(text = "Password", fontSize = 12.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
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

                        // Channel Field
                        Column {
                            Text(text = "Channel", fontSize = 12.sp, color = TextSecondary)
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
                                    onDismissRequest = { expandedChannelDropdown = false }
                                ) {
                                    listOf("Auto", "1", "6", "11").forEach { ch ->
                                        DropdownMenuItem(
                                            text = { Text(ch) },
                                            onClick = {
                                                channel = ch
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
                            Text(text = "Band Steering", fontSize = 13.sp, color = TextPrimary)
                            Switch(
                                checked = isBandSteeringEnabled,
                                onCheckedChange = { isBandSteeringEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = TextPrimary,
                                    checkedTrackColor = NeonGreen
                                )
                            )
                        }

                        // Transmit Power Slider
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Transmit Power", fontSize = 13.sp, color = TextPrimary)
                                Text(
                                    text = "${transmitPower.toInt()}%",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonCyan
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Slider(
                                value = transmitPower,
                                onValueChange = { transmitPower = it },
                                valueRange = 0f..100f,
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

                // Apply Changes Button
                CyberPrimaryButton(
                    text = "Apply Changes",
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
                                snackbarHostState.showSnackbar(
                                    if (success) "WiFi configuration applied successfully!" else "Failed to apply changes."
                                )
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Security Notice Card
                NeonCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = CyberSurfaceVariant
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Changes will take effect in a few seconds...",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
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
