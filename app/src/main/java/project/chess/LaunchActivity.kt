package project.chess

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import project.chess.menu.data.Language
import project.chess.menu.data.LanguageManager
import project.chess.menu.data.SupportedLanguages


class LaunchActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LanguageManager.loadLanguage(this) // 👈 Important
        val context = LanguageManager.applyLocale(this)
        auth = Firebase.auth
        //Force la connexion
        auth.signOut()
        val channel = NotificationChannel(
            "chess_channel",
            "Match Notifications",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
        val gameId = intent.getStringExtra("gameId")
        val challengeFrom = intent.getStringExtra("challenge_from")


        setContent {
            CompositionLocalProvider(LocalContext provides context) {
                ChessApp(
                    initialGameId = gameId,
                    isChallenge = gameId != null
                )
            }
        }
    }

}

