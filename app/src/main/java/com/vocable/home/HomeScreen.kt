package com.vocable.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import com.vocable.R
import com.vocable.ui.theme.SurfaceLight
import com.vocable.ui.theme.TealPrimary
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import kotlin.random.Random


@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeScreen(onProfilePressed: () -> Unit, onSettingsPressed: () -> Unit) {
    val viewmodel = koinViewModel<HomeViewModel>()
    val state by viewmodel.state.collectAsState()
    val pagerState = rememberPagerState()
    val words = state.pages
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            delay(800)
            if (!pagerState.isScrollInProgress) {
                showContent = true
            }
        } else {
            showContent = false
        }
    }

    if (words.isNotEmpty()) {

        Box(modifier = Modifier.fillMaxSize()) {
            VerticalPager(
                count = words.size,
                state = pagerState,
                contentPadding = PaddingValues(vertical = 52.dp),
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val selectedPageData = words[page]
                key(selectedPageData.word.id) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {

                        WordInfo(
                            pageData = words[page],
                            onTypeSelected = { type ->
                                viewmodel.selectFlashCardType(
                                    selectedPageData,
                                    type,
                                    pagerState.currentPage
                                )
                            })

                    }
                }
            }


                Box(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 48.dp)
                        .clip(CircleShape)
                        .background(TealPrimary) // use any color you like
                ) {
                    Row {
                        IconButton(
                            onClick = { onProfilePressed() },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Home",
                                tint = Color.White // optional: change icon color
                            )
                        }
                        IconButton(
                            onClick = { onSettingsPressed() },

                            ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = Color.White // optional: change icon color
                            )
                        }
                    }

                }

        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun WordInfo(pageData: PageData, onTypeSelected: (FlashCardType) -> Unit) {
    val word = pageData.word
    Column(horizontalAlignment = Alignment.Start) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = word.word.uppercase(),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.titleLarge
            )

            if (word.audio?.isNotEmpty() == true) {
                IconButton(
                    onClick = {},
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_audio),
                        contentDescription = null,
                        modifier = Modifier.size(52.dp),
                        //tint = TealPrimaryVariant.copy(alpha = .6f)
                    )
                }
            }

        }

        WordInfoOptions(
            availableTypes = pageData.availableFlashCars,
            selectedType = pageData.flashCardTypeWithCardIndex.first,
            onTypeSelected = onTypeSelected
        )

        Spacer(Modifier.height(8.dp))

        pageData.flashCardItems.isNotEmpty().let {
            FlashCards(items = pageData.flashCardItems, word.id)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WordInfoOptions(
    availableTypes: List<FlashCardType>,
    selectedType: FlashCardType,
    onTypeSelected: (FlashCardType) -> Unit
) {
    if (availableTypes.isEmpty()) return

    LazyRow(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),

    ) {
        items(availableTypes) { type ->
            // Use type-friendly display names
            val displayName = when (type) {
                FlashCardType.MEANING -> "Meaning"
                FlashCardType.SENTENCES -> "Sentences"
                FlashCardType.SYNONYMS -> "Synonyms"
                FlashCardType.ANTONYMS -> "Antonyms"
                FlashCardType.HYPERNYMS -> "Hypernyms"
                FlashCardType.CONTEXTS -> "Contexts"
                FlashCardType.EQUIVALENTS -> "Equivalents"
                FlashCardType.FORMS -> "Forms"
                FlashCardType.RELATED_WORDS -> "Related Words"
                FlashCardType.RHYMES -> "Rhymes"
            }

            FilterChip(
                shape = RoundedCornerShape(24.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = type.color,
                    selectedLabelColor = SurfaceLight
                ),
                selected = selectedType == type,
                onClick = { onTypeSelected(type) },
                label = {
                    Text(
                        displayName,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            )
        }
    }
}
/*
fun randomColor(): Color {
    val hue = Random.nextFloat() * 360f // Any hue
    val saturation = 0.4f + Random.nextFloat() * 0.2f // soft colors
    val lightness = 0.7f + Random.nextFloat() * 0.2f // light tones (0.7 - 0.9)

    val colorInt = ColorUtils.HSLToColor(floatArrayOf(hue, saturation, lightness))
    return Color(colorInt)
}*/


fun randomColor(isDarkTheme: Boolean): Color {
    return if (isDarkTheme) {
        generateDarkColor()
    } else {
        generateLightColor()
    }
}

fun generateLightColor(): Color {
    val hue = Random.nextFloat() * 360f
    val saturation = 0.3f + Random.nextFloat() * 0.2f     // Soft colors
    val lightness = 0.8f + Random.nextFloat() * 0.15f     // Light tones
    val colorInt = ColorUtils.HSLToColor(floatArrayOf(hue, saturation, lightness))
    return Color(colorInt)
}

fun generateDarkColor(): Color {
    val hue = Random.nextFloat() * 200f
    val saturation = Random.nextFloat() * 0.3f     // Soft colors
    val lightness = Random.nextFloat() * 0.4f     // Light tones
    val colorInt = ColorUtils.HSLToColor(floatArrayOf(hue, saturation, lightness))
    return Color(colorInt)
}







