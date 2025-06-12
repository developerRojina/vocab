package com.vocable

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.vocable.home.randomColor
import kotlin.random.Random

@Composable
fun WordCard(
    content: String,
    modifier: Modifier,
    onWordClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .graphicsLayer {
                transformOrigin = TransformOrigin(0f, 0f)
                rotationZ = Random.nextInt(-2, 2).toFloat()
            }
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onWordClicked()
            }

    ) {
        WordCardBackground(
            modifier
                .fillMaxWidth()
                .fillMaxHeight()

        )
        Text(
            content,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun WordCardBackground(modifier: Modifier) {
    val isDarkTheme = isSystemInDarkTheme()
    val color = randomColor(isDarkTheme)
    Canvas(
        modifier = modifier
    ) {
        val width = size.width
        val height = size.height
        val curveRadius = Random.nextInt(-80, 80).toFloat()


        // 1️⃣ Your custom curved path
        val path = Path().apply {
            moveTo(0f, 0f)

            quadraticTo(
                width / 2, curveRadius, // control point
                width, 0f // end point
            )

            quadraticTo(width - curveRadius, height / 2, width, height)


            lineTo(0f, height)
            close()
        }

        val shadowOffset = 6f
        val shadowColor = Color.Black.copy(alpha = 0.25f)

        val shadowPath = Path().apply {
            // same path, but offset for shadow effect
            moveTo(0f + shadowOffset, 0f + shadowOffset)

            quadraticTo(
                width / 2 + shadowOffset, curveRadius + shadowOffset,
                width + shadowOffset, 0f + shadowOffset
            )

            quadraticTo(
                width - curveRadius + shadowOffset,
                height / 2 + shadowOffset,
                width + shadowOffset,
                height + shadowOffset
            )

            lineTo(0f + shadowOffset, height + shadowOffset)
            close()
        }



        drawPath(path = shadowPath, color = shadowColor)

        drawPath(path = path, color = color)

    }

}

