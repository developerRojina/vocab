package com.vocable.auth

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.vocable.data.auth.source.remote.GoogleAuthProvider
import org.koin.androidx.compose.getKoin
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

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
            Box() {
                Column(

                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(30.dp)
                ) {

                    Text(text = "V", style = MaterialTheme.typography.titleLarge)
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            viewmodel.login(authProvider)
                        }) {
                        Text("Login")
                    }
                }
            }
        }

    }


}