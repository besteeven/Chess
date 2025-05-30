package project.chess.connexion

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

class SignUpFragment: Fragment(R.layout.fragment_inscription) {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emailLayout = view.findViewById<TextInputLayout>(R.id.emailInputLayout)
        val usernameLayout = view.findViewById<TextInputLayout>(R.id.usernameInputLayout)
        val passwordLayout = view.findViewById<TextInputLayout>(R.id.passwordInputLayout)
        val emailEdit = view.findViewById<TextInputEditText>(R.id.editTextEmail)
        val usernameEdit = view.findViewById<TextInputEditText>(R.id.editTextLogin)
        val passwordEdit = view.findViewById<TextInputEditText>(R.id.editTextPassword)
        val loginButton = view.findViewById<TextView>(R.id.loginLink)
        val signUpButton = view.findViewById<Button>(R.id.home_signup_button)

        auth = Firebase.auth
        signUpButton.setOnClickListener(){

            val emailInput = emailEdit.text.toString().trim()
            val usernameInput = usernameEdit.text.toString().trim()
            val passwordInput = passwordEdit.text.toString()

            var isValid = true

            if (Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
                emailLayout.error = null
            }else{
                emailLayout.error = "Invalid email"
                isValid = false
            }

            if (passwordInput.length >= 6){
                passwordLayout.error = null
            }else{
                passwordLayout.error = "Password must be at least 6 characters"
                isValid = false
            }

            if (usernameInput.length >= 4){
                usernameLayout.error = null
            }else{
                usernameLayout.error = "Username must be ay least 4 characters"
                isValid = false
            }

            if (isValid){
                signUpUser(emailInput,usernameInput,passwordInput)
            }

        }

        loginButton.setOnClickListener(){
            findNavController().navigate(R.id.action_signUpFragment_to_signInFragment, null, NavOptions.
            Builder().setPopUpTo(R.id.signUpFragment, true)
                .build())
        }
    }

    fun signUpUser(email: String, username:String, password: String){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener() { task ->
            if (task.isSuccessful) {
                Log.d("TAG", "createUserWithEmail:success")
                val user = auth.currentUser
                linkUsernameToEmail(email,username)
                findNavController().navigate(R.id.action_signUpFragment_to_homeFragment, null, NavOptions
                    .Builder().setPopUpTo(R.id.connexionFragment,true)
                    .build())
            } else {
                Log.w("TAG", "createUserWithEmail:failure", task.exception)
            }
        }
    }

    fun linkUsernameToEmail(email: String, username:String){
        val user = hashMapOf(
            "email" to email
        )

        db.collection("users").document(username)
            .set(user)
            .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }

    }
}