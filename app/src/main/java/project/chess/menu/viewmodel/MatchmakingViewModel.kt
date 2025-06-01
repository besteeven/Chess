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

class MatchmakingViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val username = auth.currentUser?.displayName ?: auth.currentUser?.email ?: "unknown"

    private var listener: ListenerRegistration? = null

    private val _isSearching = mutableStateOf(false)
    val isSearching: State<Boolean> = _isSearching

    private val _matchFound = MutableStateFlow<String?>(null)
    val matchFound: StateFlow<String?> = _matchFound

    private var currentSearchId: String? = null

    private val _gameId = MutableStateFlow<String?>(null)
    val gameId: StateFlow<String?> = _gameId

    fun resetMatchmakingState() {
        _matchFound.value = null
        _gameId.value = null
    }


    fun startSearch(type: String) {
        _isSearching.value = true
        _matchFound.value = null

        val email = auth.currentUser?.email ?: run {
            _isSearching.value = false
            return
        }

        // Étape 1 : Trouver le document user avec cet email
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { result ->
                val userDoc = result.documents.firstOrNull()
                if (userDoc == null) {
                    _isSearching.value = false
                    return@addOnSuccessListener
                }

                val username = userDoc.id
                val elo = userDoc.getLong("elo")?.toInt() ?: 1000

                // Étape 2 : écouter la file d’attente
                listener = db.collection("matchmaking")
                    .whereEqualTo("type", type)
                    .orderBy("timestamp")
                    .addSnapshotListener { snapshot, _ ->
                        val match = snapshot?.documents?.firstOrNull { doc ->
                            val otherUsername = doc.getString("username")
                            val otherElo = doc.getLong("elo")?.toInt() ?: 1000
                            otherUsername != null &&
                                    otherUsername != username &&
                                    (type == "random" || kotlin.math.abs(otherElo - elo) < 150)
                        }

                        if (match != null) {
                            val opponentUsername = match.getString("username") ?: return@addSnapshotListener

                            val gameDoc = db.collection("games").document()
                            val newGameId = gameDoc.id

                            gameDoc.set(
                                mapOf(
                                    "white" to username,
                                    "black" to opponentUsername,
                                    "createdAt" to FieldValue.serverTimestamp()
                                )
                            )

                            db.collection("matchmaking").document(match.id).delete()
                            currentSearchId?.let {
                                db.collection("matchmaking").document(it).delete()
                            }

                            listener?.remove()
                            _isSearching.value = false
                            _matchFound.value = opponentUsername
                            _gameId.value = newGameId
                        }
                    }

                // Étape 3 : ajouter son entrée à la file
                val doc = db.collection("matchmaking").document()
                currentSearchId = doc.id
                doc.set(
                    mapOf(
                        "type" to type,
                        "username" to username,
                        "elo" to elo,
                        "timestamp" to FieldValue.serverTimestamp()
                    )
                )
            }
            .addOnFailureListener {
                _isSearching.value = false
            }
    }



    fun cancelSearch() {
        _isSearching.value = false
        currentSearchId?.let {
            db.collection("matchmaking").document(it).delete()
        }
        listener?.remove()
    }

    override fun onCleared() {
        super.onCleared()
        cancelSearch()
    }
}
