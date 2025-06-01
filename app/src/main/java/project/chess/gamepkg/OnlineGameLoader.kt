package project.chess.gamepkg

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import project.chess.online.OnlineGameScreen

@Composable
fun OnlineGameLoader(gameId: String) {
    val context = LocalContext.current
    val db = Firebase.firestore
    val auth = Firebase.auth
    val currentUsername = auth.currentUser?.displayName ?: auth.currentUser?.email ?: "unknown"

    var isWhite by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(gameId) {
        db.collection("games").document(gameId).get()
            .addOnSuccessListener { document ->
                val white = document.getString("white")
                val black = document.getString("black")

                isWhite = when (currentUsername) {
                    white -> true
                    black -> false
                    else -> null
                }
            }
    }

    if (isWhite != null) {
        OnlineGameScreen(gameId = gameId, isWhite = isWhite!!)
    } else {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
