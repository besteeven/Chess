package project.chess.connexion

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import project.chess.R

class ConnexionFragment:Fragment(R.layout.fragment_home) {



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.home_signup_button).setOnClickListener {
            Log.d("TEST", "Signup Clicked")
            findNavController().navigate(R.id.action_connexionFragment_to_signUpFragment)
        }

        view.findViewById<Button>(R.id.home_login_button).setOnClickListener {
            Log.d("TEST", "Login clicked")
            findNavController().navigate(R.id.action_connexionFragment_to_signInFragment)
        }
    }
}