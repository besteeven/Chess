package project.chess.gamepkg

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import project.chess.entities.Case
import project.chess.entities.Couleur
import project.chess.entities.Pion
import project.chess.entities.Plateau
import project.chess.entities.Roi
import project.chess.entities.Tour

@Composable
fun LocalGameScreen(
    modifier: Modifier = Modifier,
    onGameEnd: () -> Unit = {}) {
    val plateau = remember { Plateau() }
    var selectedCase by remember { mutableStateOf<Case?>(null) }
    var possibleMoves by remember { mutableStateOf<List<Case>>(emptyList()) }
    val board = remember { mutableStateOf(plateau.getBoardMatrix()) }
    var tour by remember { mutableStateOf(Couleur.BLANC) }
    var promotionCase by remember { mutableStateOf<Case?>(null) }
    var promotionColor by remember { mutableStateOf<Couleur?>(null) }
    var endMessage by remember { mutableStateOf<String?>(null) }

    var whiteTime by remember { mutableStateOf(15 * 60) }
    var blackTime by remember { mutableStateOf(15 * 60) }
    var isRunning by remember { mutableStateOf(true) }

    LaunchedEffect(tour, isRunning, endMessage, promotionCase) {
        while (isRunning && endMessage == null && promotionCase == null) {
            delay(1000)
            if (tour == Couleur.BLANC) {
                whiteTime--
                if (whiteTime <= 0) {
                    isRunning = false
                    endMessage = "Temps écoulé ! Noir gagne."
                }
            } else {
                blackTime--
                if (blackTime <= 0) {
                    isRunning = false
                    endMessage = "Temps écoulé ! Blanc gagne."
                }
            }
        }
    }

    fun checkGameState() {
        when {
            plateau.estEchecEtMat(tour) -> {
                endMessage = "Échec et mat ! ${if (tour == Couleur.BLANC) "Noir" else "Blanc"} gagne."
                isRunning = false
            }
            plateau.estPat(tour) -> {
                endMessage = "Pat ! Match nul."
                isRunning = false
            }
            plateau.roiEnEchec(tour) -> endMessage = "Échec au roi ${if (tour == Couleur.BLANC) "blanc" else "noir"}."
            else -> endMessage = null
        }
    }

    Box {
        Column {
            Row(
                Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Blanc : ${whiteTime / 60}:${(whiteTime % 60).toString().padStart(2, '0')}", fontSize = 18.sp)
                Text("Noir : ${blackTime / 60}:${(blackTime % 60).toString().padStart(2, '0')}", fontSize = 18.sp)
            }

            ChessBoardUI(
                board = board.value,
                selectedCase = selectedCase,
                possibleMoves = possibleMoves,
                onCaseClick = { x, y ->
                    // Bloque les coups si promotion ou partie terminée
                    if (promotionCase != null || (endMessage?.contains("mat") == true || endMessage?.contains("Pat") == true)) return@ChessBoardUI

                    val case = plateau.cases[y][x]
                    val piece = case?.piece

                    if (selectedCase == null) {
                        // Sélection d'une pièce du bon joueur uniquement
                        if (piece != null && piece.couleur == tour) {
                            selectedCase = case
                            // Si le roi est en échec, ne proposer que les coups qui le protègent
                            possibleMoves = if (plateau.roiEnEchec(tour)) {
                                piece.getMouvementsValides().filter { move ->
                                    val fromCase = selectedCase ?: case
                                    val toCase = move
                                    val originalPiece = toCase.piece
                                    val originalFromPiece = fromCase.piece

                                    toCase.piece = piece
                                    fromCase.piece = null
                                    piece.position = toCase

                                    val stillInCheck = plateau.roiEnEchec(tour)

                                    // Annule la simulation
                                    fromCase.piece = originalFromPiece
                                    toCase.piece = originalPiece
                                    piece.position = fromCase

                                    !stillInCheck
                                }
                            } else {
                                piece.getMouvementsValides()
                            }
                        }
                    } else {
                        // Tentative de déplacement
                        if (case in possibleMoves) {
                            val pieceToMove = selectedCase!!.piece
                            if (pieceToMove != null) {
                                // --- Prise en passant ---
                                if (pieceToMove is Pion) {
                                    if (case != null) {
                                        if (case.x != selectedCase!!.x && case.piece == null) {
                                            val dir = if (pieceToMove.couleur == Couleur.BLANC) -1 else 1
                                            val capturedCase = plateau.cases[case.y + dir][case.x]
                                            capturedCase?.piece = null
                                        }
                                    }
                                    // Double pas
                                    if (case != null) {
                                        if (Math.abs(case.y - selectedCase!!.y) == 2) {
                                            plateau.caseEnPassant = plateau.cases[(case.y + selectedCase!!.y) / 2][case.x]
                                        } else {
                                            plateau.caseEnPassant = null
                                        }
                                    }
                                } else {
                                    plateau.caseEnPassant = null
                                }

                                // --- Roque ---
                                if (case != null) {
                                    if (pieceToMove is Roi && Math.abs(case.x - selectedCase!!.x) == 2) {
                                        val yRoi = case.y
                                        if (case.x == 6) {
                                            // Petit roque
                                            val tour = plateau.cases[yRoi][7]?.piece as? Tour
                                            if (tour != null) {
                                                plateau.cases[yRoi][5]?.piece = tour
                                                plateau.cases[yRoi][7]?.piece = null
                                                tour.position = plateau.cases[yRoi][5]!!
                                                tour.aBouge = true
                                            }
                                        } else if (case.x == 2) {
                                            // Grand roque
                                            val tour = plateau.cases[yRoi][0]?.piece as? Tour
                                            if (tour != null) {
                                                plateau.cases[yRoi][3]?.piece = tour
                                                plateau.cases[yRoi][0]?.piece = null
                                                tour.position = plateau.cases[yRoi][3]!!
                                                tour.aBouge = true
                                            }
                                        }
                                        (pieceToMove as Roi).aBouge = true
                                    }
                                }

                                // --- Promotion ---
                                if (case != null) {
                                    if (pieceToMove is Pion && (case.y == 0 || case.y == 7)) {
                                        selectedCase!!.piece = null
                                        promotionCase = case
                                        promotionColor = pieceToMove.couleur
                                    } else {
                                        case.piece = pieceToMove
                                        selectedCase!!.piece = null
                                        pieceToMove.position = case
                                        // Marque le roi ou la tour comme ayant bougé
                                        if (pieceToMove is Roi) pieceToMove.aBouge = true
                                        if (pieceToMove is Tour) pieceToMove.aBouge = true
                                        tour = if (tour == Couleur.BLANC) Couleur.NOIR else Couleur.BLANC
                                        board.value = plateau.getBoardMatrix()
                                        checkGameState()
                                    }
                                }
                            }
                            selectedCase = null
                            possibleMoves = emptyList()
                        } else {
                            // Désélection si clic ailleurs
                            selectedCase = null
                            possibleMoves = emptyList()
                        }
                    }

                }
            )

            Text(
                text = "Tour : ${if (tour == Couleur.BLANC) "Blanc" else "Noir"}",
                fontSize = 18.sp,
                modifier = Modifier.padding(8.dp)
            )
        }

        // Popups (promotion, échec, fin de partie)
        if (promotionCase != null && promotionColor != null) {
            PromotionChoiceSimple(
                couleur = promotionColor!!,
                promotionCase = promotionCase!!,
                onPieceChosen = { piece ->
                    promotionCase!!.piece = piece
                    piece.position = promotionCase!!
                    promotionCase = null
                    promotionColor = null
                    board.value = plateau.getBoardMatrix()
                    tour = if (tour == Couleur.BLANC) Couleur.NOIR else Couleur.BLANC
                    checkGameState()
                }
            )
        }

        if (endMessage != null && (endMessage!!.contains("mat") || endMessage!!.contains("Pat") || endMessage!!.contains("Temps écoulé"))) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Fin de partie") },
                text = { Text(endMessage!!) },
                confirmButton = {
                    Button(onClick = { onGameEnd() }) {
                        Text("Quitter")
                    }
                }
            )
        }

        if (endMessage?.contains("Échec au roi") == true) {
            AlertDialog(
                onDismissRequest = { endMessage = null },
                title = { Text("Échec !") },
                text = { Text(endMessage!!) },
                confirmButton = {
                    Button(onClick = { endMessage = null }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}
