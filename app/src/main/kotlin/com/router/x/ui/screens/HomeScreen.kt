package com.router.x.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.router.x.R
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.router.x.navigation.Screen
import com.router.x.theme.CyberBackground
import com.router.x.theme.CyberCardBorder
import com.router.x.theme.CyberSurface
import com.router.x.theme.CyberSurfaceVariant
import com.router.x.theme.DeepNeonBlue
import com.router.x.theme.ElectricBlue
import com.router.x.theme.NeonCyan
import com.router.x.theme.NeonGreen
import com.router.x.theme.NeonGreenGlow
import com.router.x.theme.NeonMagenta
import com.router.x.theme.NeonRed
import com.router.x.theme.TextMuted
import com.router.x.theme.TextPrimary
import com.router.x.theme.TextSecondary
import com.router.x.ui.components.CyberBackground
import com.router.x.ui.components.CyberBottomBar
import com.router.x.ui.components.NeonCard
import com.router.x.ui.components.RadialGauge
import com.router.x.ui.components.WaveformChart
import com.router.x.viewmodel.RouterViewModel

@Composable
fun HomeScreen(
    viewModel: RouterViewModel,
    onNavigate: (Screen) -> Unit
) {
    val selectedRouter by viewModel.selectedRouter.collectAsState()
    val telemetry by viewModel.telemetry.collectAsState()
    val devices by viewModel.devices.collectAsState()
    val isRouterAuth by viewModel.isRouterAuthenticated.collectAsState()

    var showRebootDialog by remember { mutableStateOf(false) }

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
                    text = "Are you sure you want to reboot ${selectedRouter.name} (${selectedRouter.ipAddress})? Network traffic will temporarily disconnect.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.rebootRouter { showRebootDialog = false }
                    }
                ) {
                    Text("Reboot", color = NeonRed, fontWeight = FontWeight.Bold)
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
        Column(modifier = Modifier.fillMaxSize()) {
            // Main Scrollable Area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onNavigate(Screen.Routers) }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = TextPrimary
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.app_logo_small),
                            contentDescription = "ROUTER X Logo",
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                        )
                        Text(
                            text = "ROUTER X",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp,
                            color = NeonCyan
                        )
                    }

                    Box {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Alerts",
                                tint = TextPrimary
                            )
                        }
                        Surface(
                            modifier = Modifier
                                .size(8.dp)
                                .align(Alignment.TopEnd)
                                .padding(top = 8.dp, end = 8.dp),
                            shape = CircleShape,
                            color = NeonCyan
                        ) {}
                    }
                }

                // Connected Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = NeonGreenGlow,
                        border = BorderStroke(1.dp, NeonGreen)
                    ) {
                        Text(
                            text = "✓ Connected ✓",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonGreen,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Router Authentication Status Banner
                if (!isRouterAuth) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0x22FF9E00),
                        border = BorderStroke(1.dp, Color(0xFFFF9E00)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigate(Screen.RouterLogin) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color(0xFFFF9E00),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Router Admin Locked",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFF9E00)
                                    )
                                    Text(
                                        text = "Tap to enter password & unlock real controls",
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFF9E00).copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, Color(0xFFFF9E00))
                            ) {
                                Text(
                                    text = "Login",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF9E00),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = NeonGreenGlow,
                        border = BorderStroke(1.dp, NeonGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = NeonGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Router Admin Authenticated",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonGreen
                                )
                            }
                            Text(
                                text = "Logout",
                                fontSize = 11.sp,
                                color = TextMuted,
                                modifier = Modifier.clickable { viewModel.logoutRouter() }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Network Health Card
                NeonCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = CyberCardBorder
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Network Health",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = telemetry.healthStatus,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NeonGreen
                            )
                        }

                        // Circular 98% Gauge
                        RadialGauge(
                            value = telemetry.healthPercent,
                            label = "",
                            color = NeonGreen,
                            size = 80.dp,
                            strokeWidth = 7.dp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Metrics Row (Download, Upload, Devices)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Download
                    MetricCard(
                        title = "Download",
                        value = "${telemetry.downloadSpeedMbps.toInt()} Mbps",
                        icon = Icons.Default.ArrowDownward,
                        accentColor = NeonCyan,
                        modifier = Modifier.weight(1f)
                    )
                    // Upload
                    MetricCard(
                        title = "Upload",
                        value = "${telemetry.uploadSpeedMbps.toInt()} Mbps",
                        icon = Icons.Default.ArrowUpward,
                        accentColor = NeonMagenta,
                        modifier = Modifier.weight(1f)
                    )
                    // Devices
                    MetricCard(
                        title = "Devices",
                        value = "${devices.size}",
                        icon = Icons.Default.Devices,
                        accentColor = ElectricBlue,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.ConnectedDevices) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Live Traffic Chart Card
                NeonCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    WaveformChart(
                        downloadPoints = telemetry.downloadHistory,
                        uploadPoints = telemetry.uploadHistory
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Active Router Card
                NeonCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = if (isRouterAuth) NeonGreen.copy(alpha = 0.5f) else NeonCyan.copy(alpha = 0.4f),
                    onClick = { onNavigate(Screen.RouterLogin) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(52.dp),
                            shape = RoundedCornerShape(12.dp),
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

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = selectedRouter.name,
                                    fontSize = 15.sp,
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
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "⏱ Uptime: ${selectedRouter.uptime}",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "CPU: ${telemetry.cpuUsage}%",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = NeonCyan
                                )
                                Text(
                                    text = "RAM: ${telemetry.ramUsage}%",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = NeonGreen
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    QuickActionButton(
                        title = "WiFi",
                        icon = Icons.Default.Wifi,
                        onClick = {
                            if (isRouterAuth) onNavigate(Screen.AdvancedSettings)
                            else onNavigate(Screen.RouterLogin)
                        }
                    )
                    QuickActionButton(
                        title = "Reboot",
                        icon = Icons.Default.PowerSettingsNew,
                        onClick = {
                            if (isRouterAuth) showRebootDialog = true
                            else onNavigate(Screen.RouterLogin)
                        },
                        accent = NeonRed
                    )
                    QuickActionButton(
                        title = "Devices",
                        icon = Icons.Default.Devices,
                        onClick = { onNavigate(Screen.ConnectedDevices) }
                    )
                    QuickActionButton(
                        title = "Console",
                        icon = Icons.Default.Language,
                        onClick = { onNavigate(Screen.WebConsole) }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Bottom Navigation Bar
            CyberBottomBar(
                currentScreen = Screen.Home,
                onNavigate = onNavigate
            )
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    NeonCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 10.sp,
                color = TextMuted
            )
        }
    }
}

@Composable
fun QuickActionButton(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    accent: Color = NeonCyan
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Surface(
            modifier = Modifier.size(54.dp),
            shape = RoundedCornerShape(16.dp),
            color = CyberSurface,
            border = BorderStroke(1.dp, CyberCardBorder)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accent,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary
        )
    }
}
