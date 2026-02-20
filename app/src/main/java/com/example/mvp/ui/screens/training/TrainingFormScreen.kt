package com.example.mvp.ui.screens.training

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingFormScreen(
    modifier: Modifier = Modifier,
    initial: Training? = null,
    onBack: () -> Unit = {},
    onSave: (Training) -> Unit = {}
) {
    val bgTop = Color(0xFF0B1220)
    val bgMid = Color(0xFF0E2A3B)
    val accent = Color(0xFF00E5FF)

    var name by remember { mutableStateOf(initial?.name ?: "") }
    var dateText by remember { mutableStateOf(initial?.dateText ?: "") }
    var durationText by remember { mutableStateOf(initial?.durationMin?.toString() ?: "") }
    var type by remember { mutableStateOf(initial?.type ?: TrainingType.FUERZA) }

    var showExitDialog by remember { mutableStateOf(false) }

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
                        tint = Color.White
                    )
                }
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Entrenamientos",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = Color.White.copy(alpha = 0.08f)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (initial == null) "Crear entrenamiento" else "Editar entrenamiento",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    SportField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Nombre del entrenamiento",
                        accent = accent
                    )

                    SportField(
                        value = dateText,
                        onValueChange = { dateText = it },
                        label = "Fecha (dd/mm/aaaa)",
                        accent = accent,
                        trailing = { Icon(Icons.Default.Today, null, tint = Color.White.copy(alpha = 0.65f)) }
                    )

                    SportField(
                        value = durationText,
                        onValueChange = { durationText = it.filter(Char::isDigit).take(3) },
                        label = "Duración (en min)",
                        accent = accent,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    TrainingTypeDropdown(
                        selected = type,
                        onSelected = { type = it },
                        accent = accent
                    )

                    val enabled =
                        name.isNotBlank() && dateText.isNotBlank() && (durationText.toIntOrNull() ?: 0) > 0

                    Button(
                        onClick = {
                            val dur = durationText.toIntOrNull() ?: 0
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
                        enabled = enabled,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = accent.copy(alpha = 0.22f))
                    ) {
                        Text(
                            "Guardar",
                            color = accent,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        if (showExitDialog) {
            AlertDialog(
                onDismissRequest = { showExitDialog = false },
                title = { Text("Salir sin guardar") },
                text = { Text("¿Desea salir sin guardar el entrenamiento actual?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showExitDialog = false
                            onBack()
                        }
                    ) { Text("Sí") }
                },
                dismissButton = {
                    TextButton(onClick = { showExitDialog = false }) { Text("No") }
                }
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
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailing: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        label = { Text(label) },
        trailingIcon = trailing,
        keyboardOptions = keyboardOptions,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.White.copy(alpha = 0.18f),
            focusedBorderColor = accent,
            unfocusedLabelColor = Color.White.copy(alpha = 0.65f),
            focusedLabelColor = accent,
            unfocusedTextColor = Color.White,
            focusedTextColor = Color.White,
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
    accent: Color
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
                unfocusedBorderColor = Color.White.copy(alpha = 0.18f),
                focusedBorderColor = accent,
                unfocusedLabelColor = Color.White.copy(alpha = 0.65f),
                focusedLabelColor = accent,
                unfocusedTextColor = Color.White,
                focusedTextColor = Color.White,
                cursorColor = accent
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            TrainingType.entries.forEach { t ->
                DropdownMenuItem(
                    text = { Text(t.label) },
                    onClick = {
                        onSelected(t)
                        expanded = false
                    }
                )
            }
        }
    }
}