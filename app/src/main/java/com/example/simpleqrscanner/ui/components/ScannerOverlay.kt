package com.example.simpleqrscanner.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun ScannerOverlay(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val boxWidth = 250.dp.toPx()
        val boxHeight = 250.dp.toPx()

        val topLeftX = (size.width - boxWidth) / 2
        val topLeftY = (size.height - boxHeight) / 2

        // Draw a semi transparent dark background layer
        drawRect(
            color = Color.Black.copy(alpha = 0.4f)
        )

        drawRoundRect(
            color = Color.Cyan,
            topLeft = Offset(topLeftX, topLeftY),
            size = Size(boxWidth, boxHeight),
            cornerRadius = CornerRadius(12.dp.toPx()),
            style = Stroke(width = 4.dp.toPx())
        )
    }
}