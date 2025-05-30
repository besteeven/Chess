package project.chess.auth.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

interface IAuthViewModel {
    val loginError: StateFlow<String?>
    fun signIn(username: String, password: String, onSuccess: () -> Unit)
    fun signUpUser(email: String, username: String, password: String, onSuccess: () -> Unit)
}

class AuthViewModel : ViewModel(), IAuthViewModel {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _loginError = MutableStateFlow<String?>(null)
    override val loginError = _loginError.asStateFlow()

    override fun signIn(username: String, password: String, onSuccess: () -> Unit) {
        _loginError.value = null

        if (Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            // Direct email login
            signInWithEmail(username, password, onSuccess)
        } else {
            // Lookup email from username in Firestore
            db.collection("users").document(username).get()
                .addOnSuccessListener { doc ->
                    val email = doc.getString("email")
                    if (!email.isNullOrBlank()) {
                        signInWithEmail(email, password, onSuccess)
                    } else {
                        _loginError.value = "Nom d'utilisateur inconnu"
                    }
                }
                .addOnFailureListener {
                    _loginError.value = "Erreur réseau. Réessaie."
                }
        }
    }

    override fun signUpUser(email: String, username: String, password: String, onSuccess: () -> Unit) {
        _loginError.value = null

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _loginError.value = "Email invalide"
            return
        }
        if (username.length < 4) {
            _loginError.value = "Nom d'utilisateur trop court"
            return
        }
        if (password.length < 6) {
            _loginError.value = "Mot de passe trop court"
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val userMap = mapOf("email" to email)
                db.collection("users").document(username).set(userMap)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { _loginError.value = "Erreur lors de l’enregistrement du username" }
            }
            .addOnFailureListener {
                _loginError.value = "Erreur lors de la création du compte"
            }
    }


    private fun signInWithEmail(email: String, password: String, onSuccess: () -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener {
                _loginError.value = "Email ou mot de passe incorrect"
            }
    }
}
