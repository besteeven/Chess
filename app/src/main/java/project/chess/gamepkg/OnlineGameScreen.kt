
package project.chess.online

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import project.chess.entities.Case
import project.chess.entities.Couleur
import project.chess.entities.Plateau
import project.chess.gamepkg.ChessBoardUI

@Composable
fun OnlineGameScreen(gameId: String, isWhite: Boolean) {
    val db = Firebase.firestore
    val auth = Firebase.auth
    val username = auth.currentUser?.displayName ?: auth.currentUser?.email ?: "unknown"

    val plateau = remember { Plateau() }
    val board = remember { mutableStateOf(plateau.getBoardMatrix()) }
    var selectedCase by remember { mutableStateOf<Case?>(null) }
    var possibleMoves by remember { mutableStateOf<List<Case>>(emptyList()) }
    var tour by remember { mutableStateOf(Couleur.BLANC) }
    var endMessage by remember { mutableStateOf<String?>(null) }

    val myColor = if (isWhite) Couleur.BLANC else Couleur.NOIR
    val coroutineScope = rememberCoroutineScope()

    var moveListener: ListenerRegistration? by remember { mutableStateOf(null) }

    // Reçoit les coups de l’adversaire
    LaunchedEffect(gameId) {
        moveListener = db.collection("games")
            .document(gameId)
            .collection("moves")
            .addSnapshotListener { snapshot, _ ->
                val lastMove = snapshot?.documents
                    ?.mapNotNull {
                        val from = it.getString("from")
                        val to = it.getString("to")
                        if (from != null && to != null) from to to else null
                    }
                    ?.lastOrNull() ?: return@addSnapshotListener

                val (from, to) = lastMove
                val startCase = plateau.getCase(from)
                if (startCase?.piece?.couleur != myColor) {
                    plateau.applyMove(from, to)
                    tour = myColor
                    board.value = plateau.getBoardMatrix()
                    checkGameState(plateau, myColor) { endMessage = it }
                }
            }
    }


    DisposableEffect(Unit) {
        onDispose {
            moveListener?.remove()
        }
    }

    fun sendMove(from: String, to: String) {
        val moveData = mapOf(
            "from" to from,
            "to" to to,
            "timestamp" to FieldValue.serverTimestamp(),
            "player" to username
        )
        db.collection("games")
            .document(gameId)
            .collection("moves")
            .add(moveData)
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column {
            Text(
                text = "Tour : ${if (tour == Couleur.BLANC) "Blanc" else "Noir"}",
                fontSize = 20.sp,
                modifier = Modifier.padding(8.dp)
            )
            ChessBoardUI(
                board = board.value,
                selectedCase = selectedCase,
                possibleMoves = possibleMoves,
                onCaseClick = { x, y ->
                    if (tour != myColor || endMessage != null) return@ChessBoardUI
                    val case = plateau.cases[y][x] ?: return@ChessBoardUI
                    val piece = case.piece

                    if (selectedCase == null) {
                        if (piece != null && piece.couleur == myColor) {
                            selectedCase = case
                            possibleMoves = piece.getMouvementsValides()
                        }
                    } else {
                        if (case in possibleMoves) {
                            val from = selectedCase!!.toString()
                            val to = case.toString()
                            plateau.applyMove(from, to)
                            sendMove(from, to)
                            selectedCase = null
                            possibleMoves = emptyList()
                            tour = if (tour == Couleur.BLANC) Couleur.NOIR else Couleur.BLANC
                            board.value = plateau.getBoardMatrix()
                            checkGameState(plateau, myColor) { endMessage = it }
                        } else {
                            selectedCase = null
                            possibleMoves = emptyList()
                        }
                    }
                }
            )
        }

        if (endMessage != null) {
            AlertDialog(
                onDismissRequest = {},
                confirmButton = {
                    Button(onClick = { /* quitter ou retour menu */ }) {
                        Text("Quitter")
                    }
                },
                title = { Text("Fin de partie") },
                text = { Text(endMessage!!) }
            )
        }
    }
}

fun Plateau.getCase(notation: String): Case? {
    val file = notation[0] - 'a'
    val rank = 8 - notation[1].digitToInt()
    return if (file in 0..7 && rank in 0..7) this.cases[rank][file] else null
}

fun Plateau.applyMove(from: String, to: String): Boolean {
    val start = getCase(from) ?: return false
    val end = getCase(to) ?: return false
    val piece = start.piece ?: return false

    end.piece = piece
    start.piece = null
    piece.position = end
    return true
}

fun checkGameState(plateau: Plateau, couleur: Couleur, onEnd: (String) -> Unit) {
    when {
        plateau.estEchecEtMat(couleur) -> onEnd("Échec et mat !")
        plateau.estPat(couleur) -> onEnd("Pat ! Match nul.")
        plateau.roiEnEchec(couleur) -> {} // Échec visuel déjà géré
    }
}
