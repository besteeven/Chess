package project.chess.gamepkg.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PlayerInfoCard(name: String, elo: String, isTurn: Boolean, color:String) {
    val borderColor = if (isTurn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline

    Surface(
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .border(width = 3.dp, color = borderColor, shape = MaterialTheme.shapes.medium)
            .padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = name, style = MaterialTheme.typography.titleMedium)
            Text(text = "ELO : $elo", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Color : $color", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
