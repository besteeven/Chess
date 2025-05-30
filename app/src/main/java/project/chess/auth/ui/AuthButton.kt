package project.chess.auth.ui

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import project.chess.menu.component.dropShadow

@Composable
fun AuthButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    ElevatedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .width(250.dp)
            .heightIn(min = 64.dp)
            .dropShadow(
                shape = RoundedCornerShape(16.dp),
                blur = 10.dp,
                offsetY = 8.dp,
                color = Color.Black.copy(alpha = 0.3f)
            ),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,         // Couleur de fond
            contentColor = Color.Black,                 // Couleur du texte
            disabledContainerColor = Color.Gray,        // Fond désactivé
            disabledContentColor = Color.LightGray      // Texte désactivé
        )
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize =25.sp
        )
    }
}
