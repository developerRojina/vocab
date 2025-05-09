package com.vocable.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vocable.data.word.domain.model.Meaning
import timber.log.Timber
import kotlin.math.abs


@Composable
fun FlashCards(items: List<Any>) {
    Timber.d("the items size is ${items.size}")
    val meaningCount = items.size
    val colors = remember(meaningCount) { items.map { randomColor() } }
    Column(horizontalAlignment = Alignment.Start) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
        ) {

            var topCardIndex by remember(meaningCount) { mutableIntStateOf(-1) }

            val xOffsets = remember(meaningCount) {
                mutableStateListOf<Dp>().apply {
                    repeat(meaningCount) { index ->
                        add(24.dp * index)
                    }
                }
            }
            val yOffsets = remember(meaningCount) {
                mutableStateListOf<Dp>().apply {
                    repeat(meaningCount) { index ->
                        add(12.dp * index)
                    }
                }
            }
            val dragOffsets = remember(meaningCount) {
                mutableStateListOf<Float>().apply {
                    repeat(meaningCount) { index ->
                        add(0f)
                    }
                }
            }

            val blockSize = 5

            LaunchedEffect(topCardIndex) {
                Timber.d("the selected card is $topCardIndex")
                if (topCardIndex >= 0) {
                    var remainder = (topCardIndex % blockSize)
                    if (remainder == 0 && topCardIndex > 0) {
                        val division = (topCardIndex / blockSize) - 1
                        var blockIndex = division * 5

                        for (k in blockIndex until topCardIndex + 1) {
                            yOffsets[k] = 0.dp
                            xOffsets[k] = 16.dp
                            dragOffsets[k] = -90f + ((division + 1) * 5)
                        }
                    }

                    if (topCardIndex < meaningCount - 1) {
                        val division = (topCardIndex / blockSize)
                        dragOffsets[topCardIndex] =
                            -90f + (minOf(topCardIndex, remainder + division) * blockSize)
                        xOffsets[topCardIndex + 1] = 16.dp
                        yOffsets[topCardIndex + 1] = 12.dp

                        for (j in (topCardIndex + 2) until meaningCount) {
                            xOffsets[j] = 24.dp * (j - (topCardIndex))
                            yOffsets[j] = 12.dp * (j - (topCardIndex))

                        }
                    }
                }
            }

            items.indices.reversed().forEach { visualIndex ->

                val item = items[visualIndex]
                val color = colors[visualIndex]
                val dragOffset = dragOffsets[visualIndex]
                val xOffset = xOffsets[visualIndex]
                val yOffset = yOffsets[visualIndex]
                val (type, body) = when (val data = item) {
                    is String -> null to data
                    is Meaning -> {
                        data.partOfSpeech to data.meaning
                    }

                    else -> null to ""
                }

                FlashCard(
                    FlashCardInfo(
                        data = body,
                        type = type,
                        color = color,
                        dragOffset = dragOffset,
                        xOffset = xOffset,
                        yOffset = yOffset
                    ),
                    visualIndex,

                    onSwiped = { dataIndex ->
                        topCardIndex = dataIndex
                    },
                    onTap = { dataIndex ->
                        if (dataIndex <= topCardIndex) {
                            for (j in dataIndex until meaningCount) {
                                dragOffsets[j] = 0f
                                xOffsets[j] = 24.dp * (j - dataIndex)
                                yOffsets[j] = 12.dp * (j - dataIndex)
                            }
                        }
                    },
                )
            }
        }
    }

}

@Composable
fun FlashCard(
    info: FlashCardInfo,
    visualIndex: Int,
    onTap: (Int) -> Unit = {},
    onSwiped: (Int) -> Unit = {},
) {
    val dataIndex = visualIndex
    val rotationDegrees by animateFloatAsState(
        targetValue = info.dragOffset,
        animationSpec = tween(durationMillis = 300),
        label = "rotationAnim"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .offset(x = info.xOffset, y = info.yOffset)
            .graphicsLayer {
                transformOrigin = TransformOrigin(0f, 0f)
                rotationZ = abs(rotationDegrees)
            }

            .pointerInput(info.hashCode()) {
                detectTapGestures(
                    onTap = { onTap(dataIndex) }
                )
            }
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    Timber.i("inside delta is $delta")
                    if (delta < 0) {
                        onSwiped(dataIndex)
                    }
                }
            ),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = info.color)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
        ) {

            Column(Modifier.padding(28.dp)) {
                info.type?.let { Text(text = it) }
                Text(
                    text = info.data,
                    textAlign = TextAlign.Left,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Text(
                text = dataIndex.toString(),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            )
        }
    }
}

