package com.example.mvp.ui.screens.players

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mvp.ui.theme.ButtonTextDark
import com.example.mvp.ui.theme.GlassBase
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerFormScreen(
    modifier: Modifier = Modifier,
    initial: Player? = null,

    existingPlayers: List<Player> = emptyList(),

    onBack: () -> Unit = {},
    onSave: (Player) -> Unit = {}
) {
    val bgTop = MaterialTheme.colorScheme.background
    val bgMid = MaterialTheme.colorScheme.surface
    val accent = MaterialTheme.colorScheme.primary
    val accent2 = MaterialTheme.colorScheme.secondary
    val onBg = MaterialTheme.colorScheme.onBackground

    var name by remember { mutableStateOf(initial?.name ?: "") }
    var ageText by remember { mutableStateOf(initial?.age?.toString() ?: "18") }
    var number by remember { mutableIntStateOf(initial?.number ?: 10) }
    var position by remember { mutableStateOf(initial?.position ?: PlayerPosition.MED) }
    var rating by remember { mutableFloatStateOf((initial?.rating ?: 78).toFloat()) }
    var status by remember { mutableStateOf(initial?.status ?: PlayerStatus.TITULAR) }

    var showExitDialog by remember { mutableStateOf(false) }

    var touchedName by remember { mutableStateOf(false) }
    var touchedAge by remember { mutableStateOf(false) }
    var touchedNumber by remember { mutableStateOf(false) }

    val age = ageText.toIntOrNull() ?: 0
    val ratingInt = rating.roundToInt().coerceIn(40, 99)
    val title = if (initial == null) "Nuevo jugador" else "Editar jugador"

    val nameError = remember(name) { validatePlayerName(name) }

    val ageError = remember(ageText) {
        val v = ageText.toIntOrNull()
        when {
            ageText.isBlank() -> "La edad es obligatoria."
            v == null -> "Introduce una edad válida."
            v !in 16..40 -> "La edad debe estar entre 16 y 40."
            else -> null
        }
    }

    val numberError = remember(number, existingPlayers, initial) {
        val exists = existingPlayers.any { p ->
            p.id != (initial?.id ?: 0) && p.number == number
        }
        if (exists) "El dorsal #$number ya está asignado a otro jugador." else null
    }

    val enabled = remember(nameError, ageError, numberError) {
        nameError == null && ageError == null && numberError == null
    }

    val dirty = remember(name, ageText, number, position, rating, status, initial) {
        val initName = initial?.name ?: ""
        val initAge = initial?.age?.toString() ?: "18"
        val initNumber = initial?.number ?: 10
        val initPos = initial?.position ?: PlayerPosition.MED
        val initRating = (initial?.rating ?: 78).toFloat()
        val initStatus = initial?.status ?: PlayerStatus.TITULAR

        name != initName ||
                ageText != initAge ||
                number != initNumber ||
                position != initPos ||
                rating != initRating ||
                status != initStatus
    }

    fun requestBack() {
        if (dirty) showExitDialog = true else onBack()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(bgTop, bgMid, bgTop)))
                .padding(horizontal = 20.dp)
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 70.dp, y = (-60).dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(accent.copy(alpha = 0.25f), Color.Transparent)
                        ),
                        shape = CircleShape
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp, bottom = 14.dp),
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
                        text = "Jugador",
                        color = onBg,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    color = GlassBase.copy(alpha = 0.08f)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(accent.copy(alpha = 0.35f), accent2.copy(alpha = 0.30f))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = initials(name),
                                    color = ButtonTextDark,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = title,
                                    color = onBg.copy(alpha = 0.75f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = if (name.isBlank()) "Nombre del jugador" else name.trim(),
                                    color = onBg,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = accent.copy(alpha = 0.16f)
                            ) {
                                Text(
                                    text = "OVR $ratingInt",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    color = accent,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it.take(40)
                                touchedName = true
                            },
                            singleLine = true,
                            label = { Text("Nombre y apellidos") },
                            isError = touchedName && nameError != null,
                            supportingText = {
                                if (touchedName && nameError != null) Text(nameError)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = onBg.copy(alpha = 0.18f),
                                focusedBorderColor = accent,
                                unfocusedLabelColor = onBg.copy(alpha = 0.65f),
                                focusedLabelColor = accent,
                                unfocusedTextColor = onBg,
                                focusedTextColor = onBg,
                                cursorColor = accent
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = ageText,
                                onValueChange = {
                                    ageText = it.filter(Char::isDigit).take(2)
                                    touchedAge = true
                                },
                                singleLine = true,
                                label = { Text("Edad") },
                                isError = touchedAge && ageError != null,
                                supportingText = {
                                    if (touchedAge && ageError != null) Text(ageError)
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = onBg.copy(alpha = 0.18f),
                                    focusedBorderColor = accent,
                                    unfocusedLabelColor = onBg.copy(alpha = 0.65f),
                                    focusedLabelColor = accent,
                                    unfocusedTextColor = onBg,
                                    focusedTextColor = onBg,
                                    cursorColor = accent
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(18.dp),
                                color = GlassBase.copy(alpha = 0.06f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Dorsal",
                                            color = onBg.copy(alpha = 0.70f),
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(onClick = {
                                                touchedNumber = true
                                                number = (number - 1).coerceAtLeast(1)
                                            }) {
                                                Icon(Icons.Default.Remove, null, tint = onBg)
                                            }
                                            Text(
                                                text = "#$number",
                                                color = onBg,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            IconButton(onClick = {
                                                touchedNumber = true
                                                number = (number + 1).coerceAtMost(99)
                                            }) {
                                                Icon(Icons.Default.Add, null, tint = onBg)
                                            }
                                        }
                                    }

                                    if (touchedNumber && numberError != null) {
                                        Text(
                                            text = numberError,
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }

                        Text(
                            text = "Posición",
                            color = onBg.copy(alpha = 0.70f),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            PlayerPosition.entries.forEach { pos ->
                                FilterChip(
                                    selected = position == pos,
                                    onClick = { position = pos },
                                    label = { Text(pos.short) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = accent.copy(alpha = 0.18f),
                                        selectedLabelColor = accent,
                                        containerColor = GlassBase.copy(alpha = 0.06f),
                                        labelColor = onBg.copy(alpha = 0.78f)
                                    ),
                                    border = null
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = GlassBase.copy(alpha = 0.06f)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Valoración",
                                        color = onBg.copy(alpha = 0.70f),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "$ratingInt",
                                        color = accent,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Slider(
                                    value = rating,
                                    onValueChange = { rating = it },
                                    valueRange = 40f..99f,
                                    colors = SliderDefaults.colors(
                                        activeTrackColor = accent,
                                        thumbColor = accent
                                    )
                                )
                            }
                        }

                        Text(
                            text = "Estado",
                            color = onBg.copy(alpha = 0.70f),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                            PlayerStatus.entries.forEachIndexed { index, s ->
                                SegmentedButton(
                                    selected = status == s,
                                    onClick = { status = s },
                                    shape = SegmentedButtonDefaults.itemShape(index, PlayerStatus.entries.size)
                                ) { Text(s.label) }
                            }
                        }

                        Button(
                            onClick = {
                                touchedName = true
                                touchedAge = true
                                touchedNumber = true
                                if (!enabled) return@Button

                                onSave(
                                    Player(
                                        id = initial?.id ?: 0,
                                        name = name.trim(),
                                        position = position,
                                        age = age.coerceIn(16, 40),
                                        number = number.coerceIn(1, 99),
                                        rating = ratingInt,
                                        status = status
                                    )
                                )
                            },
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
                                    text = if (initial == null) "Crear jugador" else "Guardar cambios",
                                    color = ButtonTextDark,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            if (showExitDialog) {
                AlertDialog(
                    onDismissRequest = { showExitDialog = false },
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                    tonalElevation = 6.dp,
                    shape = RoundedCornerShape(24.dp),
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    title = { Text("Salir sin guardar", fontWeight = FontWeight.SemiBold) },
                    text = { Text("¿Desea salir sin guardar los cambios del jugador?") },
                    confirmButton = {
                        TextButton(onClick = { showExitDialog = false; onBack() }) {
                            Text("Sí", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showExitDialog = false }) {
                            Text("No", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                )
            }
        }
    }
}

private fun initials(name: String): String {
    val parts = name.trim().split(" ").filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> "??"
        parts.size == 1 -> parts.first().take(2).uppercase()
        else -> (parts[0].take(1) + parts[1].take(1)).uppercase()
    }
}

private fun validatePlayerName(raw: String): String? {
    val name = raw.trim()

    if (name.isBlank()) return "El nombre es obligatorio."
    if (name.length < 3) return "El nombre debe tener al menos 3 caracteres."
    if (name.length > 40) return "El nombre no puede superar 40 caracteres."
    if (name.contains(Regex("""\s{2,}"""))) return "Evita espacios dobles."
    if (!Regex("""^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ' -]+$""").matches(name)) {
        return "Solo letras y espacios (se permite ' y -)."
    }
    return null
}