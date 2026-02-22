package com.example.mvp.ui.screens.training

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mvp.ui.theme.GlassBase
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingFormScreen(
    modifier: Modifier = Modifier,
    initial: Training? = null,
    onBack: () -> Unit = {},
    onSave: (Training) -> Unit = {}
) {
    val bgTop = MaterialTheme.colorScheme.background
    val bgMid = MaterialTheme.colorScheme.surface
    val accent = MaterialTheme.colorScheme.primary
    val onBg = MaterialTheme.colorScheme.onBackground

    var name by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf("") }
    var durationText by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(TrainingType.FUERZA) }

    var showExitDialog by remember { mutableStateOf(false) }

    var touchedName by remember { mutableStateOf(false) }
    var touchedDate by remember { mutableStateOf(false) }
    var touchedDuration by remember { mutableStateOf(false) }

    LaunchedEffect(initial?.id) {
        name = initial?.name ?: ""
        dateText = initial?.dateText ?: ""
        durationText = initial?.durationMin?.toString() ?: ""
        type = initial?.type ?: TrainingType.FUERZA

        touchedName = false
        touchedDate = false
        touchedDuration = false
    }

    val isEditing = initial != null

    val nameError = remember(name) { validateName(name) }
    val dateError = remember(dateText, isEditing) { validateDateStrict(dateText, allowPast = isEditing) }
    val durationError = remember(durationText) { validateDuration(durationText) }

    val isValid = remember(nameError, dateError, durationError) {
        nameError == null && dateError == null && durationError == null
    }

    val dirty = remember(name, dateText, durationText, type, initial) {
        val initName = initial?.name ?: ""
        val initDate = initial?.dateText ?: ""
        val initDur = initial?.durationMin?.toString() ?: ""
        val initType = initial?.type ?: TrainingType.FUERZA
        name != initName || dateText != initDate || durationText != initDur || type != initType
    }

    fun requestBack() {
        if (dirty) showExitDialog = true else onBack()
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
                .padding(top = 18.dp, bottom = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { requestBack() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = onBg
                    )
                }
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Entrenamientos",
                    color = onBg,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = GlassBase.copy(alpha = 0.08f)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (initial == null) "Crear entrenamiento" else "Editar entrenamiento",
                        color = onBg,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    SportField(
                        value = name,
                        onValueChange = { name = it; touchedName = true },
                        label = "Nombre del entrenamiento",
                        accent = accent,
                        onText = onBg,
                        isError = touchedName && nameError != null,
                        supportingText = if (touchedName) nameError else null
                    )

                    SportField(
                        value = dateText,
                        onValueChange = {
                            dateText = it.filter { ch -> ch.isDigit() || ch == '/' }.take(10)
                            touchedDate = true
                        },
                        label = "Fecha (dd/MM/aaaa)",
                        accent = accent,
                        onText = onBg,
                        trailing = { Icon(Icons.Default.Today, null, tint = onBg.copy(alpha = 0.65f)) },
                        isError = touchedDate && dateError != null,
                        supportingText = if (touchedDate) (dateError ?: "Formato correcto: dd/MM/aaaa") else null
                    )

                    SportField(
                        value = durationText,
                        onValueChange = {
                            durationText = it.filter(Char::isDigit).take(3)
                            touchedDuration = true
                        },
                        label = "Duración (en min)",
                        accent = accent,
                        onText = onBg,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = touchedDuration && durationError != null,
                        supportingText = if (touchedDuration) durationError else "Recomendado: 5–300 min"
                    )

                    TrainingTypeDropdown(
                        selected = type,
                        onSelected = { type = it },
                        accent = accent,
                        onText = onBg
                    )

                    Button(
                        onClick = {
                            touchedName = true
                            touchedDate = true
                            touchedDuration = true
                            if (!isValid) return@Button

                            val dur = durationText.toInt()
                            onSave(
                                Training(
                                    id = initial?.id ?: 0,
                                    name = name.trim(),
                                    dateText = dateText.trim(),
                                    durationMin = dur,
                                    type = type
                                )
                            )
                        },
                        enabled = isValid,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = accent.copy(alpha = 0.22f))
                    ) {
                        Text(
                            text = "Guardar",
                            color = accent,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        if (showExitDialog) {
            GlassAlertDialog(
                title = "Salir sin guardar",
                text = "¿Desea salir sin guardar el entrenamiento actual?",
                confirmText = "Sí",
                dismissText = "No",
                onConfirm = {
                    showExitDialog = false
                    onBack()
                },
                onDismiss = { showExitDialog = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SportField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    accent: Color,
    onText: Color,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailing: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    supportingText: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        label = { Text(label) },
        trailingIcon = trailing,
        keyboardOptions = keyboardOptions,
        isError = isError,
        supportingText = supportingText?.let { { Text(it) } },
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = onText.copy(alpha = 0.18f),
            focusedBorderColor = accent,
            unfocusedLabelColor = onText.copy(alpha = 0.65f),
            focusedLabelColor = accent,
            unfocusedTextColor = onText,
            focusedTextColor = onText,
            cursorColor = accent
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrainingTypeDropdown(
    selected: TrainingType,
    onSelected: (TrainingType) -> Unit,
    accent: Color,
    onText: Color
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected.label,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text("Tipo de entrenamiento") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = onText.copy(alpha = 0.18f),
                focusedBorderColor = accent,
                unfocusedLabelColor = onText.copy(alpha = 0.65f),
                focusedLabelColor = accent,
                unfocusedTextColor = onText,
                focusedTextColor = onText,
                cursorColor = accent
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.Transparent)
        ) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                tonalElevation = 6.dp
            ) {
                Column {
                    TrainingType.entries.forEach { t ->
                        val isSelected = t == selected

                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = t.label,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                                )
                            },
                            onClick = {
                                onSelected(t)
                                expanded = false
                            },
                            modifier = Modifier.background(
                                if (isSelected) accent.copy(alpha = 0.14f) else Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GlassAlertDialog(
    title: String,
    text: String,
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
        tonalElevation = 6.dp,
        shape = RoundedCornerShape(24.dp),
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        title = {
            Text(title, fontWeight = FontWeight.SemiBold)
        },
        text = {
            Text(text)
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    confirmText,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissText, color = MaterialTheme.colorScheme.primary)
            }
        }
    )
}

private fun validateName(raw: String): String? {
    val name = raw.trim()
    if (name.isBlank()) return "El nombre es obligatorio."
    if (name.length < 3) return "Mínimo 3 caracteres."
    if (name.length > 40) return "Máximo 40 caracteres."
    val hasLetterOrDigit = name.any { it.isLetterOrDigit() }
    if (!hasLetterOrDigit) return "Debe contener letras o números."
    return null
}

@RequiresApi(Build.VERSION_CODES.O)
private fun validateDateStrict(raw: String, allowPast: Boolean = false): String? {
    val txt = raw.trim()
    if (txt.isBlank()) return "La fecha es obligatoria."
    if (!Regex("""^\d{2}/\d{2}/\d{4}$""").matches(txt)) {
        return "Formato inválido. Usa dd/MM/aaaa."
    }
    return try {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT)
        val selectedDate = LocalDate.parse(txt, formatter)

        if (!allowPast) {
            val today = LocalDate.now()
            if (selectedDate.isBefore(today)) {
                return "La fecha no puede ser anterior a hoy."
            }
        }
        null
    } catch (_: Exception) {
        "Fecha no válida. Revisa día/mes."
    }
}

private fun validateDuration(raw: String): String? {
    val txt = raw.trim()
    if (txt.isBlank()) return "La duración es obligatoria."
    val minutes = txt.toIntOrNull() ?: return "Debe ser un número."
    if (minutes < 5) return "Mínimo 5 minutos."
    if (minutes > 300) return "Máximo 300 minutos."
    return null
}