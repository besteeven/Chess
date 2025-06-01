package project.chess.core.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.google.firebase.annotations.concurrent.Background

public val LightColors = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    secondary = Secondary,
    onSecondary = OnSecondary,
    background = Background,
    onBackground = OnBackground,
    onTertiary = Bluelink,
    surface = Color.White,
    onSurface = Color.Black
)

public val DarkColors = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    secondary = Secondary,
    onSecondary = OnSecondary,
    background = Background,
    onTertiary = Bluelink,
    onBackground = OnBackground,
    surface = Color.White,
    onSurface = Color.Black
)