package project.chess.gamepkg

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
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
    onCaseClick: (x: Int, y: Int) -> Unit
) {
    Column {
        for (y in 7 downTo 0) {
            Row {
                for (x in 0..7) {
                    val isWhite = (x + y) % 2 == 0
                    val backgroundColor = if (isWhite) Color(0xFFE0C08D) else Color(0xFF76482A)

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(backgroundColor)
                            .clickable { onCaseClick(x, y) },
                        contentAlignment = Alignment.Center
                    ) {
                        val piece = board[x][y]
                        if (piece != null) {
                            Text(text = piece.getSymbol(), fontSize = 24.sp)
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