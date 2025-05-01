package com.vocable.dashboard

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.vocable.BottomNavItem
import com.vocable.Screen
import com.vocable.home.HomeScreen
import com.vocable.profile.ProfileScreen
import com.vocable.quiz.QuizScreen
import com.vocable.settings.SettingsScreen
import com.vocable.ui.theme.VocableTheme

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        // bottomBar = { BottomNavBar(navController) }

    ) { contentPadding ->
        NavHost(
            navController = navController,
            modifier = Modifier.padding(contentPadding),
            startDestination = Screen.Home.route
        ) {
            composable(Screen.Home.route) {
                HomeScreen {
                    navController.navigate(Screen.Profile.route)
                }
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
            composable(Screen.Profile.route) {
                ProfileScreen(onNavigateToQuiz = {
                    navController.navigate(Screen.Quiz.route)
                }, onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                })
            }
            composable(Screen.Quiz.route) {
                QuizScreen()
            }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("Home", Screen.Home.route, Icons.Default.Home),
        BottomNavItem("Profile", Screen.Profile.route, Icons.Default.Search),
        BottomNavItem("Settings", Screen.Settings.route, Icons.Default.Person)
    )
    NavigationBar {
        val currentDestination = navController.currentBackStackEntryAsState().value?.destination

        items.forEach { item ->
            val selected = currentDestination?.route == item.route

            NavigationBarItem(
                label = { Text(item.name) },
                onClick = {
                    navController.navigate(item.route)
                },
                selected = selected,
                icon = { item.icon }
            )

        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    VocableTheme {
        //DashboardScreen()
    }
}