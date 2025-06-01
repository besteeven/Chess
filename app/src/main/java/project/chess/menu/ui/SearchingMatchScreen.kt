package project.chess.menu.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import project.chess.menu.viewmodel.MatchmakingViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import project.chess.R

@Composable
fun SearchingMatchScreen(
    navController: NavController,
    viewModel: MatchmakingViewModel = viewModel()
) {
    val isSearching by viewModel.isSearching
    val matchFound by viewModel.matchFound.collectAsState()
    val gameId by viewModel.gameId.collectAsState()
    val isWhite by viewModel.isWhite.collectAsState()
    Log.d("SearchingScreen", "GameId: $gameId, IsWhite: $isWhite")
    LaunchedEffect(gameId, isWhite) {
        if (gameId != null && isWhite != null) {
            navController.navigate("online_game/${gameId}/${isWhite}")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isSearching) {
            CircularProgressIndicator()
            Text("Recherche d'un adversaire...")

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                viewModel.cancelSearch()
                navController.popBackStack()
            }) {
                Text("Annuler")
            }
        } else if (matchFound != null) {
            Text("Adversaire trouvé : ${matchFound}")
        }
    }
}

