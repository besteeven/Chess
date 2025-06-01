package project.chess.menu.data

import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class MatchMakingUtils {


}
suspend fun findOrCreateMatch(currentUsername: String, type: String): Pair<String, Boolean> {
    val db = Firebase.firestore
    val matchmakingRef = db.collection("matchmaking")
    val gamesRef = db.collection("games")

    // Étape 1 : chercher un autre joueur
    val waitingSnapshot = matchmakingRef
        .whereEqualTo("type", type)
        .orderBy("timestamp")
        .get()
        .await()

    val opponentDoc = waitingSnapshot.documents.firstOrNull { it.id != currentUsername }

    return if (opponentDoc != null) {
        val opponentUsername = opponentDoc.id

        val gameId = db.runTransaction { transaction ->
            val gameDoc = gamesRef.document()
            val gameData = mapOf(
                "white" to opponentUsername,
                "black" to currentUsername,
                "createdAt" to FieldValue.serverTimestamp()
            )
            transaction.set(gameDoc, gameData)
            transaction.delete(matchmakingRef.document(opponentUsername))
            gameDoc.id
        }.await()

        Pair(gameId, false) // Tu es noir
    } else {
        // Ajouter soi-même dans la file d’attente
        val userSnapshot = db.collection("users").document(currentUsername).get().await()
        val elo = userSnapshot.getLong("elo")?.toInt() ?: 1000

        matchmakingRef.document(currentUsername).set(
            mapOf(
                "type" to type,
                "elo" to elo,
                "timestamp" to FieldValue.serverTimestamp()
            )
        ).await()

        Pair("", true) // En attente
    }
}

suspend fun tryFindOrCreateMatch(
    db: FirebaseFirestore,
    username: String,
    elo: Int,
    type: String
): MatchResult {
    val matchmakingRef = db.collection("matchmaking")
    val gamesRef = db.collection("games")

    val querySnapshot = matchmakingRef
        .whereEqualTo("type", type)
        .orderBy("timestamp")
        .get()
        .await()

    val opponentDoc = querySnapshot.documents.firstOrNull { doc ->
        val otherUsername = doc.getString("username")
        val otherElo = doc.getLong("elo")?.toInt() ?: 1000
        otherUsername != null &&
                otherUsername != username &&
                (type == "random" || kotlin.math.abs(otherElo - elo) < 150)
    }

    return if (opponentDoc != null) {
        val opponentUsername = opponentDoc.getString("username") ?: return MatchResult(waiting = true)

        // Crée un document de partie
        val gameDoc = gamesRef.document()
        val gameData = mapOf(
            "white" to opponentUsername,
            "black" to username,
            "createdAt" to FieldValue.serverTimestamp()
        )
        gameDoc.set(gameData).await()

        // Supprime les entrées de matchmaking
        matchmakingRef.document(opponentDoc.id).delete().await()

        MatchResult(
            gameId = gameDoc.id,
            isWhite = false,
            opponent = opponentUsername,
            waiting = false
        )
    } else {
        // Pas d'adversaire : entre dans la file d'attente
        val myDoc = matchmakingRef.document(username)
        myDoc.set(
            mapOf(
                "type" to type,
                "username" to username,
                "elo" to elo,
                "timestamp" to FieldValue.serverTimestamp()
            )
        ).await()

        MatchResult(waiting = true)
    }
}
data class MatchResult(
    val gameId: String? = null,
    val opponent: String? = null,
    val isWhite: Boolean? = null,
    val waiting: Boolean = false
)
