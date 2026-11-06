package com.router.x.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.NetworkPing
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.router.x.navigation.Screen
import com.router.x.theme.CyberBackground
import com.router.x.theme.CyberCardBorder
import com.router.x.theme.CyberSurface
import com.router.x.theme.DeepNeonBlue
import com.router.x.theme.NeonCyan
import com.router.x.theme.NeonGreen
import com.router.x.theme.NeonMagenta
import com.router.x.theme.NeonOrange
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
fun MonitoringScreen(
    viewModel: RouterViewModel,
    onNavigate: (Screen) -> Unit
) {
    val telemetry by viewModel.telemetry.collectAsState()
    val activeTimeRange by viewModel.monitoringTimeRange.collectAsState()

    CyberBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
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
                        text = "Monitoring",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = NeonCyan
                        )
                    }
                }

                // Time Range Pills (1h, 6h, 24h, 7d)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("1h", "6h", "24h", "7d").forEach { range ->
                        val isSelected = activeTimeRange == range
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) DeepNeonBlue else CyberSurface,
                            border = BorderStroke(1.dp, if (isSelected) NeonCyan else CyberCardBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.monitoringTimeRange.value = range }
                        ) {
                            Text(
                                text = range,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) NeonCyan else TextSecondary,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // KPI Metric Row (Download, Upload, Ping)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Download",
                        value = "${telemetry.downloadSpeedMbps.toInt()} Mbps",
                        icon = Icons.Default.ArrowDownward,
                        accentColor = NeonCyan,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Upload",
                        value = "${telemetry.uploadSpeedMbps.toInt()} Mbps",
                        icon = Icons.Default.ArrowUpward,
                        accentColor = NeonMagenta,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Ping",
                        value = "${telemetry.pingMs} ms",
                        icon = Icons.Default.Speed,
                        accentColor = NeonGreen,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Waveform Chart
                NeonCard(modifier = Modifier.fillMaxWidth()) {
                    WaveformChart(
                        downloadPoints = telemetry.downloadHistory,
                        uploadPoints = telemetry.uploadHistory
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Router Resources Section
                Text(
                    text = "Router Resources",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                NeonCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        RadialGauge(
                            value = telemetry.cpuUsage,
                            label = "CPU",
                            color = NeonCyan,
                            size = 72.dp
                        )
                        RadialGauge(
                            value = telemetry.ramUsage,
                            label = "RAM",
                            color = NeonGreen,
                            size = 72.dp
                        )
                        RadialGauge(
                            value = telemetry.temperature,
                            label = "Temp",
                            unit = "°C",
                            color = NeonOrange,
                            size = 72.dp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Network Details Card
                Text(
                    text = "Network Details",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                NeonCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        NetworkDetailRow("Connected Devices", "${telemetry.connectedDevicesCount}")
                        HorizontalDivider(color = CyberCardBorder.copy(alpha = 0.5f))
                        NetworkDetailRow("Packet Loss", "${telemetry.packetLossPercent}%")
                        HorizontalDivider(color = CyberCardBorder.copy(alpha = 0.5f))
                        NetworkDetailRow("Jitter", "${telemetry.jitterMs} ms")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Bottom Navigation Bar
            CyberBottomBar(
                currentScreen = Screen.Monitoring,
                onNavigate = onNavigate
            )
        }
    }
}

@Composable
fun NetworkDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.sp, color = TextSecondary)
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
    }
}
