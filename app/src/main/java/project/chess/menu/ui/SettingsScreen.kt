package project.chess.menu.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import project.chess.R
import project.chess.core.navigation.Routes
import project.chess.core.theme.Theme
import project.chess.menu.component.MenuButton
import project.chess.menu.data.LanguageManager

@Preview
@Composable
fun DrawSettings(modifier: Modifier = Modifier) {
    Theme {
        SettingsScreen(
        )
    }
}

@Composable
fun SettingsScreen(
    navController: NavController = rememberNavController()
) {
    val context = LocalContext.current
    val language by remember { derivedStateOf { LanguageManager.currentLanguage } }

    var showPasswordDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.settings),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        MenuButton(
            text = stringResource(R.string.change_password),
            onClick = { showPasswordDialog = true },
            iconVector = Icons.Default.Lock,
            height = 96.dp,
            backgroundColor = MaterialTheme.colorScheme.secondary
        )

        MenuButton(
            text = stringResource(R.string.language),
            onClick = {
                LanguageManager.cycleLanguage(context)
                restartApp(context)
            },
            iconVector = Icons.Default.Language,
            imageRes = language.flagRes,
            backgroundColor = MaterialTheme.colorScheme.secondary
        )

        MenuButton(
            text = stringResource(R.string.logout),
            onClick = { Logout(navController) },
            iconVector = Icons.AutoMirrored.Filled.ExitToApp,
            backgroundColor = MaterialTheme.colorScheme.secondary
        )
    }

    if (showPasswordDialog) {
        ChangePasswordDialog(onDismiss = { showPasswordDialog = false })
    }
}

@Composable
fun ChangePasswordDialog(onDismiss: () -> Unit) {
    val auth = FirebaseAuth.getInstance()
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.change_password)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PasswordField(value = oldPassword, label = stringResource(R.string.oldpass)) {
                    oldPassword = it
                }
                PasswordField(value = newPassword, label = stringResource(R.string.newpass)) {
                    newPassword = it
                }

                message?.let {
                    val color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    Text(it, color = color)
                }
            }
        },

        confirmButton = {
            TextButton(onClick = {

                changePassword(
                    oldPassword = oldPassword,
                    newPassword = newPassword,
                    auth = auth,
                    onSuccess = {
                        message = context.getString(R.string.updatedpass)
                        isError = false
                    },
                    onError = {
                        message = it
                        isError = true
                    }
                )
            }) {
                Text(text = stringResource(R.string.accept))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
fun PasswordField(value: String, label: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true
    )
}

fun changePassword(
    oldPassword: String,
    newPassword: String,
    auth: FirebaseAuth,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val user = auth.currentUser
    val email = user?.email

    if (user == null || email == null) {
        onError("Utilisateur non connecté")
        return
    }

    val credential = EmailAuthProvider.getCredential(email, oldPassword)

    user.reauthenticate(credential)
        .addOnSuccessListener {
            user.updatePassword(newPassword)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onError("Erreur lors de la mise à jour") }
        }
        .addOnFailureListener {
            onError("Mot de passe actuel incorrect")
        }
}



fun Logout(navController: NavController) {
    FirebaseAuth.getInstance().signOut()
    navController.navigate(Routes.LOGIN) {
        popUpTo(Routes.MENU) { inclusive = true } // Supprime la pile
        launchSingleTop = true
    }
}

fun restartApp(context: Context) {
    val activity = context as? Activity ?: return

    // Fermer le clavier
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)

    activity.finish()
    activity.startActivity(Intent(activity, activity::class.java))
}

