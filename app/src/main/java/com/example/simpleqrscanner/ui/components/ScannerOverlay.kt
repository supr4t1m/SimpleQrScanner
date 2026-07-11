package com.example.simpleqrscanner.ui.components

import android.R.attr.fillType
import android.view.View
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp

@Composable
fun ScannerOverlay(modifier: Modifier = Modifier) {

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {

        val boxWidth = 250.dp.toPx()
        val boxHeight = 250.dp.toPx()

        val topLeftX = (size.width - boxWidth) / 2
        val topLeftY = (size.height - boxHeight) / 2

        // 1. Create a path calculation for the cutout
        val cutoutPath = Path().apply {
            // Fill type EvenOdd tells Android to subtract overlapping paths
            fillType = PathFillType.EvenOdd

            // Path 1: The entire screen bounds
            addRect(androidx.compose.ui.geometry.Rect(Offset.Zero, size))

            // Path 2: The inner scanning window box
            addRoundRect(
                RoundRect(
                    left = topLeftX,
                    top = topLeftY,
                    right = topLeftX + boxWidth,
                    bottom = topLeftY + boxHeight,
                    cornerRadius = CornerRadius(12.dp.toPx())
                )
            )
        }

        // Draw a semi transparent dark background layer
        drawPath(
            path = cutoutPath,
            color = Color.Black.copy(alpha = 0.6f)
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