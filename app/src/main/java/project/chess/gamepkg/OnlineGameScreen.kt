package project.chess.online

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.delay
import project.chess.entities.Case
import project.chess.entities.Couleur
import project.chess.entities.Plateau
import project.chess.gamepkg.ChessBoardUI

@Composable
fun OnlineGameScreen(gameId: String, isWhite: Boolean, onGameEnd: () -> Unit = {}) {
    val db = Firebase.firestore
    val auth = Firebase.auth
    val context = LocalContext.current

    val plateau = remember { Plateau() }
    val board = remember { mutableStateOf(plateau.getBoardMatrix()) }
    var selectedCase by remember { mutableStateOf<Case?>(null) }
    var possibleMoves by remember { mutableStateOf<List<Case>>(emptyList()) }
    var currentTurnColor by remember { mutableStateOf(Couleur.BLANC) }
    var endMessage by remember { mutableStateOf<String?>(null) }
    val myColor = if (isWhite) Couleur.BLANC else Couleur.NOIR
    var moveListener: ListenerRegistration? by remember { mutableStateOf(null) }
    var showMenu by remember { mutableStateOf(false) }

    val userEmail = auth.currentUser?.email
    var username by remember { mutableStateOf("unknown") }

    // Récupère le username à partir de l'email
    LaunchedEffect(userEmail) {
        if (userEmail != null) {
            db.collection("users")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnSuccessListener { result ->
                    result.firstOrNull()?.let {
                        username = it.id
                    }
                }
        }
    }

    // Reçoit les coups de l’adversaire
    LaunchedEffect(gameId) {
        moveListener = db.collection("games")
            .document(gameId)
            .collection("moves")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, _ ->
                val lastMove = snapshot?.documents
                    ?.mapNotNull {
                        val from = it.getString("from")
                        val to = it.getString("to")
                        val player = it.getString("player")
                        if (from != null && to != null && player != null) Triple(from, to, player) else null
                    }
                    ?.lastOrNull() ?: return@addSnapshotListener

                val (from, to, player) = lastMove
                if (player != username) {
                    plateau.applyMove(from, to)
                    currentTurnColor = myColor
                    board.value = plateau.getBoardMatrix()
                    checkGameState(plateau, myColor) { endMessage = it }
                }
            }
    }

    // Écoute le champ "result" pour la capitulation/adversaire
    LaunchedEffect(gameId) {
        db.collection("games").document(gameId)
            .addSnapshotListener { snapshot, _ ->
                val result = snapshot?.getString("result")
                if (result == "white_win") {
                    endMessage = if (isWhite) "Vous avez gagné ! L'adversaire a abandonné." else "Vous avez abandonné la partie."
                } else if (result == "black_win") {
                    endMessage = if (!isWhite) "Vous avez gagné ! L'adversaire a abandonné." else "Vous avez abandonné la partie."
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
        currentTurnColor = if (myColor == Couleur.BLANC) Couleur.NOIR else Couleur.BLANC
    }

    // Capitulation
    fun resign() {
        // Notifie Firestore de l'abandon
        val result = if (isWhite) "black_win" else "white_win"
        db.collection("games").document(gameId).update("result", result)
    }

    // Ferme automatiquement après 3 secondes si fin de partie
    LaunchedEffect(endMessage) {
        if (endMessage != null) {
            delay(3000)
            onGameEnd()
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column {
            // Menu capitulation
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(
                        text = { Text("Capituler") },
                        onClick = {
                            showMenu = false
                            resign()
                        }
                    )
                }
            }
            Text(
                text = "Tour : ${if (currentTurnColor == Couleur.BLANC) "Blanc" else "Noir"}",
                fontSize = 20.sp,
                modifier = Modifier.padding(8.dp)
            )
            ChessBoardUI(
                board = board.value,
                selectedCase = selectedCase,
                possibleMoves = possibleMoves,
                onCaseClick = { x, y ->
                    if (currentTurnColor != myColor || endMessage != null) return@ChessBoardUI
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

        // Dialog de fin de partie (capitulation ou victoire par abandon)
        if (endMessage != null) {
            AlertDialog(
                onDismissRequest = {},
                confirmButton = {
                    Button(onClick = { onGameEnd() }) {
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
        plateau.roiEnEchec(couleur) -> {} // Message non bloquant
    }
}