package project.chess.menu.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

data class Friend(val username: String, val elo: Int)

class FriendMatchViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val _friendList = MutableStateFlow<List<Friend>>(emptyList())
    val friendList: StateFlow<List<Friend>> = _friendList

    init {
        viewModelScope.launch {
            loadFriendList()
        }
    }

    private suspend fun loadFriendList() {
        val email = auth.currentUser?.email ?: return

        val userSnapshot = db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .await()
            .documents
            .firstOrNull() ?: return

        val currentUsername = userSnapshot.id
        val friendUsernames = userSnapshot.get("friends") as? List<String> ?: emptyList()

        val friendList = mutableListOf<Friend>()

        for (friendUsername in friendUsernames) {
            val friendDoc = db.collection("users").document(friendUsername).get().await()
            if (friendDoc.exists()) {
                val elo = friendDoc.getLong("elo")?.toInt() ?: 1000
                friendList.add(Friend(friendUsername, elo))
            }
        }
        Log.d("DEBUG", "LISTE DAMIS = $friendList.toString()")
        _friendList.value = friendList
    }

    fun sendChallenge(toUsername: String) {
        viewModelScope.launch {
            val fromEmail = Firebase.auth.currentUser?.email ?: return@launch
            val fromUsername = getUsernameByEmail(fromEmail) ?: return@launch

            val targetUserDoc = Firebase.firestore.collection("users").document(toUsername).get().await()
            val targetToken = targetUserDoc.getString("fcmToken") ?: return@launch

            val gameDoc = Firebase.firestore.collection("games").document()
            gameDoc.set(
                mapOf(
                    "white" to fromUsername,
                    "black" to toUsername,
                    "createdAt" to FieldValue.serverTimestamp()
                )
            ).await()

            val serverKey = "AAAA...TON_SERVER_KEY_ICI..." // 🔐 Garde-la privée

            val data = JSONObject().apply {
                put("to", targetToken)
                put("notification", JSONObject().apply {
                    put("title", "Défi d’échecs")
                    put("body", "$fromUsername vous a défié !")
                })
                put("data", JSONObject().apply {
                    put("from", fromUsername)
                    put("gameId", gameDoc.id)
                })
            }

            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .addHeader("Authorization", "key=$serverKey")
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create("application/json".toMediaType(), data.toString()))
                .build()

            client.newCall(request).execute()
        }
    }


    private suspend fun getUsernameByEmail(email: String): String? {
        val snapshot = db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .await()

        return snapshot.documents.firstOrNull()?.id
    }
}



