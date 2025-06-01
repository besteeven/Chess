package project.chess.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import project.chess.auth.ui.LoginScreen
import project.chess.auth.ui.SignupScreen
import project.chess.gamepkg.LocalGameScreen
import project.chess.gamepkg.OnlineGameLoader
import project.chess.menu.ui.FriendMatchScreen
import project.chess.menu.ui.HistoryScreen
import project.chess.menu.ui.HomeScreen
import project.chess.menu.ui.MainMenuDestination
import project.chess.menu.ui.MainMenuScreen
import project.chess.menu.ui.MatchTypeSelectionScreen
import project.chess.menu.ui.ProfileScreen
import project.chess.menu.ui.RankingScreen
import project.chess.menu.ui.SearchingMatchScreen
import project.chess.menu.ui.SettingsScreen
import project.chess.menu.viewmodel.FriendMatchViewModel
import project.chess.menu.viewmodel.MatchmakingViewModel
import project.chess.online.OnlineGameScreen

object Routes {
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val MENU = "menu"
}

object OnlineRoutes {
    const val MatchType = "match_type"
    const val Searching = "searching"
}

object FriendRoutes{
    const val FriendSearch = "friend_search"
}


@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
            initialGameId: String? = null,
    isChallenge: Boolean = false
) {
    val matchmakingViewModel: MatchmakingViewModel = viewModel()

    LaunchedEffect(initialGameId, isChallenge) {
        if (isChallenge && initialGameId != null) {
            navController.navigate("online_game/${initialGameId}/false")
        }
    }


    NavHost(navController = navController, startDestination = Routes.LOGIN) {

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Routes.MENU) },
                onSignupClick = { navController.navigate(Routes.SIGNUP) }
            )
        }
        composable(Routes.SIGNUP) {
            SignupScreen(
                onSignupSuccess = { navController.navigate(Routes.MENU) },
                onLoginClick = { navController.popBackStack() }
            )
        }

        navigation(startDestination = MainMenuDestination.Home.route, route = Routes.MENU) {
            composable(MainMenuDestination.Home.route) {
                MainMenuScreen(
                    navController = navController,
                    content = { HomeScreen(navController = navController) }
                )
            }
            composable(MainMenuDestination.History.route) {
                MainMenuScreen(
                    navController = navController,
                    content = { HistoryScreen() }
                )
            }
            composable(MainMenuDestination.Profile.route) {
                MainMenuScreen(
                    navController = navController,
                    content = { ProfileScreen() }
                )
            }
            composable(MainMenuDestination.Ranking.route) {
                MainMenuScreen(
                    navController = navController,
                    content = { RankingScreen() }
                )
            }
            composable(MainMenuDestination.Settings.route) {
                MainMenuScreen(
                    navController = navController,
                    content = {
                        SettingsScreen(
                            navController = navController
                        )
                    }
                )
            }

            composable(OnlineRoutes.MatchType) {
                MatchTypeSelectionScreen(navController, matchmakingViewModel)
            }
            composable(OnlineRoutes.Searching) {
                SearchingMatchScreen(navController, matchmakingViewModel)
            }

            composable("localGame") {
                LocalGameScreen(onGameEnd = {
                    navController.popBackStack()
                })
            }

            composable("online_game/{gameId}/{isWhite}") { backStackEntry ->
                val gameId = backStackEntry.arguments?.getString("gameId") ?: return@composable
                val isWhite = backStackEntry.arguments?.getString("isWhite")?.toBooleanStrictOrNull() ?: return@composable
                OnlineGameScreen(gameId = gameId, isWhite = isWhite)
            }

            composable(FriendRoutes.FriendSearch) {
                FriendMatchScreen(
                    navController = navController
                )
            }

        }

    }
}