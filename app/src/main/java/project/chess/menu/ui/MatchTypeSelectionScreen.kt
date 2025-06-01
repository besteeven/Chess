package project.chess.menu.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import project.chess.menu.component.MenuButton
import project.chess.menu.viewmodel.MatchmakingViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import project.chess.R

@Composable
fun MatchTypeSelectionScreen(
    navController: NavController,
    viewModel: MatchmakingViewModel = viewModel()
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.gametype),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        MenuButton(
            text = stringResource(R.string.ranked),
            onClick = {
                viewModel.startSearch("ranked")
                navController.navigate("searching")
            },
            backgroundColor = MaterialTheme.colorScheme.secondary
        )

        MenuButton(
            text = stringResource(R.string.unranked),
            onClick = {
                viewModel.startSearch("unranked")
                navController.navigate("searching")
            },
            backgroundColor = MaterialTheme.colorScheme.secondary
        )

        MenuButton(
            text = stringResource(R.string.random),
            onClick = {
                viewModel.startSearch("random")
                navController.navigate("searching")
            },
            backgroundColor = MaterialTheme.colorScheme.secondary
        )
    }
}
