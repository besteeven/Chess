package project.chess.auth.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import project.chess.R
import project.chess.auth.viewmodel.AuthViewModel
import project.chess.auth.viewmodel.IAuthViewModel
import project.chess.core.theme.Theme
import project.chess.menu.component.CustomTextField
import project.chess.menu.component.dropShadow



@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSignupClick: () -> Unit,
    viewModel: IAuthViewModel = viewModel<AuthViewModel>()
) {
    val background = painterResource(id = R.drawable.logo)
    val loginError by viewModel.loginError.collectAsState()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Fond personnalisé
        Image(
            painter = background,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 400.dp),
            horizontalAlignment = Alignment.CenterHorizontally,

        ) {
            // Container noir transparent
            Card(
                modifier = Modifier
                    .heightIn(max = 250.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier
                    .padding(24.dp)
                    .padding(horizontal = 15.dp)
                    .fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    CustomTextField(
                        value = username,
                        onValueChange = {
                            username = it
                            usernameError = null
                        },
                        label = stringResource(R.string.username),
                        isError = usernameError != null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (usernameError != null) {
                        Text(
                            text = usernameError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = typography.bodySmall,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    CustomTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = null
                        },
                        label = stringResource(R.string.password),
                        isError = passwordError != null,
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = null)
                            }
                        }
                    )

                    if (passwordError != null) {
                        Text(
                            text = passwordError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = typography.bodySmall,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            AuthButton(
                text = stringResource(R.string.login),
                onClick = {
                    var valid = true
                    if (username.isBlank()) {
                        usernameError = "USERNAMEERROR"
                        valid = false
                    }
                    if (password.length < 6) {
                        passwordError = "PASSWORD ERROR"//stringResource(R.string.error_password_short)
                        valid = false
                    }

                    if (valid) {
                        viewModel.signIn(username, password, onLoginSuccess)
                    }
                }
            )

            if (loginError != null) {
                Text(
                    text = loginError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.signup),
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onTertiary,
                style = typography.bodyLarge.copy(
                    textDecoration = TextDecoration.Underline
                ),
                modifier = Modifier.clickable(onClick = onSignupClick)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val fakeViewModel = FakeAuthViewModel()

    Theme {
        LoginScreen(
            onLoginSuccess = {},
            onSignupClick = {},
            viewModel = fakeViewModel
        )
    }
}

class FakeAuthViewModel : IAuthViewModel {
    override val loginError = MutableStateFlow<String?>(null)
    override fun signIn(username: String, password: String, onSuccess: () -> Unit) { }
    override fun signUpUser(email: String, username: String, password: String, onSuccess: () -> Unit) { }
}