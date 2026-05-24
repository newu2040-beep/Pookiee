package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.dp
import kotlin.math.sin

@Composable
fun BatteryMascot(
    level: Int,
    isCharging: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveOffset"
    )

    Box(
        modifier = modifier.size(120.dp, 200.dp),
        contentAlignment = Alignment.Center
    ) {
        val batteryColor = when {
            level < 20 -> Color.Red
            level < 50 -> Color(0xFFFFA500)
            else -> Color(0xFF4CAF50)
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 8.dp.toPx()
            val capWidth = size.width * 0.4f
            val capHeight = size.height * 0.05f
            val bodyHeight = size.height * 0.95f
            
            // Draw Cap
            drawRoundRect(
                color = Color.Gray.copy(alpha = 0.5f),
                topLeft = Offset((size.width - capWidth) / 2, 0f),
                size = Size(capWidth, capHeight),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
            
            // Draw Body Border
            drawRoundRect(
                color = Color.Gray.copy(alpha = 0.3f),
                topLeft = Offset(0f, capHeight),
                size = Size(size.width, bodyHeight),
                cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
            )

            // Draw Liquid / Level
            clipRect(
                top = capHeight + bodyHeight * (1 - level / 100f),
                bottom = size.height
            ) {
                // Wave effect
                val waveHeight = 8.dp.toPx()
                for (x in 0..size.width.toInt()) {
                    val y = capHeight + bodyHeight * (1 - level / 100f) + 
                            sin(x.toDouble() / size.width.toDouble() * 2 * Math.PI + waveOffset).toFloat() * waveHeight
                    
                    drawLine(
                        color = batteryColor.copy(alpha = 0.6f),
                        start = Offset(x.toFloat(), y),
                        end = Offset(x.toFloat(), size.height),
                        strokeWidth = 1f
                    )
                }
            }
        }
        
        if (isCharging) {
            Icon(
                Icons.Default.Bolt,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.Yellow
            )
        }
    }
}
