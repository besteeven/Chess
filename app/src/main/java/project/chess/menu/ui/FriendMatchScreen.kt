package project.chess.menu.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import project.chess.menu.component.MenuButton
import project.chess.menu.viewmodel.FriendMatchViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun FriendMatchScreen(
    navController: NavController,
    viewModel: FriendMatchViewModel = viewModel()
) {
    val friends by viewModel.friendList.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Jouer avec un ami",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        if (friends.isEmpty()) {
            Text("Aucun ami disponible")
        }

        friends.forEach { friend ->
            MenuButton(
                text = "${friend.username} (ELO: ${friend.elo})",
                onClick = {
                    viewModel.sendChallenge(friend.username)
                    Toast.makeText(context, "Demande envoyée à ${friend.username}", Toast.LENGTH_SHORT).show()
                },
                backgroundColor = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
