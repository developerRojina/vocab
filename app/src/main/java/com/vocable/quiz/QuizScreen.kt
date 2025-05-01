package com.vocable.quiz

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import com.vocable.data.quiz.domain.model.Quiz
import com.vocable.data.quiz.domain.model.QuizType
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPagerApi::class)
@Composable
fun QuizScreen() {
    val viewmodel = koinViewModel<QuizViewModel>()
    val state = viewmodel.state.collectAsState().value

    // total 10 questions
    // What are the words you were assigned today?
    // Select the proper meaning of the word (1-3)
    // Select correct Spelling (1-3)
    //
    val pagerState = rememberPagerState()

    VerticalPager(
        count = state.quizzes.size, state = pagerState,
        modifier = Modifier.fillMaxSize(),
        userScrollEnabled = false
    ) { page ->
        val quiz = state.quizzes.get(page)
        QuizView(quiz)
    }

}


@Composable
fun QuizView(quiz: Quiz) {

    val coroutineScope = rememberCoroutineScope()
    var progress by remember { mutableStateOf(0f) }

    // Launch a timer effect
    LaunchedEffect(Unit) {
        val startTime = withFrameNanos { it }
        while (progress < 1f) {
            val currentTime = withFrameNanos { it }
            progress = ((currentTime - startTime) / 3000.toFloat() / 1_000_000f).coerceIn(0f, 1f)
        }
    }

    // Animate the progress change
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 300),
        label = "fillProgress"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray) // base
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(fraction = animatedProgress)
                .align(Alignment.BottomStart) // so it fills from bottom
                .background(Color.Green) // animated fill
        )
    }
    Column {
        when (quiz.quizType) {
            QuizType.USER_INPUT -> QuizScreenUserType(quiz)
            QuizType.CHOICE -> QuizScreenChoice(quiz)
        }
    }

}


@Composable
fun ColumnScope.QuizScreenUserType(quiz: Quiz) {
    Text(text = quiz.question)
    quiz.answers.map {
        TextField(value = "", onValueChange = {})
    }


}

@Composable
fun ColumnScope.QuizScreenChoice(quiz: Quiz) {
    Text(text = quiz.question)
}