package com.router.x.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.router.x.theme.NeonCyan
import com.router.x.theme.NeonMagenta
import com.router.x.theme.TextMuted
import com.router.x.theme.TextSecondary

@Composable
fun WaveformChart(
    downloadPoints: List<Float>,
    uploadPoints: List<Float>,
    modifier: Modifier = Modifier,
    maxVal: Float = 160f,
    showLegend: Boolean = true
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (showLegend) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Live Traffic (Last 1 Hour)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(8.dp),
                        shape = CircleShape,
                        color = NeonCyan
                    ) {}
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Download", fontSize = 11.sp, color = TextSecondary)

                    Spacer(modifier = Modifier.width(12.dp))

                    Surface(
                        modifier = Modifier.size(8.dp),
                        shape = CircleShape,
                        color = NeonMagenta
                    ) {}
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Upload", fontSize = 11.sp, color = TextSecondary)
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
        ) {
            // Y-axis labels
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 4.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "150", fontSize = 9.sp, color = TextMuted)
                Text(text = "100", fontSize = 9.sp, color = TextMuted)
                Text(text = "50", fontSize = 9.sp, color = TextMuted)
                Text(text = "0", fontSize = 9.sp, color = TextMuted)
            }

            // Canvas for grid and waveforms
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 28.dp, bottom = 4.dp)
            ) {
                val w = size.width
                val h = size.height

                // Draw 4 horizontal grid lines
                val gridLines = 4
                for (i in 0 until gridLines) {
                    val y = h * (i.toFloat() / (gridLines - 1))
                    drawLine(
                        color = Color(0x1400E5FF),
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 1f
                    )
                }

                fun drawWave(data: List<Float>, strokeColor: Color, glowColor: Color) {
                    if (data.size < 2) return
                    val step = w / (data.size - 1)
                    val path = Path()
                    val fillPath = Path()

                    val startY = h - (data[0] / maxVal).coerceIn(0f, 1f) * h
                    path.moveTo(0f, startY)
                    fillPath.moveTo(0f, h)
                    fillPath.lineTo(0f, startY)

                    for (i in 0 until data.size - 1) {
                        val x1 = i * step
                        val y1 = h - (data[i] / maxVal).coerceIn(0f, 1f) * h
                        val x2 = (i + 1) * step
                        val y2 = h - (data[i + 1] / maxVal).coerceIn(0f, 1f) * h

                        val cx = (x1 + x2) / 2f
                        path.cubicTo(cx, y1, cx, y2, x2, y2)
                        fillPath.cubicTo(cx, y1, cx, y2, x2, y2)
                    }

                    fillPath.lineTo(w, h)
                    fillPath.close()

                    // Draw gradient fill
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                glowColor.copy(alpha = 0.35f),
                                glowColor.copy(alpha = 0.05f),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = h
                        )
                    )

                    // Draw glowing stroke
                    drawPath(
                        path = path,
                        color = strokeColor,
                        style = Stroke(width = 2.5f, cap = StrokeCap.Round)
                    )
                }

                // Draw upload first (behind)
                drawWave(uploadPoints, NeonMagenta, NeonMagenta)
                // Draw download
                drawWave(downloadPoints, NeonCyan, NeonCyan)
            }
        }
    }
}
