package com.router.x.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.router.x.theme.CyberBackground
import com.router.x.theme.DeepNeonBlue
import com.router.x.theme.ElectricBlue
import com.router.x.theme.NeonCyan
import com.router.x.theme.NeonCyanGlow
import com.router.x.theme.TextMuted
import com.router.x.theme.TextPrimary
import com.router.x.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2200, easing = FastOutSlowInEasing)
        )
        delay(300)
        onNavigateToHome()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
            .clickable { onNavigateToHome() },
        contentAlignment = Alignment.Center
    ) {
        // Ambient background planet & glow
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Center glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        DeepNeonBlue.copy(alpha = 0.45f * pulseGlow),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.5f, h * 0.62f),
                    radius = w * 0.7f
                ),
                center = Offset(w * 0.5f, h * 0.62f),
                radius = w * 0.7f
            )

            // Cyber grid lines
            for (i in -4..4) {
                drawLine(
                    color = Color(0x1800E5FF),
                    start = Offset(w * 0.5f + i * 40f, h * 0.55f),
                    end = Offset(w * 0.5f + i * 110f, h * 0.78f),
                    strokeWidth = 1.5f
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Logo & Header Section
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Futuristic "X" with WiFi arcs
                Box(
                    modifier = Modifier.size(90.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(80.dp)) {
                        // Concentric WiFi arcs
                        val center = Offset(size.width / 2f, size.height * 0.25f)
                        drawArc(
                            color = NeonCyan.copy(alpha = 0.9f),
                            startAngle = 205f,
                            sweepAngle = 130f,
                            useCenter = false,
                            topLeft = Offset(center.x - 30.dp.toPx(), center.y - 30.dp.toPx()),
                            size = androidx.compose.ui.geometry.Size(60.dp.toPx(), 60.dp.toPx()),
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )
                        drawArc(
                            color = ElectricBlue.copy(alpha = 0.8f),
                            startAngle = 215f,
                            sweepAngle = 110f,
                            useCenter = false,
                            topLeft = Offset(center.x - 20.dp.toPx(), center.y - 20.dp.toPx()),
                            size = androidx.compose.ui.geometry.Size(40.dp.toPx(), 40.dp.toPx()),
                            style = Stroke(width = 2.5f.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    // Cyber X text
                    Text(
                        text = "X",
                        fontSize = 58.sp,
                        fontWeight = FontWeight.Black,
                        color = NeonCyan,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "ROUTER X",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 3.sp,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "One App. Complete Control.",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NeonCyan
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Universal Router Management\nLocal • Remote • Secure",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }

            // Central Cyber Router Hologram
            Box(
                modifier = Modifier
                    .size(240.dp),
                contentAlignment = Alignment.Center
            ) {
                // Neon Ring
                Canvas(modifier = Modifier.size(200.dp)) {
                    drawOval(
                        brush = Brush.radialGradient(
                            colors = listOf(NeonCyan.copy(alpha = 0.5f), Color.Transparent)
                        ),
                        topLeft = Offset(0f, size.height * 0.45f),
                        size = androidx.compose.ui.geometry.Size(size.width, size.height * 0.35f),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }

                // Glowing router icon / representation
                Icon(
                    imageVector = Icons.Default.Router,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(90.dp)
                )
            }

            // Bottom Progress Indicator
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Initializing Network Engine...",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "${(progress.value * 92 + 8).toInt()}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { progress.value },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp),
                    color = NeonCyan,
                    trackColor = CyberBackground.copy(alpha = 0.7f),
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }
}
