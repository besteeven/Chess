package project.chess.menu.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val _userData = MutableStateFlow<User?>(null)
    val userData = _userData.asStateFlow()

    init {
        loadUserData()
    }

    private fun getCurrentUsername(): String? {
        return auth.currentUser?.email?.let { email ->
            // Simplification si username = document ID
            // In practice, you might need reverse lookup
            _userData.value?.username
        }
    }

    private fun loadUserData() {
        val email = auth.currentUser?.email ?: return
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { snapshot ->
                val doc = snapshot.documents.firstOrNull()
                if (doc != null) {
                    val username = doc.id
                    val friends = doc.get("friends") as? List<String> ?: emptyList()
                    val requests = doc.get("friendRequests") as? List<String> ?: emptyList()
                    val elo = doc.getLong("elo")?.toInt() ?: 1450
                    _userData.value = User(username, friends, requests, elo)
                }
            }
    }

    fun sendFriendRequest(targetUsername: String) {
        val currentUsername = _userData.value?.username ?: return
        if (targetUsername == currentUsername || _userData.value?.friends?.contains(targetUsername) == true) return

        val targetRef = db.collection("users").document(targetUsername)
        targetRef.get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                val currentRequests = doc.get("friendRequests") as? MutableList<String> ?: mutableListOf()
                if (!currentRequests.contains(currentUsername)) {
                    currentRequests.add(currentUsername)
                    targetRef.update("friendRequests", currentRequests)
                }
            }
        }
    }

    fun acceptFriendRequest(from: String) {
        val user = _userData.value ?: return
        val docRef = db.collection("users").document(user.username)

        val updatedRequests = user.friendRequests.toMutableList().apply { remove(from) }
        val updatedFriends = user.friends.toMutableList().apply { if (!contains(from)) add(from) }

        docRef.update(
            mapOf(
                "friendRequests" to updatedRequests,
                "friends" to updatedFriends
            )
        )

        val otherRef = db.collection("users").document(from)
        otherRef.get().addOnSuccessListener { otherDoc ->
            val theirFriends = (otherDoc.get("friends") as? MutableList<String>) ?: mutableListOf()
            if (!theirFriends.contains(user.username)) {
                theirFriends.add(user.username)
                otherRef.update("friends", theirFriends)
            }
        }

        loadUserData()
    }

    fun rejectFriendRequest(from: String) {
        val user = _userData.value ?: return
        val updatedRequests = user.friendRequests.toMutableList().apply { remove(from) }
        db.collection("users").document(user.username).update("friendRequests", updatedRequests)
        loadUserData()
    }

    fun removeFriend(friend: String) {
        val user = _userData.value ?: return
        val updatedFriends = user.friends.toMutableList().apply { remove(friend) }
        db.collection("users").document(user.username).update("friends", updatedFriends)

        // Remove the current user from the other friend's list
        db.collection("users").document(friend).get().addOnSuccessListener { doc ->
            val theirFriends = (doc.get("friends") as? MutableList<String>) ?: mutableListOf()
            theirFriends.remove(user.username)
            db.collection("users").document(friend).update("friends", theirFriends)
        }

        loadUserData()
    }
}

data class User(
    val username: String,
    val friends: List<String>,
    val friendRequests: List<String>,
    val elo: Int = 1450
)


