package project.chess.menu.ui

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import project.chess.core.theme.Theme
import project.chess.menu.component.BottomBar
import project.chess.R


object MainMenuDestinations {
    val items = listOf(
        MainMenuDestination.Home,
        MainMenuDestination.History,
        MainMenuDestination.Profile,
        MainMenuDestination.Ranking,
        MainMenuDestination.Settings
    )
}

@Composable
fun MainMenuScreen(
    navController: NavHostController = rememberNavController(),
    content: @Composable () -> Unit
) {
    Theme {
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentRoute = currentBackStack?.destination?.route

        Scaffold(
            bottomBar = {
                BottomBar(
                    navController = navController,
                    items = MainMenuDestinations.items,
                    currentRoute = currentRoute
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                content()
            }
        }

        BackHandler(enabled = true) {
            // Ne fait rien
        }
    }
}


sealed class MainMenuDestination(val route: String, val icon: ImageVector, @StringRes val labelRes: Int) {
    object Home : MainMenuDestination("home", Icons.Default.Home, R.string.home)
    object History : MainMenuDestination("history", Icons.Default.History, R.string.history)
    object Profile : MainMenuDestination("profile", Icons.Default.Person, R.string.profile)
    object Ranking : MainMenuDestination("Ranking", Icons.Default.Star, R.string.ranking)
    object Settings : MainMenuDestination("settings", Icons.Default.Settings, R.string.settings)
}