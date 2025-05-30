package project.chess.menu.ui

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import project.chess.core.theme.Theme
import project.chess.menu.component.BottomBar

object MainMenuDestinations {
    val items = listOf(
        MainMenuDestination.Home,
        MainMenuDestination.History,
        MainMenuDestination.Profile,
        MainMenuDestination.Ranking,
        MainMenuDestination.Settings
    )
}

@Preview
@Composable
fun MainMenuScreen() {
    Theme {
        val navController = rememberNavController()
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
            NavHost(
                navController = navController,
                startDestination = MainMenuDestination.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(MainMenuDestination.Home.route) {
                    HomeScreen()
                }
                composable(MainMenuDestination.History.route) {
                    HistoryScreen()
                }
                composable(MainMenuDestination.Profile.route) {
                    ProfileScreen()
                }
                composable(MainMenuDestination.Ranking.route) {
                    RankingScreen()
                }
                composable(MainMenuDestination.Settings.route) {
                    SettingsScreen()
                }
            }
        }

        // 🔐 Bloquer le retour
        BackHandler(enabled = true) {
            // Ne fait rien
        }
    }
}


sealed class MainMenuDestination(val route: String, val icon: ImageVector, val label: String) {
    object Home : MainMenuDestination("home", Icons.Default.Home, "Accueil")
    object History : MainMenuDestination("history", Icons.Default.History, "Historique")
    object Profile : MainMenuDestination("profile", Icons.Default.Person, "Profil")
    object Ranking : MainMenuDestination("Ranking", Icons.Default.Star, "Classement")
    object Settings : MainMenuDestination("settings", Icons.Default.Settings, "Paramètres")
}