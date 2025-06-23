package com.vocable

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vocable.auth.LoginScreen
import com.vocable.dashboard.DashboardScreen
import com.vocable.notification.Constants.EXTRA_NOTIFICATION_TYPE
import com.vocable.notification.domain.model.NotificationType
import com.vocable.ui.theme.VocableTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val settingsIntent: Intent =
                    Intent(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, packageName)
                startActivity(settingsIntent)
            }

        }
    }

    val viewmodel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        getPendingIntents()
        requestNotificationPermission()

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
                        DashboardScreen {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Dashboard.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                    composable(Screen.Login.route) {
                        LoginScreen {
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                                launchSingleTop = true
                            }
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
              //  viewmodel.updateWords()
            }
        }
    }


    private fun requestNotificationPermission() {
        if (!isPermissionGranted(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

fun isPermissionGranted(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else true
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