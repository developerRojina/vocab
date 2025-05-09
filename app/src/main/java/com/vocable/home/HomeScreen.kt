package com.vocable.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import org.koin.androidx.compose.koinViewModel
import kotlin.random.Random


@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeScreen(onProfilePressed: () -> Unit) {
    val viewmodel = koinViewModel<HomeViewModel>()
    val state by viewmodel.state.collectAsState()
    val pagerState = rememberPagerState()
    val isScrolling = remember { derivedStateOf { pagerState.isScrollInProgress } }

    if (state.words.isNotEmpty()) {
        LaunchedEffect(pagerState.currentPage) {
            viewmodel.updatePageData(pagerState.currentPage)
        }
        Box(modifier = Modifier.fillMaxSize()) {
            VerticalPager(
                contentPadding = PaddingValues(vertical = 48.dp),
                count = state.words.size, state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    state.selectedPageData?.let {
                        WordInfo(it)
                    }
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
fun WordInfo(selectedPageData: SelectedPageData) {
    val word = selectedPageData.word
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

        selectedPageData.flashCardItems.isNotEmpty().let {
            FlashCards(items = selectedPageData.flashCardItems)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WordInfoOptions() {
    val items = listOf<String>("Meaning", "Sentence", "Synonyms")
    var selectedItem by remember { mutableStateOf<String?>(null) }

    FlowRow(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { item ->
            FilterChip(
                colors = FilterChipDefaults.filterChipColors(),
                selected = selectedItem == item,
                onClick = { selectedItem = item },
                label = { Text(item) }
            )
        }
    }

    /*  Row(modifier = Modifier.padding(vertical = 8.dp)) {
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
      }*/
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

