package com.example.mvp.ui.screens.players

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.mvp.domain.model.Player
import com.example.mvp.domain.model.PLAYER_MAX_NUMBER
import com.example.mvp.domain.model.PLAYER_MIN_NUMBER
import com.example.mvp.domain.model.PlayerAttributes
import com.example.mvp.domain.model.PlayerLevel
import com.example.mvp.domain.model.PlayerPosition
import com.example.mvp.domain.model.PlayerStatus
import com.example.mvp.domain.model.PlayerStyle
import com.example.mvp.domain.model.PlayerValidator
import com.example.mvp.domain.model.calculateRating
import com.example.mvp.domain.model.defaultStyleFor
import com.example.mvp.domain.model.generateAttributes
import com.example.mvp.domain.model.stylesFor
import com.example.mvp.ui.theme.ButtonTextDark
import com.example.mvp.ui.theme.GlassBase

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
    var level by remember { mutableStateOf(initial?.level ?: PlayerLevel.BUENO) }
    var style by remember { mutableStateOf(initial?.style ?: defaultStyleFor(position)) }
    var status by remember {
        mutableStateOf(
            if (initial?.status == PlayerStatus.LESIONADO) {
                PlayerStatus.LESIONADO
            } else {
                PlayerStatus.DISPONIBLE
            }
        )
    }

    var showExitDialog by remember { mutableStateOf(false) }
    var touchedName by remember { mutableStateOf(false) }
    var touchedAge by remember { mutableStateOf(false) }
    var touchedNumber by remember { mutableStateOf(false) }

    LaunchedEffect(position) {
        if (style !in stylesFor(position)) {
            style = defaultStyleFor(position)
        }
    }

    val age = ageText.toIntOrNull() ?: 0
    val ratingInt = remember(position, level, style) { calculateRating(position, level, style) }
    val attributes = remember(position, level, style) { generateAttributes(position, level, style) }
    val title = if (initial == null) "Nuevo jugador" else "Editar jugador"

    val nameError = remember(name) { PlayerValidator.validateName(name) }

    val ageError = remember(ageText) {
        val v = ageText.toIntOrNull()
        when {
            ageText.isBlank() -> "La edad es obligatoria."
            v == null -> "Introduce una edad válida."
            else -> PlayerValidator.validateAge(v)
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

    val dirty = remember(name, ageText, number, position, level, style, status, initial) {
        val initName = initial?.name ?: ""
        val initAge = initial?.age?.toString() ?: "18"
        val initNumber = initial?.number ?: 10
        val initPos = initial?.position ?: PlayerPosition.MED
        val initLevel = initial?.level ?: PlayerLevel.BUENO
        val initStyle = initial?.style ?: defaultStyleFor(initPos)
        val initStatus = if (initial?.status == PlayerStatus.LESIONADO) {
            PlayerStatus.LESIONADO
        } else {
            PlayerStatus.DISPONIBLE
        }

        name != initName ||
                ageText != initAge ||
                number != initNumber ||
                position != initPos ||
                level != initLevel ||
                style != initStyle ||
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
                    .verticalScroll(rememberScrollState())
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
                                            listOf(
                                                accent.copy(alpha = 0.35f),
                                                accent2.copy(alpha = 0.30f)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$ratingInt",
                                        color = ButtonTextDark,
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = "OVR",
                                        color = ButtonTextDark.copy(alpha = 0.80f),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
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
                                Text(
                                    text = "${position.label} · ${level.label} · ${style.label}",
                                    color = onBg.copy(alpha = 0.62f),
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1
                                )
                            }
                        }

                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it
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
                                                number = (number - 1).coerceAtLeast(PLAYER_MIN_NUMBER)
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
                                                number = (number + 1).coerceAtMost(PLAYER_MAX_NUMBER)
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

                        PlayerProfileCard(
                            selectedPosition = position,
                            selectedLevel = level,
                            selectedStyle = style,
                            onPositionSelected = { newPosition ->
                                position = newPosition
                                if (style !in stylesFor(newPosition)) {
                                    style = defaultStyleFor(newPosition)
                                }
                            },
                            onLevelSelected = { level = it },
                            onStyleSelected = { style = it },
                            accent = accent,
                            accent2 = accent2,
                            onBg = onBg
                        )

                        AttributePreview(
                            position = position,
                            attributes = attributes,
                            accent = accent,
                            onBg = onBg
                        )

                        Text(
                            text = "Estado",
                            color = onBg.copy(alpha = 0.70f),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        val availabilityOptions = listOf(
                            PlayerStatus.DISPONIBLE,
                            PlayerStatus.LESIONADO
                        )

                        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                            availabilityOptions.forEachIndexed { index, s ->
                                SegmentedButton(
                                    selected = status == s,
                                    onClick = { status = s },
                                    shape = SegmentedButtonDefaults.itemShape(index, availabilityOptions.size)
                                ) {
                                    Text(s.label)
                                }
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
                                        age = age,
                                        number = number,
                                        status = status,
                                        level = level,
                                        style = style,
                                        lineupSlot = when {
                                            status == PlayerStatus.LESIONADO -> null
                                            initial?.lineupSlot != null &&
                                                    !initial.lineupSlot.startsWith("BENCH_") &&
                                                    initial.position != position -> null
                                            else -> initial?.lineupSlot
                                        }
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
                                            listOf(
                                                accent.copy(alpha = alpha),
                                                accent2.copy(alpha = alpha)
                                            )
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
                    title = {
                        Text(
                            text = "Salir sin guardar",
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    text = {
                        Text("¿Desea salir sin guardar los cambios del jugador?")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showExitDialog = false
                                onBack()
                            }
                        ) {
                            Text(
                                text = "Sí",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showExitDialog = false }) {
                            Text(
                                text = "No",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun PlayerProfileCard(
    selectedPosition: PlayerPosition,
    selectedLevel: PlayerLevel,
    selectedStyle: PlayerStyle,
    onPositionSelected: (PlayerPosition) -> Unit,
    onLevelSelected: (PlayerLevel) -> Unit,
    onStyleSelected: (PlayerStyle) -> Unit,
    accent: Color,
    accent2: Color,
    onBg: Color
) {
    var showPositionDialog by remember { mutableStateOf(false) }
    var showLevelDialog by remember { mutableStateOf(false) }
    var showStyleDialog by remember { mutableStateOf(false) }

    val availableStyles = remember(selectedPosition) {
        stylesFor(selectedPosition)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = GlassBase.copy(alpha = 0.075f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.04f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    accent.copy(alpha = 0.28f),
                                    accent2.copy(alpha = 0.22f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚡",
                        color = accent,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black
                    )
                }

                Column(Modifier.weight(1f)) {
                    Text(
                        text = "Perfil de jugador",
                        color = onBg,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Define posición, nivel y estilo base.",
                        color = onBg.copy(alpha = 0.58f),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfileSelectorField(
                    label = "Posición",
                    value = selectedPosition.label,
                    accent = accent,
                    onBg = onBg,
                    modifier = Modifier.weight(1f),
                    onClick = { showPositionDialog = true }
                )

                ProfileSelectorField(
                    label = "Nivel",
                    value = selectedLevel.label,
                    accent = accent,
                    onBg = onBg,
                    modifier = Modifier.weight(1f),
                    onClick = { showLevelDialog = true }
                )
            }

            ProfileSelectorField(
                label = "Estilo",
                value = selectedStyle.label,
                accent = accent,
                onBg = onBg,
                modifier = Modifier.fillMaxWidth(),
                onClick = { showStyleDialog = true }
            )
        }
    }

    if (showPositionDialog) {
        SelectionDialog(
            title = "Selecciona la posición",
            options = PlayerPosition.entries.toList(),
            selected = selectedPosition,
            optionLabel = { it.label },
            accent = accent,
            onDismiss = { showPositionDialog = false },
            onSelect = { newPosition ->
                onPositionSelected(newPosition)
                if (selectedStyle !in stylesFor(newPosition)) {
                    onStyleSelected(defaultStyleFor(newPosition))
                }
                showPositionDialog = false
            }
        )
    }

    if (showLevelDialog) {
        SelectionDialog(
            title = "Selecciona el nivel",
            options = PlayerLevel.entries.toList(),
            selected = selectedLevel,
            optionLabel = { it.label },
            accent = accent,
            onDismiss = { showLevelDialog = false },
            onSelect = { newLevel ->
                onLevelSelected(newLevel)
                showLevelDialog = false
            }
        )
    }

    if (showStyleDialog) {
        SelectionDialog(
            title = "Selecciona el estilo",
            options = availableStyles,
            selected = selectedStyle,
            optionLabel = { it.label },
            accent = accent,
            onDismiss = { showStyleDialog = false },
            onSelect = { newStyle ->
                onStyleSelected(newStyle)
                showStyleDialog = false
            }
        )
    }
}

@Composable
private fun ProfileSelectorField(
    label: String,
    value: String,
    accent: Color,
    onBg: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = GlassBase.copy(alpha = 0.085f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.14f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = label,
                    color = onBg.copy(alpha = 0.56f),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = value,
                    color = accent,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }

            Text(
                text = "⌄",
                color = onBg.copy(alpha = 0.70f),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun <T> SelectionDialog(
    title: String,
    options: List<T>,
    selected: T,
    optionLabel: (T) -> String,
    accent: Color,
    onDismiss: () -> Unit,
    onSelect: (T) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
        tonalElevation = 8.dp,
        shape = RoundedCornerShape(26.dp),
        title = {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                options.forEach { option ->
                    val isSelected = option == selected

                    Surface(
                        onClick = { onSelect(option) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) {
                            accent.copy(alpha = 0.16f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.40f)
                        },
                        border = if (isSelected) {
                            BorderStroke(1.dp, accent.copy(alpha = 0.45f))
                        } else {
                            BorderStroke(1.dp, Color.White.copy(alpha = 0.035f))
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = optionLabel(option),
                                color = if (isSelected) accent else MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )

                            if (isSelected) {
                                Text(
                                    text = "✓",
                                    color = accent,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cerrar",
                    color = accent,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    )
}

@Composable
private fun AttributePreview(
    position: PlayerPosition,
    attributes: PlayerAttributes,
    accent: Color,
    onBg: Color
) {
    val values = if (position == PlayerPosition.POR) {
        attributes.goalkeeperAttributes()
    } else {
        attributes.fieldAttributes()
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = GlassBase.copy(alpha = 0.06f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Atributos generados",
                color = onBg,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            values.forEach { (label, value) ->
                AttributeRow(label = label, value = value, accent = accent, onBg = onBg)
            }
        }
    }
}

@Composable
private fun AttributeRow(label: String, value: Int, accent: Color, onBg: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = label,
            color = onBg.copy(alpha = 0.68f),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(82.dp)
        )
        LinearProgressIndicator(
            progress = value / 100f,
            modifier = Modifier
                .weight(1f)
                .height(7.dp)
                .clip(RoundedCornerShape(50)),
            color = accent,
            trackColor = GlassBase.copy(alpha = 0.16f)
        )
        Text(
            text = "$value",
            color = onBg,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(28.dp)
        )
    }
}