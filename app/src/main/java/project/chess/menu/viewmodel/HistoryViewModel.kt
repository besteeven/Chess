package project.chess.menu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HistoryViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    private val _matchHistory = MutableStateFlow<List<Triple<String, String, String>>>(emptyList())
    val matchHistory: StateFlow<List<Triple<String, String, String>>> = _matchHistory

    init {
        viewModelScope.launch {
            loadMatchHistory()
        }
    }

    private suspend fun loadMatchHistory() {
        val email = auth.currentUser?.email ?: return

        val userDoc = db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .await()
            .documents
            .firstOrNull() ?: return

        val currentUsername = userDoc.id
        _username.value = currentUsername

        val historySnapshot = db.collection("users")
            .document(currentUsername)
            .collection("historic")
            .orderBy("timestamp")
            .get()
            .await()

        val history = historySnapshot.documents.mapNotNull { doc ->
            val color = doc.getString("color") ?: return@mapNotNull null
            val result = doc.getString("result") ?: return@mapNotNull null
            val opponent = doc.getString("opponent") ?: return@mapNotNull null

            Triple(color, result, opponent)
        }

        _matchHistory.value = history.reversed() // Pour afficher les plus récents en haut
    }
}
