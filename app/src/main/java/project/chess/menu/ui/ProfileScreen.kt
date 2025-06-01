package project.chess.menu.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Minimize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import project.chess.R
import project.chess.menu.viewmodel.ProfileViewModel
import androidx.lifecycle.viewmodel.compose.viewModel


@Preview
@Composable
fun ProfileScreen(viewModel: ProfileViewModel = viewModel()) {
    val userData by viewModel.userData.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Infos(
            username = userData?.username ?: stringResource(R.string.loading),
            elo = userData?.elo.toString(),
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.2f)
        )

        FriendList(
            viewModel = viewModel,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.3f)
        )

        SkinSelect(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
        )
    }
}



@Composable
fun FriendList(viewModel: ProfileViewModel, modifier: Modifier = Modifier) {
    val userData by viewModel.userData.collectAsState()
    var showPopup by remember { mutableStateOf(false) }
    var newFriend by remember { mutableStateOf("") }

    Card(
        modifier = modifier
            .fillMaxWidth()
        .padding(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary),
        shape = RoundedCornerShape(0.dp),
        ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.friends), style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = { showPopup = true }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Ajouter un ami")
                }
            }
            HorizontalDivider(thickness = 1.dp)
            userData?.friends?.forEach { friend ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("• $friend")
                    IconButton(onClick = { viewModel.removeFriend(friend) }) {
                        Icon(
                            imageVector = Icons.Default.Minimize,
                            contentDescription = "Retirer"
                        )
                    }
                }
            }

            if (showPopup) {
                AlertDialog(
                    onDismissRequest = { showPopup = false },
                    title = { Text(text = stringResource(R.string.addfriends)) },
                    text = {
                        Column {
                            Text(text = stringResource(R.string.friendrequests))
                            userData?.friendRequests?.forEach { requester ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("- $requester")
                                    Row {
                                        IconButton(onClick = {
                                            viewModel.acceptFriendRequest(requester)
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = stringResource(R.string.accept)
                                            )
                                        }
                                        IconButton(onClick = {
                                            viewModel.rejectFriendRequest(requester)
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Minimize,
                                                contentDescription = stringResource(R.string.refuse),
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = newFriend,
                                onValueChange = { newFriend = it },
                                label = { Text(stringResource(R.string.pseudo)) }
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.sendFriendRequest(newFriend)
                            newFriend = ""
                            showPopup = false
                        }) {
                            Text(text = stringResource(R.string.send))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showPopup = false }) {
                            Text(text = stringResource(R.string.cancel))
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SkinSelect(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Sélection de skin (WIP)", style = MaterialTheme.typography.bodyLarge)
        }
    }
}


@Composable
fun Infos(username: String, elo: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.profil),
            contentDescription = "Avatar",
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                username,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                "ELO : $elo",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}


