package project.chess.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import project.chess.auth.ui.LoginScreen
import project.chess.auth.ui.SignupScreen
import project.chess.menu.ui.MainMenuScreen

object Routes {
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val MENU = "menu"
}

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
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

        composable(Routes.MENU) {
            MainMenuScreen()
        }
    }
}