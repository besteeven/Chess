package project.chess

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



class LaunchActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        //Force la connexion
        auth.signOut()

        setContent {
            ChessApp()
        }
    }

}
