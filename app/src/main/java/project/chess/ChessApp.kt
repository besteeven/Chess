package project.chess

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import project.chess.core.navigation.AppNavHost
import project.chess.core.theme.Theme


@Composable
fun ChessApp(modifier: Modifier = Modifier) {
    Theme {
        Surface {
            AppNavHost() // Ce sera ton système de navigation global
        }
    }

}