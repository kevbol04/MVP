package com.example.mvp.ui.screens.login

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import com.example.mvp.ui.theme.ButtonTextDark
import com.example.mvp.ui.theme.Danger
import com.example.mvp.ui.theme.GlassBase

private enum class AuthMode { Login, Register }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    onLogin: (email: String, password: String) -> Unit = { _, _ -> },
    onRegister: (name: String, email: String, password: String) -> Unit = { _, _, _ -> },
) {
    var mode by remember { mutableStateOf(AuthMode.Login) }

    val bgTop = MaterialTheme.colorScheme.background
    val bgMid = MaterialTheme.colorScheme.surface
    val accent = MaterialTheme.colorScheme.primary
    val accent2 = MaterialTheme.colorScheme.secondary
    val onBg = MaterialTheme.colorScheme.onBackground

    val logoScale = remember { Animatable(0.86f) }
    val logoAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        logoAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing)
        )
        logoScale.animateTo(
            targetValue = 1.04f,
            animationSpec = tween(durationMillis = 460, easing = FastOutSlowInEasing)
        )
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium)
        )
    }

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
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    Surface(
                        modifier = Modifier
                            .size(68.dp)
                            .scale(logoScale.value)
                            .alpha(logoAlpha.value),
                        shape = CircleShape,
                        color = Color.Transparent,
                        border = BorderStroke(1.dp, onBg.copy(alpha = 0.12f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            GlassBase.copy(alpha = 0.25f),
                                            GlassBase.copy(alpha = 0.12f)
                                        )
                                    )
                                )
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo_mvp2),
                                contentDescription = "Logo ProFootball",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "ProFootball",
                            style = MaterialTheme.typography.titleLarge,
                            color = onBg,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Tu progreso, tus estadísticas, tu mejor versión.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = onBg.copy(alpha = 0.72f)
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                color = GlassBase.copy(alpha = 0.08f),
                tonalElevation = 2.dp
            ) {
                Box(Modifier.padding(18.dp)) {
                    Crossfade(targetState = mode, label = "") { m ->
                        when (m) {
                            AuthMode.Login -> LoginForm(
                                accent = accent,
                                accent2 = accent2,
                                onText = onBg,
                                onLogin = onLogin,
                                onGoRegister = { mode = AuthMode.Register }
                            )

                            AuthMode.Register -> RegisterForm(
                                accent = accent,
                                accent2 = accent2,
                                onText = onBg,
                                onRegister = onRegister,
                                onGoLogin = { mode = AuthMode.Login }
                            )
                        }
                    }
                }
            }

            Text(
                text = "Entrenamientos · Partidos · Jugadores · Estadísticas",
                color = onBg.copy(alpha = 0.55f),
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
    onText: Color,
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
            color = onText,
            fontWeight = FontWeight.SemiBold
        )

        AuthTextField(
            value = email,
            onValueChange = { email = it },
            label = "Correo electrónico",
            leadingIcon = { Icon(Icons.Default.Email, null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            accent = accent,
            onText = onText
        )

        AuthTextField(
            value = password,
            onValueChange = { password = it },
            label = "Contraseña",
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            trailing = {
                TextButton(onClick = { showPassword = !showPassword }) {
                    Text(
                        if (showPassword) "Ocultar" else "Ver",
                        color = onText.copy(alpha = 0.85f)
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            accent = accent,
            onText = onText
        )

        GradientButton(
            text = "Iniciar sesión",
            accent = accent,
            accent2 = accent2,
            onClick = { onLogin(email.trim(), password) }
        )

        TextButton(onClick = onGoRegister, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("¿No tienes cuenta? Crear cuenta", color = onText.copy(alpha = 0.78f))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegisterForm(
    accent: Color,
    accent2: Color,
    onText: Color,
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
            color = onText,
            fontWeight = FontWeight.SemiBold
        )

        AuthTextField(
            value = name,
            onValueChange = { name = it },
            label = "Nombre y apellidos",
            leadingIcon = { Icon(Icons.Default.Person, null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            accent = accent,
            onText = onText
        )

        AuthTextField(
            value = email,
            onValueChange = { email = it },
            label = "Correo electrónico",
            leadingIcon = { Icon(Icons.Default.Email, null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            accent = accent,
            onText = onText
        )

        AuthTextField(
            value = password,
            onValueChange = { password = it },
            label = "Contraseña",
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            trailing = {
                TextButton(onClick = { showPassword = !showPassword }) {
                    Text(
                        if (showPassword) "Ocultar" else "Ver",
                        color = onText.copy(alpha = 0.85f)
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            accent = accent,
            onText = onText
        )

        AuthTextField(
            value = confirm,
            onValueChange = { confirm = it },
            label = "Confirmar contraseña",
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            accent = accent,
            onText = onText,
            supportingText = if (confirm.isNotBlank() && !passwordsMatch) {
                { Text("Las contraseñas no coinciden", color = Danger) }
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
            Text("¿Ya tienes cuenta? Inicia sesión", color = onText.copy(alpha = 0.78f))
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
    accent: Color,
    onText: Color
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
            unfocusedBorderColor = onText.copy(alpha = 0.18f),
            focusedBorderColor = accent,
            unfocusedLabelColor = onText.copy(alpha = 0.65f),
            focusedLabelColor = accent,
            unfocusedTextColor = onText,
            focusedTextColor = onText,
            unfocusedLeadingIconColor = onText.copy(alpha = 0.6f),
            focusedLeadingIconColor = accent,
            unfocusedTrailingIconColor = onText.copy(alpha = 0.7f),
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
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.985f else 1f,
        animationSpec = spring(dampingRatio = 0.62f, stiffness = Spring.StiffnessMedium),
        label = "btnScale"
    )

    val elevation by animateDpAsState(
        targetValue = if (pressed) 2.dp else 10.dp,
        animationSpec = tween(durationMillis = 140, easing = FastOutSlowInEasing),
        label = "btnElevation"
    )

    val alpha = if (enabled) 1f else 0.35f

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .scale(scale),
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 0.dp,
        shadowElevation = elevation,
        color = Color.Transparent
    ) {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(18.dp),
            interactionSource = interactionSource,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                accent.copy(alpha = alpha),
                                accent2.copy(alpha = alpha)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = ButtonTextDark
                )
            }
        }
    }
}