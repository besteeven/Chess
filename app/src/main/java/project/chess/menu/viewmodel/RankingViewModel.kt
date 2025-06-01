package project.chess.menu.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RankingViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _ranking = MutableStateFlow<List<Pair<String, Int>>>(emptyList())
    val ranking = _ranking.asStateFlow()

    private val _currentUserPosition = MutableStateFlow<Triple<Int, String, Int>?>(null)
    val currentUserPosition = _currentUserPosition.asStateFlow()


    init {
        loadRanking()
    }

    private fun loadRanking() {
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                val players = result.documents.mapNotNull { doc ->
                    val username = doc.id
                    val elo = doc.getLong("elo")?.toInt() ?: return@mapNotNull null
                    username to elo
                }.sortedByDescending { it.second }

                _ranking.value = players

                val currentEmail = auth.currentUser?.email
                val currentUser = result.firstOrNull { it.getString("email") == currentEmail }

                if (currentUser != null) {
                    val username = currentUser.id
                    val elo = currentUser.getLong("elo")?.toInt() ?: 0
                    val index = players.indexOfFirst { it.first == username }
                    if (index != -1) {
                        _currentUserPosition.value = Triple(index + 1, username, elo)
                    }
                }

            }
    }
}
