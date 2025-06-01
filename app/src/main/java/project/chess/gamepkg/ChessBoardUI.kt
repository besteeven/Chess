package project.chess.gamepkg

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import project.chess.entities.Piece
import project.chess.entities.Case
@Composable
fun ChessBoardUI(
    board: Array<Array<Piece?>>,
    selectedCase: Case?,
    possibleMoves: List<Case>,
    onCaseClick: (x: Int, y: Int) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (y in 7 downTo 0) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (x in 0..7) {
                        val isWhite = (x + y) % 2 != 0
                        val isSelected = selectedCase?.x == x && selectedCase.y == y
                        val isPossibleMove = possibleMoves.any { it.x == x && it.y == y }
                        val isCapture = isPossibleMove && board[y][x] != null

                        val backgroundColor = when {
                            isSelected -> Color.Yellow
                            isPossibleMove && isCapture -> Color.Red.copy(alpha = 0.5f)
                            isPossibleMove -> Color(0xFF4CAF50).copy(alpha = 0.3f) // Vert clair
                            isWhite -> Color(0xFFE0C08D)
                            else -> Color(0xFF76482A)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .background(backgroundColor)
                                .clickable { onCaseClick(x, y) },
                            contentAlignment = Alignment.Center
                        ) {
                            val piece = board[y][x]
                            if (piece != null) {
                                androidx.compose.material.Text(
                                    text = piece.getSymbol(),
                                    fontSize = 24.sp
                                )
                            }
                            // Affiche un point pour les coups possibles sur cases vides
                            if (isPossibleMove && !isCapture) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .background(Color(0xFF388E3C).copy(alpha = 0.7f), shape = androidx.compose.foundation.shape.CircleShape)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


//utilisation:
/*setContent {
    MaterialTheme {
        val board = remember { mutableStateOf(plateau.getBoardMatrix()) }
        Column {
            ChessBoardUI(board.value, onCaseClick = { x, y -> })
        }
    }
}*/