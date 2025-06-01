package project.chess

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import androidx.activity.compose.setContent
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LanguageManager.loadLanguage(this) // 👈 Important
        val context = LanguageManager.applyLocale(this)
        auth = Firebase.auth
        //Force la connexion
        auth.signOut()

        setContent {
            CompositionLocalProvider(LocalContext provides context) {
                ChessApp()
            }
        }
    }

}

