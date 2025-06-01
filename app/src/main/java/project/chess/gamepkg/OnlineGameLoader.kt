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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import project.chess.online.OnlineGameScreen

@Composable
fun OnlineGameLoader(gameId: String) {
    val db = Firebase.firestore
    var isWhite by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(gameId) {
        val currentUsername = getCurrentUsername()
        val docSnapshot = db.collection("games").document(gameId).get().await()
        val white = docSnapshot.getString("white")
        val black = docSnapshot.getString("black")

        isWhite = when (currentUsername) {
            white -> true
            black -> false
            else -> null // Cas d'erreur : ni blanc ni noir
        }
    }

    when (isWhite) {
        true, false -> OnlineGameScreen(gameId = gameId, isWhite = isWhite!!)
        null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

suspend fun getCurrentUsername(): String? {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    val email = auth.currentUser?.email ?: return null

    val result = db.collection("users")
        .whereEqualTo("email", email)
        .get()
        .await()

    return result.documents.firstOrNull()?.id
}


