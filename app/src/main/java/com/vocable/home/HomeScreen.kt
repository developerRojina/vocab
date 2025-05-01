package com.vocable.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import com.vocable.R
import com.vocable.data.word.domain.model.Word
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber
import kotlin.math.abs
import kotlin.random.Random


@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeScreen(onProfilePressed: () -> Unit) {
    val viewmodel = koinViewModel<HomeViewModel>()
    val words = viewmodel.myWords.collectAsState()
    val pagerState = rememberPagerState()
    val isScrolling = remember { derivedStateOf { pagerState.isScrollInProgress } }


    if (words.value.isNotEmpty()) {

        Box(modifier = Modifier.fillMaxSize()) {
            VerticalPager(
                contentPadding = PaddingValues(vertical = 48.dp),
                count = words.value.size, state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    WordInfo(words.value[page])
                }
            }
            val enterAnimation =
                if (pagerState.currentPage != 0 && pagerState.currentPage != pagerState.pageCount - 1) {
                    scaleIn(animationSpec = tween(300))
                } else {
                    EnterTransition.None
                }

            val exitAnimation =
                if (pagerState.currentPage != 0 && pagerState.currentPage != pagerState.pageCount - 1) {
                    scaleOut(animationSpec = tween(0))
                } else {
                    ExitTransition.None
                }

            AnimatedVisibility(
                visible = !isScrolling.value,
                enter = enterAnimation,
                exit = exitAnimation,
            ) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 34.dp, horizontal = 12.dp)
                        .size(48.dp) // or adjust size as needed
                        .clip(CircleShape)
                        .background(Color.LightGray) // use any color you like
                ) {
                    IconButton(
                        onClick = { onProfilePressed() },
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Home",
                            tint = Color.Black // optional: change icon color
                        )
                    }
                }
            }

        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun WordInfo(word: Word) {
    val pagerState = rememberPagerState()
    val colors = remember { word.meaning.map { randomColor() } }
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val containerWidthDp = configuration.screenWidthDp.dp

    Column(horizontalAlignment = Alignment.Start) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = word.word,
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.titleLarge
            )

            IconButton(
                onClick = {},

                ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Home",
                    tint = Color.Black // optional: change icon color
                )
            }

        }


        WordInfoOptions()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
        ) {
            val meaningCount = word.meaning.size
            val topIndex = remember {   mutableIntStateOf(meaningCount-1)}

            val xOffsets = remember {
                mutableStateListOf<Dp>().apply {
                    repeat(meaningCount) { index ->
                        add(24.dp * index)
                    }
                }
            }
            val yOffsets = remember {
                mutableStateListOf<Dp>().apply {
                    repeat(meaningCount) { index ->
                        add(12.dp * index)
                    }
                }
            }
            val dragOffsets = remember {
                mutableStateListOf<Float>().apply {
                    repeat(meaningCount) { index ->
                        add(0f)
                    }
                }
            }

            word.meaning.indices.reversed().forEach { visualIndex ->

                val dataIndex = visualIndex

                val rotationDegrees by animateFloatAsState(
                    targetValue = dragOffsets[dataIndex],
                    animationSpec = tween(durationMillis = 300),
                    label = "rotationAnim"
                )

                val meaningItem = word.meaning[dataIndex]

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(x = xOffsets[visualIndex], y = yOffsets[visualIndex])
                        .graphicsLayer {
                            transformOrigin = TransformOrigin(0f, 0f)
                            rotationZ = abs(rotationDegrees)
                        }
                        .align(Alignment.Center)
                        .pointerInput(meaningItem.hashCode()) {
                            detectTapGestures(
                                onTap = {

                                    for (j in dataIndex until meaningCount) {
                                        dragOffsets[j] = 0f
                                        xOffsets[j] = 24.dp * (j - dataIndex)
                                        yOffsets[j] = 12.dp * (j - dataIndex)
                                    }
                                }
                            )
                        }
                        .draggable(
                            orientation = Orientation.Horizontal,
                            state = rememberDraggableState { delta ->
                                if (delta < 0) {
                                    dragOffsets[dataIndex] =
                                        (rotationDegrees + delta * 60.5f).coerceIn(
                                            -90f + (dataIndex * 5),
                                            0f)
                                }
                                var blockSize = 5
                                if (visualIndex % blockSize == 0 && visualIndex>0 ){
                                    Timber.i("inside 0 $visualIndex")


                                    var remainder = (visualIndex/blockSize)-1
                                    var blockIndex = remainder*5

                                    for (k in blockIndex  until visualIndex+1 ) {
                                        Timber.i("inside k $k")

                                        yOffsets[k] = 24.dp  + ((remainder+1) * 5).dp
                                        xOffsets[k] = 0.dp
                                        dragOffsets[k] = -90f + ((remainder+1) *5)
                                    }
                                }

                                if (visualIndex < meaningCount - 1) {
                                    for (j in (visualIndex + 1) until meaningCount) {
                                        Timber.i("inside j $j")

                                        xOffsets[j] = 24.dp * (j - visualIndex)
                                        yOffsets[j] = 12.dp * (j - visualIndex)
                                    }

                                }

                            }
                        ),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = colors[dataIndex])
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth()
                    ) {
                        Column(Modifier.padding(28.dp)) {
                            Text(text = meaningItem.partOfSpeech)
                            Text(
                                text = meaningItem.meaning,
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
        }


        /* HorizontalPager(
             count = word.meaning.size, state = pagerState
         ) {
             val item = word.meaning[it]
             val swipeStarted = pagerState.currentPageOffset > 0f
             Timber.d("the page offset is ${pagerState.currentPageOffset}")
             val targetAngle = if (swipeStarted && pagerState.currentPage == it) 30f * abs( 10* pagerState.currentPageOffset) else 0f
             val animatedRotation by animateFloatAsState(
                 targetValue = targetAngle,
                 animationSpec = spring(
                     dampingRatio = Spring.DampingRatioLowBouncy,
                     stiffness = Spring.StiffnessLow
                 )
             )
             Card(
                 modifier = Modifier
                     .fillMaxWidth()
                     .fillMaxHeight()
                     .align(Alignment.Center)
                     .graphicsLayer(
                        rotationZ = animatedRotation,
                         transformOrigin = TransformOrigin(
                             0f,
                             0f
                         )
                     ),

                 elevation = CardDefaults.cardElevation(4.dp),
                 shape = RoundedCornerShape(20.dp),
                 colors = CardDefaults.cardColors(containerColor = colors[word.meaning.lastIndex - it])
             ) {

                 Box(
                     modifier = Modifier
                         .fillMaxWidth()
                         .fillMaxHeight()
                         .padding(16.dp),
                     contentAlignment = Alignment.TopStart

                 ) {

                     Column {
                         Text(text = item.partOfSpeech)
                         Text(
                             textAlign = TextAlign.Left,
                             text = item.meaning,
                             style = MaterialTheme.typography.bodyLarge
                         )
                     }

                 }

             }
         }*/

    }

}

