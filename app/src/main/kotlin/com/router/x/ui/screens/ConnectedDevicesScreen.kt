package com.router.x.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DeviceUnknown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.router.x.data.model.DeviceItem
import com.router.x.data.model.DeviceType
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
import com.router.x.ui.components.CyberDangerButton
import com.router.x.ui.components.CyberOutlinedButton
import com.router.x.ui.components.CyberPrimaryButton
import com.router.x.ui.components.NeonCard
import com.router.x.viewmodel.RouterViewModel

@Composable
fun ConnectedDevicesScreen(
    viewModel: RouterViewModel,
    onNavigate: (Screen) -> Unit
) {
    val devices by viewModel.filteredDevices.collectAsState()
    val allDevices by viewModel.devices.collectAsState()
    val currentTab by viewModel.deviceTabFilter.collectAsState()

    var selectedDevice by remember { mutableStateOf<DeviceItem?>(null) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var renameText by remember { mutableStateOf("") }

    if (showRenameDialog && selectedDevice != null) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            containerColor = CyberSurface,
            title = {
                Text(
                    text = "Rename Device",
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )
            },
            text = {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    label = { Text("Device Name", color = TextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = CyberCardBorder
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedDevice?.let { viewModel.renameDevice(it.macAddress, renameText) }
                        showRenameDialog = false
                    }
                ) {
                    Text("Save", color = NeonCyan, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    CyberBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                    text = "Connected Devices",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                IconButton(onClick = { onNavigate(Screen.Home) }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextPrimary
                    )
                }
            }

            // Filter Tabs (All 24, Online 22, Offline 2)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val onlineCount = allDevices.count { it.isOnline }
                val offlineCount = allDevices.count { !it.isOnline }

                listOf(
                    "All" to "All (${allDevices.size})",
                    "Online" to "Online ($onlineCount)",
                    "Offline" to "Offline ($offlineCount)"
                ).forEach { (key, title) ->
                    val isSelected = currentTab == key
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) DeepNeonBlue else CyberSurface,
                        border = BorderStroke(1.dp, if (isSelected) NeonCyan else CyberCardBorder),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.deviceTabFilter.value = key }
                    ) {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) NeonCyan else TextSecondary,
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Devices List
            if (devices.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Scanning local network for devices...\nMake sure you are connected to Wi-Fi.",
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(devices) { device ->
                        val isSelected = selectedDevice?.id == device.id
                        DeviceCardItem(
                            device = device,
                            isSelected = isSelected,
                            onClick = {
                                selectedDevice = if (isSelected) null else device
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bottom Action Buttons (Block, Rename)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CyberDangerButton(
                    text = if (selectedDevice?.isBlocked == true) "Unblock" else "Block",
                    icon = Icons.Default.Block,
                    onClick = {
                        selectedDevice?.let { dev ->
                            if (dev.isBlocked) {
                                viewModel.unblockDevice(dev.macAddress) {}
                            } else {
                                viewModel.blockDevice(dev.macAddress) {}
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                )

                CyberPrimaryButton(
                    text = "Rename",
                    icon = Icons.Default.Edit,
                    onClick = {
                        selectedDevice?.let {
                            renameText = it.name
                            showRenameDialog = true
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun DeviceCardItem(
    device: DeviceItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val icon = when (device.deviceType) {
        DeviceType.PHONE -> Icons.Default.PhoneAndroid
        DeviceType.LAPTOP -> Icons.Default.Laptop
        DeviceType.TV -> Icons.Default.Tv
        DeviceType.DESKTOP -> Icons.Default.Computer
        DeviceType.UNKNOWN -> Icons.Default.DeviceUnknown
        DeviceType.SWITCH -> Icons.Default.Router
        DeviceType.ACCESS_POINT -> Icons.Default.Router
    }

    NeonCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (isSelected) NeonCyan else CyberCardBorder,
        borderWidth = if (isSelected) 1.5.dp else 1.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = RoundedCornerShape(12.dp),
                color = CyberSurfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (device.isBlocked) NeonRed else NeonCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = device.name + if (device.isBlocked) " (Blocked)" else "",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (device.isBlocked) NeonRed else TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${device.ipAddress} • ${device.vendor}",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            // Online Dot
            Surface(
                modifier = Modifier.size(8.dp),
                shape = CircleShape,
                color = if (device.isOnline) NeonGreen else Color(0xFF6B7280)
            ) {}

            Spacer(modifier = Modifier.width(10.dp))

            Icon(
                imageVector = Icons.Default.NavigateNext,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
