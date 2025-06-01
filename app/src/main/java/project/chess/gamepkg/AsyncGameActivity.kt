package project.chess.gamepkg

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import project.chess.entities.*
import kotlinx.coroutines.delay


class AsyncGameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val plateau = remember { Plateau() }
                var selectedCase by remember { mutableStateOf<Case?>(null) }
                var possibleMoves by remember { mutableStateOf<List<Case>>(emptyList()) }
                val board = remember { mutableStateOf(plateau.getBoardMatrix()) }
                var tour by remember { mutableStateOf(Couleur.BLANC) }
                var promotionCase by remember { mutableStateOf<Case?>(null) }
                var promotionColor by remember { mutableStateOf<Couleur?>(null) }
                var endMessage by remember { mutableStateOf<String?>(null) }

                fun checkGameState() {
                    when {
                        plateau.estEchecEtMat(tour) -> endMessage = "Échec et mat ! ${if (tour == Couleur.BLANC) "Noir" else "Blanc"} gagne."
                        plateau.estPat(tour) -> endMessage = "Pat ! Match nul."
                        plateau.roiEnEchec(tour) -> endMessage = "Échec au roi ${if (tour == Couleur.BLANC) "blanc" else "noir"}."
                        else -> endMessage = null
                    }
                }

                Box {
                    Column {
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
                                                // Prise en passant
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
                        // Affiche le joueur courant
                        Text(
                            text = "Tour : ${if (tour == Couleur.BLANC) "Blanc" else "Noir"}",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    // Interface de choix de promotion AU-DESSUS de tout
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
                    // Affiche la boîte de dialogue de fin de partie si besoin
                    if (endMessage != null && (endMessage!!.contains("mat") || endMessage!!.contains("Pat"))) {
                        AlertDialog(
                            onDismissRequest = {},
                            title = { Text("Fin de partie") },
                            text = { Text(endMessage!!) },
                            confirmButton = {
                                Button(onClick = { finish() }) {
                                    Text("Quitter")
                                }
                            }
                        )
                    }
                    // Affiche un message d'échec simple si besoin
                    if (endMessage != null && endMessage!!.contains("Échec au roi")) {
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
        }
    }
}