@Composable
fun WordInfoOptions() {
    Row(modifier = Modifier.padding(vertical = 8.dp)) {
        Box(
            modifier = Modifier
                .background(
                    color = Color(0xFFff5a1a),
                    shape = RoundedCornerShape(50)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.meaning),
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(Modifier.width(4.dp))
        Box(
            modifier = Modifier
                .background(
                    color = Color(0xFF00ADB5),
                    shape = RoundedCornerShape(50)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {


            Text(
                text = stringResource(R.string.sentence),
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
        }


    }
}

fun randomColor(): Color {
    val hue = Random.nextFloat() * 360f // Any hue
    val saturation = 0.4f + Random.nextFloat() * 0.2f // soft colors
    val lightness = 0.7f + Random.nextFloat() * 0.2f // light tones (0.7 - 0.9)

    val colorInt = ColorUtils.HSLToColor(floatArrayOf(hue, saturation, lightness))
    return Color(colorInt)
}

@Composable
fun SwipeableCard(
    text: String,
    isTopCard: Boolean,
    onSwiped: (direction: SwipeDirection) -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    val rotationDegrees by animateFloatAsState(
        targetValue = offsetX / 20,
        animationSpec = tween(durationMillis = 300)
    )

    Card(
        modifier = Modifier
            .size(300.dp, 400.dp)
            .graphicsLayer {
                translationX = offsetX
                translationY = offsetY
                rotationZ = rotationDegrees
            }
            .pointerInput(isTopCard) {
                if (isTopCard) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            // If dragged enough, consider it swiped
                            when {
                                offsetX > 200f -> onSwiped(SwipeDirection.RIGHT)
                                offsetX < -200f -> onSwiped(SwipeDirection.LEFT)
                                else -> {
                                    // Not enough, return to original
                                    offsetX = 0f
                                    offsetY = 0f
                                }
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            offsetX += dragAmount
                            offsetY += dragAmount
                        }
                    )
                }
            },
        shape = MaterialTheme.shapes.medium,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Cyan),
            contentAlignment = Alignment.Center
        ) {
            Text(text = text, style = MaterialTheme.typography.headlineMedium)
        }
    }
}

enum class SwipeDirection { LEFT, RIGHT }

