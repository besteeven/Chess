package project.chess.menu.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import project.chess.menu.data.findOrCreateMatch
import project.chess.menu.data.tryFindOrCreateMatch

class MatchmakingViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val _isSearching = mutableStateOf(false)
    val isSearching: State<Boolean> = _isSearching

    private val _matchFound = MutableStateFlow<String?>(null)
    val matchFound: StateFlow<String?> = _matchFound

    private val _gameId = MutableStateFlow<String?>(null)
    val gameId: StateFlow<String?> = _gameId

    private val _isWhite = MutableStateFlow<Boolean?>(null)
    val isWhite: StateFlow<Boolean?> = _isWhite

    private var listener: ListenerRegistration? = null

    fun startSearch(type: String) {
        _isSearching.value = true
        _matchFound.value = null
        _gameId.value = null
        _isWhite.value = null

        val email = auth.currentUser?.email ?: return

        viewModelScope.launch {
            val username = getUsernameByEmail(email) ?: run {
                _isSearching.value = false
                return@launch
            }

            val userDoc = db.collection("users").document(username).get().await()
            val elo = userDoc.getLong("elo")?.toInt() ?: 1000

            val matchmakingRef = db.collection("matchmaking")
            matchmakingRef.document(username).delete().await() // Clear any old search

            // Cherche un autre joueur déjà en attente
            val waitingPlayer = matchmakingRef
                .orderBy("timestamp")
                .get()
                .await()
                .documents
                .firstOrNull { doc ->
                    val otherUsername = doc.getString("username")
                    val otherElo = doc.getLong("elo")?.toInt() ?: 1000
                    otherUsername != null &&
                            doc.id != username &&
                            (type == "random" || kotlin.math.abs(otherElo - elo) < 150)
                }

            if (waitingPlayer != null) {
                val opponent = waitingPlayer.getString("username") ?: return@launch

                val gameDoc = db.collection("games").document()
                gameDoc.set(
                    mapOf(
                        "white" to opponent,
                        "black" to username,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                ).await()

                matchmakingRef.document(waitingPlayer.id).delete().await()

                _isSearching.value = false
                _matchFound.value = opponent
                _gameId.value = gameDoc.id
                _isWhite.value = false

            } else {
                // Tu es en attente
                matchmakingRef.document(username).set(
                    mapOf(
                        "username" to username,
                        "elo" to elo,
                        "type" to type,
                        "timestamp" to FieldValue.serverTimestamp()
                    )
                ).await()

                // 🔁 Poll toutes les 2 secondes pour voir si une partie t'a été assignée
                while (true) {
                    delay(2000)

                    val possibleGame = db.collection("games")
                        .whereEqualTo("white", username)
                        .get()
                        .await()
                        .documents
                        .firstOrNull()

                    if (possibleGame != null) {
                        val opponent = possibleGame.getString("black") ?: continue
                        _matchFound.value = opponent
                        _gameId.value = possibleGame.id
                        _isWhite.value = true
                        _isSearching.value = false

                        // Supprime ton entrée matchmaking
                        db.collection("matchmaking").document(username).delete()
                        break
                    }
                }
            }
        }
    }


    fun cancelSearch() {
        _isSearching.value = false
        val email = auth.currentUser?.email ?: return

        viewModelScope.launch {
            val username = getUsernameByEmail(email) ?: return@launch
            db.collection("matchmaking").document(username).delete().await()
        }

        listener?.remove()
    }

    override fun onCleared() {
        super.onCleared()
        cancelSearch()
    }
}

suspend fun getUsernameByEmail(email: String): String? {
    val db = Firebase.firestore
    val result = db.collection("users")
        .whereEqualTo("email", email)
        .get()
        .await()
    return result.documents.firstOrNull()?.id
}

