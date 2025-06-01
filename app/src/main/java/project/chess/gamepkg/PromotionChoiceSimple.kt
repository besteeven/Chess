package project.chess.gamepkg
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import project.chess.entities.*


@Composable
fun PromotionChoiceSimple(
    couleur: Couleur,
    promotionCase: Case,
    onPieceChosen: (Piece) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xAA000000)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .background(Color.White)
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(
                { Reine(couleur, promotionCase) },
                { Tour(couleur, promotionCase) },
                { Fou(couleur, promotionCase) },
                { Cavalier(couleur, promotionCase) }
            ).forEach { pieceFactory ->
                val piece = pieceFactory()
                Text(
                    text = piece.getSymbol(),
                    fontSize = 36.sp,
                    modifier = Modifier
                        .clickable { onPieceChosen(piece) }
                        .padding(8.dp)
                )
            }
        }
    }
}