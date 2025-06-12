package com.vocable.auth

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.vocable.R
import com.vocable.data.auth.source.remote.GoogleAuthProvider
import com.vocable.home.randomColor
import com.vocable.ui.theme.TealPrimary
import org.koin.androidx.compose.getKoin
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber


@Composable
fun LoginScreen(navigateToDashboard: () -> Unit) {

    val viewmodel = koinViewModel<LoginViewModel>()
    val state = viewmodel.loginState.collectAsState()
    val activity = LocalContext.current as ComponentActivity
    val authProvider: GoogleAuthProvider =
        getKoin().get<GoogleAuthProvider> { parametersOf(activity) }


    when (val loginState = state.value) {
        LoginUiState.LoggedIn -> {
            LaunchedEffect(loginState) {
                navigateToDashboard.invoke()
            }
        }

        else -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()

            ) {


                LoginFlashCard(
                    size = 8,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .size(150.dp)
                )


                LoginFlashCard(
                    size = 14,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(150.dp)
                )

                LoginFlashCard(
                    size = 14,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(150.dp)
                )


                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f)
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.ic_logo),
                        contentDescription = "My drawable image",
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(horizontal = 16.dp, vertical = 48.dp)

                    )
                    /* Card(
                         modifier = Modifier
                             .fillMaxHeight(.8f)
                             .align(Alignment.TopStart)
                             .padding(start = 68.dp)
                             .width(8.dp),
                         shape = RoundedCornerShape(bottomStart = 48.dp, bottomEnd = 48.dp),
                         colors = CardDefaults.cardColors(containerColor = TealPrimary)
                     ) { }
 */

                    /* val items = "VOCABLE".toCharArray().toList()
                     LazyRow (
                         modifier = Modifier
                             .align(Alignment.BottomEnd)
                             .padding(vertical = 48.dp)
                     ) {
                         items(items.size) { index ->
                             val item = items[index]

                             Text(
                                 text = item.toString(),
                                 modifier = Modifier.padding(start = 24.dp),
                                 style = MaterialTheme.typography.titleLarge
                             )

                         }
                     }
 */

                    Button(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(horizontal = 24.dp)
                            .fillMaxWidth(),


                        onClick = {
                            viewmodel.login(authProvider)
                        }) {
                        if (loginState is LoginUiState.Loading) {
                            CircularProgressIndicator(color = Color.White)
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_google),
                                contentDescription = "Login Icon",
                                modifier = Modifier.size(24.dp) // adjust as needed
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text("Login/Register", style = MaterialTheme.typography.bodyMedium)

                        }

                    }


                    /* Card(
                         modifier = Modifier
                             .size(120.dp)
                             .align(Alignment.BottomCenter),
                         shape = CircleShape,
                         colors = CardDefaults.cardColors(containerColor = TealPrimary)
                     ) {
                         Box(
                             modifier = Modifier.fillMaxSize(),
                             contentAlignment = Alignment.Center
                         ) {
                             Text("info", style = MaterialTheme.typography.titleLarge)

                         }
                     }*/
                }


            }


        }

    }


}


@Composable
fun LoginSingleFlashCard(modifier: Modifier) {
    Card(
        modifier = modifier
            .height(250.dp)
            .width(250.dp)
            .rotate(45f)
            .offset(x = 54.dp, y = 24.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(48.dp),
        colors = CardDefaults.cardColors(containerColor = TealPrimary)
    ) {}
}

@Composable
fun LoginFlashCard(size: Int, modifier: Modifier) {
    val items = (0 until size).toList()
    val isDarkTheme = isSystemInDarkTheme()
    val colors = items.map { randomColor(isDarkTheme) }
    Timber.d("the items size is ${items.size}")
    Box(modifier = modifier) {
        items.reversed().forEachIndexed { reversedIndex, _ ->
            val index = size - reversedIndex - 1
            val color = colors[index].copy(alpha = 1f) // ensure no transparency
            1f + (index * 0.1f)
            val visualIndex = index
            Timber.d("the color is $color")
            val xOffset = 24.dp * visualIndex
            Box(
                modifier = Modifier
                    .fillMaxSize()

                    .offset(x = xOffset, y = xOffset)

                    .clip(RoundedCornerShape(32.dp))
                    .background(color)
                //.zIndex(-index.toFloat())
            ) {}
        }
    }


}

@Composable
fun LoginFeatures(modifier: Modifier, features: List<String>) {

    LazyColumn(
        modifier = modifier
            .fillMaxWidth(0.6f)
            .padding(vertical = 34.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {

        items(features.size) { index ->
            Card {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                ) {


                    Text(
                        index.toString(),
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(40.dp)

                    )
                    VerticalDivider()
                    Text(
                        text = features[index],
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()


                    )
                }

            }

        }
    }

}
