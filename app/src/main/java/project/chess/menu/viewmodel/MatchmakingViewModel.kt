package project.chess.menu.viewmodel

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

            val result = tryFindOrCreateMatch(db, username, elo, type)

            if (result.waiting) {
                // Pas de match encore, attendre
                listenForMatch(username)
            } else {
                // Match trouvé et créé
                _gameId.value = result.gameId
                _matchFound.value = result.opponent
                _isWhite.value = result.isWhite
                _isSearching.value = false
            }
        }
    }

    private fun listenForMatch(username: String) {
        listener = db.collection("games")
            .whereEqualTo("white", username)
            .addSnapshotListener { snapshot, _ ->
                val game = snapshot?.documents?.firstOrNull() ?: return@addSnapshotListener
                val gameId = game.id
                val opponent = game.getString("black") ?: return@addSnapshotListener

                listener?.remove()

                _isSearching.value = false
                _gameId.value = gameId
                _matchFound.value = opponent
                _isWhite.value = true
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

