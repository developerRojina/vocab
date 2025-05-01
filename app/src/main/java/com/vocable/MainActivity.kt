package com.vocable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.notification.Constants.EXTRA_NOTIFICATION_TYPE
import com.notification.domain.model.NotificationType
import com.vocable.auth.LoginScreen
import com.vocable.dashboard.DashboardScreen
import com.vocable.ui.theme.VocableTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class MainActivity : ComponentActivity() {
    //  val receiver: LocalNotificationReceiver by inject()

    val viewmodel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        getPendingIntents()

        Timber.d("inside oncreate ${intent}")

        setContent {
            VocableTheme {
                val navController = rememberNavController()
                val currentUser = viewmodel.currentUser.collectAsState()
                val startDestination = if (currentUser.value == null) {
                    Screen.Login.route
                } else {
                    Screen.Dashboard.route
                }

                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {
                    composable(Screen.Dashboard.route) {
                        DashboardScreen()
                    }
                    composable(Screen.Login.route) {
                        LoginScreen {
                            navController.navigate(Screen.Dashboard.route)
                        }
                    }

                }

            }
        }
    }

    fun getPendingIntents() {
        if (intent.hasExtra(EXTRA_NOTIFICATION_TYPE)) {
            val type =
                intent.getStringExtra(EXTRA_NOTIFICATION_TYPE)?.let { NotificationType.valueOf(it) }
            if (type == NotificationType.NEW_WORDS) {
                viewmodel.updateWords()
            }
        }
    }
}



@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    VocableTheme {
        Greeting("Android")
    }
}