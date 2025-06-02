package project.chess.auth.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import project.chess.R
import project.chess.auth.viewmodel.AuthViewModel
import project.chess.auth.viewmodel.IAuthViewModel
import project.chess.core.theme.Theme
import project.chess.menu.component.CustomTextField

@Composable
fun SignupScreen(
    onSignupSuccess: () -> Unit,
    onLoginClick: () -> Unit,
    viewModel: IAuthViewModel = viewModel<AuthViewModel>()
) {
    val background = painterResource(id = R.drawable.logo)
    val error by viewModel.loginError.collectAsState()

    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf<String?>(null) }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = background,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(300.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    CustomTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = null
                        },
                        label = stringResource(R.string.email),
                        isError = emailError != null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    emailError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, style = typography.bodySmall)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

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
                    usernameError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, style = typography.bodySmall)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

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
                    passwordError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, style = typography.bodySmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            AuthButton(
                text = stringResource(R.string.signup),
                onClick = {
                    var valid = true
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        emailError = "Email invalide"; valid = false
                    }
                    if (username.length < 4) {
                        usernameError = "Nom d'utilisateur trop court"; valid = false
                    }
                    if (password.length < 6) {
                        passwordError = "Mot de passe trop court"; valid = false
                    }

                    if (valid) {
                        viewModel.signUpUser(email, username, password, onSignupSuccess)
                    }
                }
            )

            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.login),
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onTertiary,
                style = typography.bodyLarge.copy(textDecoration = TextDecoration.Underline),
                modifier = Modifier.clickable(onClick = onLoginClick)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}



@Preview(showBackground = true)
@Composable
fun SignupScreenPreview() {
    val fakeViewModel = FakeAuthViewModel()
    Theme {
        SignupScreen(
            onSignupSuccess = {},
            onLoginClick = {},
            viewModel = fakeViewModel
        )
    }
}

