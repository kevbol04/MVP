package com.example.mvp.ui.screens.login

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.mvp.R

private enum class AuthMode { Login, Register }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    onLogin: (email: String, password: String) -> Unit = { _, _ -> },
    onRegister: (name: String, email: String, password: String) -> Unit = { _, _, _ -> },
) {
    var mode by remember { mutableStateOf(AuthMode.Login) }

    val bgTop = Color(0xFF0B1220)
    val bgMid = Color(0xFF0E2A3B)
    val accent = Color(0xFF00E5FF)
    val accent2 = Color(0xFF7C4DFF)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(bgTop, bgMid, bgTop)))
            .padding(horizontal = 20.dp)
    ) {

        Box(
            modifier = Modifier
                .size(220.dp)
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-40).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(accent.copy(alpha = 0.28f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp, bottom = 22.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(64.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.06f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(id = R.drawable.logo_mvp),
                                contentDescription = "Logo ProFootball",
                                modifier = Modifier.size(60.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "ProFootball",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Tu progreso, tus estadísticas, tu mejor versión.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.72f)
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                color = Color.White.copy(alpha = 0.08f),
                tonalElevation = 2.dp
            ) {
                Box(Modifier.padding(18.dp)) {
                    Crossfade(targetState = mode, label = "") { m ->
                        when (m) {
                            AuthMode.Login -> LoginForm(
                                accent = accent,
                                accent2 = accent2,
                                onLogin = onLogin,
                                onGoRegister = { mode = AuthMode.Register }
                            )

                            AuthMode.Register -> RegisterForm(
                                accent = accent,
                                accent2 = accent2,
                                onRegister = onRegister,
                                onGoLogin = { mode = AuthMode.Login }
                            )
                        }
                    }
                }
            }

            Text(
                text = "Entrenamientos · Partidos · Jugadores · Estadísticas",
                color = Color.White.copy(alpha = 0.55f),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginForm(
    accent: Color,
    accent2: Color,
    onLogin: (String, String) -> Unit,
    onGoRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )

        AuthTextField(
            value = email,
            onValueChange = { email = it },
            label = "Correo electrónico",
            leadingIcon = { Icon(Icons.Default.Email, null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            accent = accent
        )

        AuthTextField(
            value = password,
            onValueChange = { password = it },
            label = "Contraseña",
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            trailing = {
                TextButton(onClick = { showPassword = !showPassword }) {
                    Text(if (showPassword) "Ocultar" else "Ver", color = Color.White.copy(alpha = 0.85f))
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            accent = accent
        )

        GradientButton(
            text = "Iniciar sesión",
            accent = accent,
            accent2 = accent2,
            onClick = { onLogin(email.trim(), password) }
        )

        TextButton(onClick = onGoRegister, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("¿No tienes cuenta? Crear cuenta", color = Color.White.copy(alpha = 0.78f))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegisterForm(
    accent: Color,
    accent2: Color,
    onRegister: (String, String, String) -> Unit,
    onGoLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    val passwordsMatch = password.isNotBlank() && password == confirm

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "Crear cuenta",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )

        AuthTextField(
            value = name,
            onValueChange = { name = it },
            label = "Nombre y apellidos",
            leadingIcon = { Icon(Icons.Default.Person, null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            accent = accent
        )

        AuthTextField(
            value = email,
            onValueChange = { email = it },
            label = "Correo electrónico",
            leadingIcon = { Icon(Icons.Default.Email, null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            accent = accent
        )

        AuthTextField(
            value = password,
            onValueChange = { password = it },
            label = "Contraseña",
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            trailing = {
                TextButton(onClick = { showPassword = !showPassword }) {
                    Text(if (showPassword) "Ocultar" else "Ver", color = Color.White.copy(alpha = 0.85f))
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            accent = accent
        )

        AuthTextField(
            value = confirm,
            onValueChange = { confirm = it },
            label = "Confirmar contraseña",
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            accent = accent,
            supportingText = if (confirm.isNotBlank() && !passwordsMatch) {
                { Text("Las contraseñas no coinciden", color = Color(0xFFFF6B6B)) }
            } else null
        )

        GradientButton(
            text = "Crear cuenta",
            accent = accent,
            accent2 = accent2,
            enabled = name.isNotBlank() && email.isNotBlank() && passwordsMatch,
            onClick = { onRegister(name.trim(), email.trim(), password) }
        )

        TextButton(onClick = onGoLogin, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("¿Ya tienes cuenta? Inicia sesión", color = Color.White.copy(alpha = 0.78f))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    supportingText: @Composable (() -> Unit)? = null,
    accent: Color
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        label = { Text(label) },
        leadingIcon = leadingIcon,
        trailingIcon = trailing,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        supportingText = supportingText,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.White.copy(alpha = 0.18f),
            focusedBorderColor = accent,
            unfocusedLabelColor = Color.White.copy(alpha = 0.65f),
            focusedLabelColor = accent,
            unfocusedTextColor = Color.White,
            focusedTextColor = Color.White,
            unfocusedLeadingIconColor = Color.White.copy(alpha = 0.6f),
            focusedLeadingIconColor = accent,
            unfocusedTrailingIconColor = Color.White.copy(alpha = 0.7f),
            focusedTrailingIconColor = accent,
            cursorColor = accent
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun GradientButton(
    text: String,
    accent: Color,
    accent2: Color,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp)
    ) {
        val alpha = if (enabled) 1f else 0.35f
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(accent.copy(alpha = alpha), accent2.copy(alpha = alpha))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF061018)
            )
        }
    }
}