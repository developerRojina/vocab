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
    val colors = remember(items.map { it.hashCode() }) {
        items.map { randomColor() }
    }
    Column(horizontalAlignment = Alignment.Start) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
        ) {

            var topCardIndex by rememberSaveable(wordId) { mutableIntStateOf(-1) }


            val cardState = remember(wordId, meaningCount) {
                CardOffsetState(meaningCount)
            }

            LaunchedEffect(wordId, topCardIndex) {
                Timber.d("the top card index is $topCardIndex")
                if (topCardIndex >= 0) {
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
                        //  if (topCardIndex== dataIndex-1) {
                        if (dataIndex == topCardIndex) {
                            //todo something here
                        }
                        topCardIndex = dataIndex
                        // }

                    },
                    onTap = { dataIndex ->
                        if (dataIndex <= topCardIndex) {
                            cardState.resetOffsets(dataIndex, meaningCount)
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
        shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 48.dp, bottomEnd = 48.dp, topEnd = 32.dp),
        colors = CardDefaults.cardColors(containerColor = info.color)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
        ) {

            Column(Modifier.padding(28.dp).align(Alignment.TopStart)) {
                info.type?.let { Text(text = it,textAlign = TextAlign.End,) }
                Text(
                    text = info.data,
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.bodyMedium
                )

            }

            Text(
                text = dataIndex.toString(),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            )
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
