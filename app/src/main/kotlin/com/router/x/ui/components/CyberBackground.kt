package com.router.x.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.router.x.theme.CyberBackground
import com.router.x.theme.CyberBackgroundElevated
import com.router.x.theme.DeepNeonBlue

@Composable
fun CyberBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Top ambient neon blue glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        DeepNeonBlue.copy(alpha = 0.25f),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.5f, 0f),
                    radius = width * 0.9f
                ),
                center = Offset(width * 0.5f, 0f),
                radius = width * 0.9f
            )

            // Subtle cyber grid dots / lines
            val step = 60f
            var y = 0f
            while (y < height) {
                drawLine(
                    color = Color(0x0800E5FF),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f
                )
                y += step
            }

            var x = 0f
            while (x < width) {
                drawLine(
                    color = Color(0x0600E5FF),
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 1f
                )
                x += step
            }
        }

        content()
    }
}
