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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.router.x.navigation.Screen
import com.router.x.theme.CyberBackground
import com.router.x.theme.CyberCardBorder
import com.router.x.theme.CyberSurface
import com.router.x.theme.CyberSurfaceVariant
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
import com.router.x.ui.components.NeonCard
import com.router.x.viewmodel.RouterViewModel

data class ControlTile(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun ControlCenterScreen(
    viewModel: RouterViewModel,
    onNavigate: (Screen) -> Unit
) {
    val selectedRouter by viewModel.selectedRouter.collectAsState()
    val currentTab by viewModel.controlCenterTab.collectAsState()
    var showRebootDialog by remember { mutableStateOf(false) }

    val controlTiles = listOf(
        ControlTile("WiFi\nSettings", Icons.Default.Wifi) { onNavigate(Screen.AdvancedSettings) },
        ControlTile("LAN &\nDHCP", Icons.Default.Lan) { onNavigate(Screen.ConnectedDevices) },
        ControlTile("Firewall &\nSecurity", Icons.Default.Security) { onNavigate(Screen.AdvancedSettings) },
        ControlTile("Routing &\nVLAN", Icons.Default.AltRoute) { onNavigate(Screen.NetworkMap) },
        ControlTile("VPN\nManagement", Icons.Default.Lock) {},
        ControlTile("Bandwidth\nControl", Icons.Default.Speed) { onNavigate(Screen.Monitoring) },
        ControlTile("Backup &\nRestore", Icons.Default.Backup) {},
        ControlTile("Advanced\nSettings", Icons.Default.Settings) { onNavigate(Screen.AdvancedSettings) }
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
                    text = "Rebooting will terminate active network connections on ${selectedRouter.modelName}.",
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
                        fontSize = 17.sp,
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
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = NeonCyan.copy(alpha = 0.5f)
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
                                    tint = NeonCyan,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = selectedRouter.modelName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = selectedRouter.ipAddress,
                                fontSize = 13.sp,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    modifier = Modifier.size(7.dp),
                                    shape = CircleShape,
                                    color = NeonGreen
                                ) {}
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Online",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = NeonGreen
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

                Spacer(modifier = Modifier.height(14.dp))

                // 2-Column Grid of 8 Features
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

                Spacer(modifier = Modifier.height(14.dp))

                // Reboot Router Button (Red Glowing Gradient)
                CyberDangerButton(
                    text = "Reboot Router",
                    icon = Icons.Default.PowerSettingsNew,
                    onClick = { showRebootDialog = true }
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Bottom Navigation Bar
            CyberBottomBar(
                currentScreen = Screen.ControlCenter,
                onNavigate = onNavigate
            )
        }
    }
}

@Composable
fun ControlGridTile(tile: ControlTile) {
    NeonCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = CyberCardBorder,
        onClick = tile.onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(46.dp),
                shape = RoundedCornerShape(12.dp),
                color = CyberSurfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = tile.icon,
                        contentDescription = tile.title,
                        tint = NeonCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = tile.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = TextPrimary,
                lineHeight = 16.sp
            )
        }
    }
}
