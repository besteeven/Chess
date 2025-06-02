package project.chess.menu.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.component1
import androidx.core.graphics.component2
import androidx.core.graphics.component3
import project.chess.R
import project.chess.menu.ui.MainMenuDestinations.items
import project.chess.menu.viewmodel.HistoryViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Preview
@Composable
fun HistoryScreen(viewModel: HistoryViewModel = viewModel()) {
    val matches by viewModel.matchHistory.collectAsState()
    val username by viewModel.username.collectAsState()

    val totalMatches = matches.size
    val wins = matches.count { (you, result, _) ->
        (result == "win")
    }

    val winRate = if (totalMatches > 0)
        (wins * 100 / totalMatches)
    else 0


    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.history),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                itemsIndexed(matches) { _, match ->
                    val (you, result, opponent) = match

                    val didWin = (result == "win")
                    val status = if (didWin) stringResource(R.string.victory) else stringResource(R.string.lose)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(opponent, style = MaterialTheme.typography.bodyLarge)
                        Text(status, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }


            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = stringResource(R.string.matchplayed, totalMatches),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = stringResource(R.string.winrate, winRate),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}
