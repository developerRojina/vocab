package com.vocable.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vocable.data.word.domain.model.Meaning
import timber.log.Timber
import kotlin.math.abs


@Composable
fun FlashCards(items: List<Any>, wordId: String) {
    Timber.d("the items size is ${items.size}")
    val meaningCount = items.size
    val isDarkTheme = isSystemInDarkTheme()
    val colors = remember(items.map { it.hashCode() }) {
        items.map { randomColor(isDarkTheme) }
    }
    Column(horizontalAlignment = Alignment.Start) {
        Box(
            modifier = Modifier
                .fillMaxWidth(.8f)
                .fillMaxHeight(.7f)
        ) {

            var topCardIndex by rememberSaveable(wordId) { mutableIntStateOf(-1) }
            var canSwipe by rememberSaveable(wordId) { mutableStateOf(true) }

            val cardState = remember(wordId, meaningCount) {
                CardOffsetState(meaningCount)
            }

            LaunchedEffect(wordId, topCardIndex, canSwipe) {
                Timber.d("the top card index is $topCardIndex")
                if (topCardIndex >= 0 && canSwipe) {
                    cardState.updateOffsetsForSelection(topCardIndex, meaningCount)
                }
            }


            items.indices.reversed().forEach { visualIndex ->

                val item = items[visualIndex]
                val color = colors[visualIndex]
                val (type, body) = extractCardContent(item)

                FlashCard(
                    FlashCardInfo(
                        data = body,
                        type = type,
                        color = color,
                        dragOffset = cardState.dragOffsets[visualIndex],
                        xOffset = cardState.xOffsets[visualIndex],
                        yOffset = cardState.yOffsets[visualIndex],
                    ),
                    visualIndex,

                    onSwiped = { dataIndex ->
                        Timber.d("the data index is $dataIndex")
                        Timber.d("the topCard index is $topCardIndex")
                        if (topCardIndex + 1 == dataIndex) {
                            topCardIndex = dataIndex
                        }

                    },
                    onTap = { dataIndex ->
                        if (dataIndex <= topCardIndex) {
                            cardState.resetOffsets(dataIndex, meaningCount)
                            topCardIndex = dataIndex - 1
                        } else if (topCardIndex + 1 == dataIndex) {
                            topCardIndex = dataIndex
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
                    if (delta < 0) {
                        onSwiped(dataIndex)
                    }
                }
            ),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 48.dp, bottomEnd = 48.dp, topEnd = 32.dp),
        colors = CardDefaults.cardColors(containerColor = info.color)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
        ) {

            Column(
                Modifier.align(Alignment.TopStart)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 34.dp, end = 8.dp, top = 16.dp)
                        .align(Alignment.End)
                ) {
                    Text(text = info.type ?: "")
                    Text(
                        text = (dataIndex + 1).toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 24.dp)

                    )
                }
                Spacer(Modifier.height(16.dp))
                HorizontalDivider()
                Text(
                    text = info.data,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 24.dp * (dataIndex % 6 + 1),
                            end = 34.dp,
                            top = 18.dp,
                            bottom = 24.dp
                        )
                        .align(Alignment.End),
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.bodyMedium
                )

            }


        }
    }
}

private fun extractCardContent(item: Any): Pair<String?, String> {
    return when (item) {
        is String -> null to item
        is Meaning -> item.partOfSpeech to item.meaning
        else -> null to ""
    }
}
