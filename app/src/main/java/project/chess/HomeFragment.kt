package project.chess

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import project.chess.gamepkg.*

class HomeFragment : Fragment(R.layout.homepage) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val localButton = view.findViewById<LinearLayout>(R.id.LocalButton)
        val onlineButton = view.findViewById<LinearLayout>(R.id.OnlineButton)
        val asyncButton = view.findViewById<LinearLayout>(R.id.AsyncButton)
        val friendlyButton = view.findViewById<LinearLayout>(R.id.FriendlyButton)

        localButton.setOnClickListener {
            // Lancer une partie locale directement
            val intent = Intent(requireContext(), LocalGameActivity::class.java)
            startActivity(intent)
        }

        onlineButton.setOnClickListener {
            // Dialogue de recherche d'adversaire en ligne
            val dialog = AlertDialog.Builder(requireContext())
                .setTitle("Recherche d'adversaire")
                .setMessage("Recherche d'un adversaire en ligne...")
                .setCancelable(false)
                .create()
            dialog.show()

            Handler(Looper.getMainLooper()).postDelayed({
                dialog.dismiss()
                val intent = Intent(requireContext(), OnlineGameActivity::class.java)
                startActivity(intent)
            }, 2000)
        }

        asyncButton.setOnClickListener {
            // Dialogue de recherche d'adversaire asynchrone
            val dialog = AlertDialog.Builder(requireContext())
                .setTitle("Recherche d'adversaire")
                .setMessage("Recherche d'un adversaire pour une partie asynchrone...")
                .setCancelable(false)
                .create()
            dialog.show()

            Handler(Looper.getMainLooper()).postDelayed({
                dialog.dismiss()
                val intent = Intent(requireContext(), AsyncGameActivity::class.java)
                startActivity(intent)
            }, 2000)
        }

        friendlyButton.setOnClickListener {
            // Dialogue d'attente de validation d'un ami
            val dialog = AlertDialog.Builder(requireContext())
                .setTitle("Invitation envoyée")
                .setMessage("En attente de la validation d'un ami...")
                .setCancelable(false)
                .create()
            dialog.show()

            Handler(Looper.getMainLooper()).postDelayed({
                dialog.dismiss()
                val intent = Intent(requireContext(), FriendlyGameActivity::class.java)
                startActivity(intent)
            }, 2000)
        }
    }
}