package com.router.x.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.router.x.theme.DeepNeonBlue
import com.router.x.theme.ElectricBlue
import com.router.x.theme.NeonCyan
import com.router.x.theme.NeonGreen
import com.router.x.theme.NeonGreenGlow
import com.router.x.theme.TextMuted
import com.router.x.theme.TextPrimary
import com.router.x.theme.TextSecondary
import com.router.x.ui.components.CyberBackground
import com.router.x.ui.components.NeonCard
import com.router.x.viewmodel.RouterViewModel

@Composable
fun NetworkMapScreen(
    viewModel: RouterViewModel,
    onNavigate: (Screen) -> Unit
) {
    val selectedRouter by viewModel.selectedRouter.collectAsState()
    val currentMode by viewModel.topologyMode.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_anim"
    )

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
                    text = "Network Map",
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

            // Mode Pills (Topology, Flow)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = CyberSurface,
                    border = BorderStroke(1.dp, CyberCardBorder)
                ) {
                    Row(modifier = Modifier.padding(4.dp)) {
                        listOf("Topology", "Flow").forEach { mode ->
                            val isSelected = currentMode == mode
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) DeepNeonBlue else Color.Transparent,
                                modifier = Modifier.clickable { viewModel.topologyMode.value = mode }
                            ) {
                                Text(
                                    text = mode,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) NeonCyan else TextSecondary,
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Topology Map Visual Diagram Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Connecting lines Canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    val topCenterX = w * 0.5f
                    val topCenterY = h * 0.12f

                    val midCenterX = w * 0.5f
                    val midCenterY = h * 0.48f

                    val bottomY = h * 0.85f
                    val bLeftX = w * 0.18f
                    val bMidX = w * 0.5f
                    val bRightX = w * 0.82f

                    // Glowing green aura around router
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                NeonGreen.copy(alpha = 0.25f * pulse),
                                Color.Transparent
                            )
                        ),
                        center = Offset(midCenterX, midCenterY),
                        radius = 80.dp.toPx()
                    )

                    // Line from Internet to Router
                    drawLine(
                        color = NeonCyan,
                        start = Offset(topCenterX, topCenterY + 30.dp.toPx()),
                        end = Offset(midCenterX, midCenterY - 40.dp.toPx()),
                        strokeWidth = 2.dp.toPx()
                    )

                    // Lines from Router to Subnodes (Devices, Switch, AP)
                    drawLine(
                        color = NeonGreen,
                        start = Offset(midCenterX, midCenterY + 45.dp.toPx()),
                        end = Offset(bLeftX, bottomY - 30.dp.toPx()),
                        strokeWidth = 2.dp.toPx()
                    )

                    drawLine(
                        color = NeonGreen,
                        start = Offset(midCenterX, midCenterY + 45.dp.toPx()),
                        end = Offset(bMidX, bottomY - 30.dp.toPx()),
                        strokeWidth = 2.dp.toPx()
                    )

                    drawLine(
                        color = NeonGreen,
                        start = Offset(midCenterX, midCenterY + 45.dp.toPx()),
                        end = Offset(bRightX, bottomY - 30.dp.toPx()),
                        strokeWidth = 2.dp.toPx()
                    )
                }

                // Node 1: Internet (Top)
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier.size(54.dp),
                        shape = CircleShape,
                        color = DeepNeonBlue,
                        border = BorderStroke(1.5.dp, NeonCyan)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Internet",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                // Node 2: Central Router
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier.size(76.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = CyberSurfaceVariant,
                        border = BorderStroke(2.dp, NeonGreen)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Router,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(42.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = selectedRouter.modelName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = selectedRouter.ipAddress,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                // Leaf Nodes Row (Bottom)
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    MapLeafNode("Devices", "(24)", Icons.Default.Devices)
                    MapLeafNode("Switch", "(1)", Icons.Default.AltRoute)
                    MapLeafNode("AP", "(2)", Icons.Default.Wifi)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Status Banner (All Systems Normal)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = CyberSurface,
                border = BorderStroke(1.dp, NeonGreen)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = NeonGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Network Status",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "All Systems Normal",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun MapLeafNode(
    title: String,
    count: String,
    icon: ImageVector
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(50.dp),
            shape = RoundedCornerShape(14.dp),
            color = CyberSurface,
            border = BorderStroke(1.dp, CyberCardBorder)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = count,
            fontSize = 10.sp,
            color = TextMuted
        )
    }
}
