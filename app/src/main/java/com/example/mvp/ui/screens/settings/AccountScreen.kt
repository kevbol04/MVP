package com.example.mvp.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.mvp.ui.theme.ButtonTextDark
import com.example.mvp.ui.theme.GlassBase

private const val MIN_PASS_LEN = 4

@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
    name: String = "Kev",
    email: String = "kev@email.com",
    onBack: () -> Unit = {},
    onSave: (String, String) -> Unit = { _, _ -> },
    onChangePassword: (current: String, new: String) -> Unit = { _, _ -> },

    onDeleteAccount: () -> Unit = {},

    passwordLoading: Boolean = false,
    passwordError: String? = null,
    passwordChanged: Boolean = false,
    onPasswordChangedConsumed: () -> Unit = {},
    onPasswordErrorConsumed: () -> Unit = {},

    deleteLoading: Boolean = false,
    deleteError: String? = null,
    accountDeleted: Boolean = false,
    onDeleteErrorConsumed: () -> Unit = {}
) {
    val bgTop = MaterialTheme.colorScheme.background
    val bgMid = MaterialTheme.colorScheme.surface
    val accent = MaterialTheme.colorScheme.primary
    val accent2 = MaterialTheme.colorScheme.secondary
    val onBg = MaterialTheme.colorScheme.onBackground

    var savedName by remember(name) { mutableStateOf(name) }
    var savedEmail by remember(email) { mutableStateOf(email) }

    var draftName by remember(name) { mutableStateOf(name) }
    var draftEmail by remember(email) { mutableStateOf(email) }

    var showChangePass by remember { mutableStateOf(false) }
    var showDelete by remember { mutableStateOf(false) }
    var showConfirmSave by remember { mutableStateOf(false) }

    val hasChanges = draftName.trim() != savedName.trim() || draftEmail.trim() != savedEmail.trim()

    LaunchedEffect(passwordChanged) {
        if (passwordChanged) {
            showChangePass = false
            onPasswordChangedConsumed()
        }
    }

    LaunchedEffect(accountDeleted) {
        if (accountDeleted) {
            showDelete = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(bgTop, bgMid, bgTop)))
            .padding(horizontal = 20.dp)
    ) {
        Box(
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.TopStart)
                .offset(x = (-70).dp, y = (-60).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(accent.copy(alpha = 0.18f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(220.dp)
                .align(Alignment.TopEnd)
                .offset(x = 70.dp, y = 40.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(accent2.copy(alpha = 0.14f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 18.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AccountTopBar(
                title = "Perfil y cuenta",
                accent = accent,
                accent2 = accent2,
                onText = onBg,
                onBack = onBack
            )

            ProfileHeroSimple(
                name = savedName,
                email = savedEmail,
                accent = accent,
                accent2 = accent2,
                onText = onBg
            )

            ActionTileWide(
                title = "Modificar contraseña",
                subtitle = "Actualiza tu contraseña de acceso",
                icon = Icons.Default.LockReset,
                accent = accent,
                accent2 = accent2,
                onText = onBg,
                onClick = {
                    onPasswordErrorConsumed()
                    showChangePass = true
                }
            )

            SectionHeader(title = "Datos de la cuenta", onText = onBg)

            GlassPanel {
                FieldRow(
                    label = "Nombre",
                    icon = Icons.Default.Person,
                    accent = accent,
                    accent2 = accent2,
                    onText = onBg
                ) {
                    OutlinedTextField(
                        value = draftName,
                        onValueChange = { draftName = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )
                }

                DividerGlass()

                FieldRow(
                    label = "Email",
                    icon = Icons.Default.Email,
                    accent = accent,
                    accent2 = accent2,
                    onText = onBg
                ) {
                    OutlinedTextField(
                        value = draftEmail,
                        onValueChange = { draftEmail = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )
                }

                DividerGlass()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Surface(
                        modifier = Modifier.clickable(enabled = hasChanges) { showConfirmSave = true },
                        shape = RoundedCornerShape(16.dp),
                        color = if (hasChanges) accent.copy(alpha = 0.18f) else GlassBase.copy(alpha = 0.06f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = if (hasChanges) accent else onBg.copy(alpha = 0.35f)
                            )
                            Text(
                                text = "Guardar cambios",
                                color = if (hasChanges) accent else onBg.copy(alpha = 0.35f),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            DangerBanner(
                title = "Eliminar cuenta",
                subtitle = "Acción irreversible. Borrará tu información.",
                onText = onBg,
                onClick = {
                    onDeleteErrorConsumed()
                    showDelete = true
                }
            )
        }

        if (showConfirmSave) {
            ConfirmProfileChangesDialog(
                accent = accent,
                accent2 = accent2,
                onText = onBg,
                oldName = savedName,
                oldEmail = savedEmail,
                newName = draftName.trim(),
                newEmail = draftEmail.trim(),
                onDismiss = { showConfirmSave = false },
                onConfirm = {
                    onSave(draftName.trim(), draftEmail.trim())
                    savedName = draftName.trim()
                    savedEmail = draftEmail.trim()
                    showConfirmSave = false
                }
            )
        }

        if (showChangePass) {
            ChangePasswordDialog(
                accent = accent,
                accent2 = accent2,
                onText = onBg,
                loading = passwordLoading,
                errorText = passwordError,
                onClearError = onPasswordErrorConsumed,
                onDismiss = {
                    if (!passwordLoading) {
                        onPasswordErrorConsumed()
                        showChangePass = false
                    }
                },
                onConfirm = { current, new ->
                    onChangePassword(current, new)
                }
            )
        }

        if (showDelete) {
            DeleteAccountDialog(
                accent = accent,
                accent2 = accent2,
                onText = onBg,
                loading = deleteLoading,
                errorText = deleteError,
                onClearError = onDeleteErrorConsumed,
                onDismiss = {
                    if (!deleteLoading) {
                        onDeleteErrorConsumed()
                        showDelete = false
                    }
                },
                onConfirmDelete = {
                    onDeleteErrorConsumed()
                    onDeleteAccount()
                }
            )
        }
    }
}

@Composable
private fun ChangePasswordDialog(
    accent: Color,
    accent2: Color,
    onText: Color,
    loading: Boolean,
    errorText: String?,
    onClearError: () -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (current: String, new: String) -> Unit
) {
    var current by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var repeat by remember { mutableStateOf("") }

    var showCurrent by remember { mutableStateOf(false) }
    var showNew by remember { mutableStateOf(false) }
    var showRepeat by remember { mutableStateOf(false) }

    val currentTrim = current.trim()
    val newTrim = newPass.trim()
    val repeatTrim = repeat.trim()

    val isValid =
        currentTrim.isNotBlank() &&
                newTrim.isNotBlank() &&
                repeatTrim.isNotBlank() &&
                newTrim.length >= MIN_PASS_LEN &&
                newTrim == repeatTrim &&
                newTrim != currentTrim

    val helperText = when {
        currentTrim.isBlank() || newTrim.isBlank() || repeatTrim.isBlank() ->
            "Completa todos los campos."
        newTrim.length < MIN_PASS_LEN ->
            "La nueva contraseña debe tener al menos $MIN_PASS_LEN caracteres."
        newTrim != repeatTrim ->
            "La nueva contraseña no coincide."
        newTrim == currentTrim ->
            "La nueva contraseña no puede ser igual a la actual."
        else -> ""
    }

    Dialog(
        onDismissRequest = { if (!loading) onDismiss() },
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.92f)),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .padding(horizontal = 18.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = GlassBase.copy(alpha = 0.14f),
                tonalElevation = 0.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                accent.copy(alpha = 0.38f),
                                                accent2.copy(alpha = 0.22f)
                                            )
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.LockReset, contentDescription = null, tint = ButtonTextDark)
                            }
                            Column {
                                Text(
                                    text = "Cambiar contraseña",
                                    color = onText,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Refuerza la seguridad de tu cuenta",
                                    color = onText.copy(alpha = 0.70f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .size(40.dp)
                                .clickable(enabled = !loading) { onDismiss() },
                            shape = CircleShape,
                            color = GlassBase.copy(alpha = 0.10f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = onText.copy(alpha = 0.70f))
                            }
                        }
                    }

                    Divider(color = Color.White.copy(alpha = 0.06f))

                    PasswordField(
                        label = "Contraseña actual",
                        value = current,
                        onValueChange = {
                            current = it
                            if (errorText != null) onClearError()
                        },
                        show = showCurrent,
                        onToggleShow = { showCurrent = !showCurrent }
                    )

                    PasswordField(
                        label = "Nueva contraseña",
                        value = newPass,
                        onValueChange = {
                            newPass = it
                            if (errorText != null) onClearError()
                        },
                        show = showNew,
                        onToggleShow = { showNew = !showNew }
                    )

                    PasswordField(
                        label = "Repetir nueva contraseña",
                        value = repeat,
                        onValueChange = {
                            repeat = it
                            if (errorText != null) onClearError()
                        },
                        show = showRepeat,
                        onToggleShow = { showRepeat = !showRepeat }
                    )

                    if (helperText.isNotBlank()) {
                        Text(
                            text = helperText,
                            color = onText.copy(alpha = 0.70f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    if (!errorText.isNullOrBlank()) {
                        Text(
                            text = errorText,
                            color = Color.Red.copy(alpha = 0.85f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable(enabled = !loading) { onDismiss() },
                            shape = RoundedCornerShape(16.dp),
                            color = GlassBase.copy(alpha = 0.10f)
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Cancelar",
                                    color = onText.copy(alpha = 0.80f),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable(enabled = isValid && !loading) {
                                    onConfirm(currentTrim, newTrim)
                                },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isValid && !loading) accent.copy(alpha = 0.20f) else GlassBase.copy(alpha = 0.08f)
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (loading) {
                                    CircularProgressIndicator(
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(18.dp)
                                    )
                                } else {
                                    Text(
                                        text = "Guardar",
                                        color = if (isValid) accent else onText.copy(alpha = 0.45f),
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeleteAccountDialog(
    accent: Color,
    accent2: Color,
    onText: Color,
    loading: Boolean,
    errorText: String?,
    onClearError: () -> Unit,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    var confirmText by remember { mutableStateOf("") }
    val token = "ELIMINAR"
    val enabled = confirmText.trim() == token

    Dialog(
        onDismissRequest = { if (!loading) onDismiss() },
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.92f)),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .padding(horizontal = 18.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = GlassBase.copy(alpha = 0.14f),
                tonalElevation = 0.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                Color.Red.copy(alpha = 0.28f),
                                                accent2.copy(alpha = 0.14f)
                                            )
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = ButtonTextDark)
                            }
                            Column {
                                Text(
                                    text = "Eliminar cuenta",
                                    color = onText,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Esta acción no se puede deshacer",
                                    color = onText.copy(alpha = 0.70f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .size(40.dp)
                                .clickable(enabled = !loading) { onDismiss() },
                            shape = CircleShape,
                            color = GlassBase.copy(alpha = 0.10f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = onText.copy(alpha = 0.70f))
                            }
                        }
                    }

                    Divider(color = Color.White.copy(alpha = 0.06f))

                    Text(
                        text = "Para confirmar, escribe \"$token\".",
                        color = onText.copy(alpha = 0.80f),
                        style = MaterialTheme.typography.bodySmall
                    )

                    OutlinedTextField(
                        value = confirmText,
                        onValueChange = {
                            confirmText = it
                            if (errorText != null) onClearError()
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        placeholder = { Text(token) },
                        enabled = !loading
                    )

                    if (!errorText.isNullOrBlank()) {
                        Text(
                            text = errorText,
                            color = Color.Red.copy(alpha = 0.85f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable(enabled = !loading) { onDismiss() },
                            shape = RoundedCornerShape(16.dp),
                            color = GlassBase.copy(alpha = 0.10f)
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Cancelar",
                                    color = onText.copy(alpha = 0.80f),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable(enabled = enabled && !loading) { onConfirmDelete() },
                            shape = RoundedCornerShape(16.dp),
                            color = if (enabled && !loading) Color.Red.copy(alpha = 0.16f) else GlassBase.copy(alpha = 0.08f)
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (loading) {
                                    CircularProgressIndicator(
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(18.dp)
                                    )
                                } else {
                                    Text(
                                        text = "Eliminar",
                                        color = if (enabled) Color.Red.copy(alpha = 0.90f) else onText.copy(alpha = 0.45f),
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConfirmProfileChangesDialog(
    accent: Color,
    accent2: Color,
    onText: Color,
    oldName: String,
    oldEmail: String,
    newName: String,
    newEmail: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.92f)),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .padding(horizontal = 18.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = GlassBase.copy(alpha = 0.14f),
                tonalElevation = 0.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                accent.copy(alpha = 0.38f),
                                                accent2.copy(alpha = 0.22f)
                                            )
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = ButtonTextDark)
                            }
                            Column {
                                Text(
                                    text = "Confirmar cambios",
                                    color = onText,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "¿Quieres actualizar tu perfil?",
                                    color = onText.copy(alpha = 0.70f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { onDismiss() },
                            shape = CircleShape,
                            color = GlassBase.copy(alpha = 0.10f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = onText.copy(alpha = 0.70f))
                            }
                        }
                    }

                    Divider(color = Color.White.copy(alpha = 0.06f))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        ChangeRow(label = "Nombre", oldValue = oldName, newValue = newName, onText = onText)
                        ChangeRow(label = "Email", oldValue = oldEmail, newValue = newEmail, onText = onText)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onDismiss() },
                            shape = RoundedCornerShape(16.dp),
                            color = GlassBase.copy(alpha = 0.10f)
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Cancelar",
                                    color = onText.copy(alpha = 0.80f),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onConfirm() },
                            shape = RoundedCornerShape(16.dp),
                            color = accent.copy(alpha = 0.20f)
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Confirmar",
                                    color = accent,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChangeRow(label: String, oldValue: String, newValue: String, onText: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            color = onText.copy(alpha = 0.70f),
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = "Antes: $oldValue",
            color = onText.copy(alpha = 0.85f),
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "Después: $newValue",
            color = onText,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun PasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    show: Boolean,
    onToggleShow: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        label = { Text(label) },
        visualTransformation = if (show) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onToggleShow) {
                Icon(
                    imageVector = if (show) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
private fun AccountTopBar(
    title: String,
    accent: Color,
    accent2: Color,
    onText: Color,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier
                .size(44.dp)
                .clickable { onBack() },
            shape = CircleShape,
            color = GlassBase.copy(alpha = 0.10f),
            tonalElevation = 0.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            listOf(accent.copy(alpha = 0.38f), accent2.copy(alpha = 0.18f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = ButtonTextDark
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        Text(
            text = title,
            color = onText,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ProfileHeroSimple(
    name: String,
    email: String,
    accent: Color,
    accent2: Color,
    onText: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = GlassBase.copy(alpha = 0.09f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(accent.copy(alpha = 0.45f), accent2.copy(alpha = 0.18f))
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = ButtonTextDark)
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = name,
                    color = onText,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = email,
                    color = onText.copy(alpha = 0.78f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun ActionTileWide(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
    accent2: Color,
    onText: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        color = GlassBase.copy(alpha = 0.08f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(accent.copy(alpha = 0.40f), accent2.copy(alpha = 0.35f))
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = ButtonTextDark)
                }

                Column {
                    Text(
                        text = title,
                        color = onText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = subtitle,
                        color = onText.copy(alpha = 0.70f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = onText.copy(alpha = 0.55f)
            )
        }
    }
}

@Composable
private fun GlassPanel(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = GlassBase.copy(alpha = 0.08f)
    ) {
        Column(modifier = Modifier.fillMaxWidth(), content = content)
    }
}

@Composable
private fun FieldRow(
    label: String,
    icon: ImageVector,
    accent: Color,
    accent2: Color,
    onText: Color,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(accent.copy(alpha = 0.40f), accent2.copy(alpha = 0.30f))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = ButtonTextDark)
            }

            Text(
                text = label,
                color = onText,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
        }

        content()
    }
}

@Composable
private fun DividerGlass() {
    Divider(
        modifier = Modifier.padding(horizontal = 14.dp),
        color = Color.White.copy(alpha = 0.06f)
    )
}

@Composable
private fun SectionHeader(title: String, onText: Color) {
    Text(
        text = title,
        color = onText,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun DangerBanner(
    title: String,
    subtitle: String,
    onText: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        color = Color.Red.copy(alpha = 0.10f)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color.Red.copy(alpha = 0.85f)
                )
                Column {
                    Text(
                        text = title,
                        color = onText,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = subtitle,
                        color = onText.copy(alpha = 0.70f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = onText.copy(alpha = 0.55f)
            )
        }
    }
}