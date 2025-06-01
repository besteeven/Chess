package project.chess.menu.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import project.chess.R
import project.chess.core.navigation.OnlineRoutes
import project.chess.core.theme.Theme
import project.chess.menu.component.MenuButton

@Preview
@Composable
fun DrawPreview(modifier: Modifier = Modifier) {
    Theme{
        HomeScreen()
    }
}

@Composable
fun HomeScreen(navController: NavController = rememberNavController()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val buttonModifier = Modifier
            .weight(1f) // ← Prend une part équitable de l'espace vertical
            .fillMaxWidth()

        MenuButton(
            text = stringResource(R.string.local_game),
            onClick = {},
            iconPainter = painterResource(id = R.drawable.local),
            backgroundColor = MaterialTheme.colorScheme.secondary,
            textSize = 50.sp,
            pngSize = 70.dp,
            modifier = buttonModifier
        )

        MenuButton(
            text = stringResource(R.string.online_game),
            onClick = {navController.navigate(OnlineRoutes.MatchType)},
            iconPainter = painterResource(id = R.drawable.online),
            backgroundColor = MaterialTheme.colorScheme.secondary,
            textSize = 50.sp,
            pngSize = 70.dp,
            modifier = buttonModifier
        )

        MenuButton(
            text = stringResource(R.string.async_game),
            onClick = {},
            iconPainter = painterResource(id = R.drawable.async),
            backgroundColor = MaterialTheme.colorScheme.secondary,
            textSize = 50.sp,
            pngSize = 70.dp,
            modifier = buttonModifier
        )

        MenuButton(
            text = stringResource(R.string.friendly_game),
            onClick = {},
            iconPainter = painterResource(id = R.drawable.friendly),
            backgroundColor = MaterialTheme.colorScheme.secondary,
            textSize = 50.sp,
            pngSize = 70.dp,
            modifier = buttonModifier
        )
    }
}

