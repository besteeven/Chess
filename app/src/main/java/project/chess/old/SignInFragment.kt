package project.chess.old

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import project.chess.R

class SignInFragment : Fragment(R.layout.fragment_connexion) {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        val usernameLayout = view.findViewById<TextInputLayout>(R.id.usernameInputLayout)
        val passwordLayout = view.findViewById<TextInputLayout>(R.id.passwordInputLayout)
        val usernameEdit = view.findViewById<TextInputEditText>(R.id.editTextLogin)
        val passwordEdit = view.findViewById<TextInputEditText>(R.id.editTextPassword)
        val loginButton = view.findViewById<Button>(R.id.home_login_button)
        val signUpButton = view.findViewById<TextView>(R.id.signUpLink)

        loginButton.setOnClickListener {
            val usernameInput = usernameEdit.text.toString().trim()
            val passwordInput = passwordEdit.text.toString()

            var isValid = true

            // Vérification des champs
            if (usernameInput.isBlank()) {
                usernameLayout.error = "Nom d'utilisateur ou email requis"
                isValid = false
            } else {
                usernameLayout.error = null
            }

            if (passwordInput.length < 6) {
                passwordLayout.error = "Mot de passe trop court"
                isValid = false
            } else {
                passwordLayout.error = null
            }

            if (!isValid) return@setOnClickListener

            // Si c'est un email valide
            if (Patterns.EMAIL_ADDRESS.matcher(usernameInput).matches()) {
                signInWithEmail(usernameInput, passwordInput)
            } else {
                // Sinon on considère que c'est un username → rechercher email
                db.collection("users").document(usernameInput).get()
                    .addOnSuccessListener { document ->
                        val email = document.getString("email")
                        if (!email.isNullOrEmpty()) {
                            signInWithEmail(email, passwordInput)
                        } else {
                            usernameLayout.error = "Nom d'utilisateur inconnu"
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("SignIn", "Erreur Firestore", e)
                        showToast("Erreur réseau. Réessaie.")
                    }
            }
        }

        signUpButton.setOnClickListener(){
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment, null, NavOptions.
            Builder().setPopUpTo(R.id.signInFragment, true)
                .build())
        }
    }

    private fun signInWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Log.d("SignIn", "Connexion réussie : ${auth.currentUser?.uid}")
                findNavController().navigate(R.id.action_signInFragment_to_homeFragment, null, NavOptions.
                Builder().setPopUpTo(R.id.signInFragment, true)
                    .build())
            }
            .addOnFailureListener { e ->
                Log.w("SignIn", "Échec de connexion", e)
                showToast("Email ou mot de passe incorrect")
            }
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}
