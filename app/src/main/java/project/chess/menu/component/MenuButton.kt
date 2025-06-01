package project.chess.menu.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

import androidx.compose.ui.graphics.painter.Painter

@Composable
fun MenuButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 56.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = Color.White,
    textSize: TextUnit = MaterialTheme.typography.bodyLarge.fontSize,
    iconVector: ImageVector? = null,      // ← icône vectorielle (optionnelle)
    iconPainter: Painter? = null,         // ← icône PNG (optionnelle)
    pngSize: Dp = 24.dp,
    imageRes: Int? = null,                 // ← image à droite (optionnelle)
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(4.dp, Color.Black),
        modifier = modifier
            .height(height)
            .fillMaxWidth()
            .dropShadow(
                shape = RoundedCornerShape(16.dp),
                blur = 10.dp,
                offsetY = 8.dp,
                color = Color.Black.copy(alpha = 0.3f)
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Priorité au PNG si fourni
                when {
                    iconPainter != null -> {
                        Image(
                            painter = iconPainter,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(pngSize)
                        )
                    }
                    iconVector != null -> {
                        Icon(
                            imageVector = iconVector,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }

                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = textSize
                    )
                )
            }

            if (imageRes != null) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}



