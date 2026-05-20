package com.example.mvp.ui.screens.matches

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mvp.domain.model.Competition
import com.example.mvp.domain.model.Match
import com.example.mvp.domain.model.MatchResult
import com.example.mvp.ui.theme.ButtonTextDark
import com.example.mvp.ui.theme.Draw
import com.example.mvp.ui.theme.GlassBase
import com.example.mvp.ui.theme.Loss
import com.example.mvp.ui.theme.Win
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle
import kotlin.math.abs

private enum class MatchFormMode(val label: String) {
    SCHEDULED("Programar"),
    FINISHED("Resultado")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchFormScreen(
    modifier: Modifier = Modifier,
    initial: Match? = null,
    existingMatches: List<Match> = emptyList(),
    onBack: () -> Unit = {},
    onSave: (Match) -> Unit = {}
) {
    val bgTop = MaterialTheme.colorScheme.background
    val bgMid = MaterialTheme.colorScheme.surface
    val accent = MaterialTheme.colorScheme.primary
    val accent2 = MaterialTheme.colorScheme.secondary
    val onBg = MaterialTheme.colorScheme.onBackground

    var rival by remember { mutableStateOf(initial?.rival ?: "") }
    var dateText by remember { mutableStateOf(initial?.dateText ?: LocalDate.now().format(dateFormatter())) }
    var competition by remember { mutableStateOf(initial?.competition ?: Competition.LIGA) }
    var mode by remember { mutableStateOf(if (initial?.isFinished == false) MatchFormMode.SCHEDULED else MatchFormMode.FINISHED) }
    var goalsForText by remember { mutableStateOf(initial?.goalsFor?.toString() ?: "0") }
    var goalsAgainstText by remember { mutableStateOf(initial?.goalsAgainst?.toString() ?: "0") }
    var showExitDialog by remember { mutableStateOf(false) }
    var touchedRival by remember { mutableStateOf(false) }
    var touchedDate by remember { mutableStateOf(false) }

    val isFinished = mode == MatchFormMode.FINISHED
    val goalsFor = goalsForText.toIntOrNull() ?: 0
    val goalsAgainst = goalsAgainstText.toIntOrNull() ?: 0
    val parsedDate = remember(dateText) { parseDateOrNull(dateText) }

    val computedResult = remember(goalsFor, goalsAgainst) {
        when {
            goalsFor > goalsAgainst -> MatchResult.VICTORIA
            goalsFor == goalsAgainst -> MatchResult.EMPATE
            else -> MatchResult.DERROTA
        }
    }

    val rivalError = remember(rival) { validateMatchRival(rival) }
    val dateError = remember(dateText, isFinished) { validateDateStrict(dateText, isFinished) }
    val calendarConflictError = remember(parsedDate, existingMatches, initial?.id) {
        validateMatchCalendarSpacing(
            selectedDate = parsedDate,
            currentMatchId = initial?.id ?: 0,
            matches = existingMatches
        )
    }
    val isValid = rivalError == null && dateError == null && calendarConflictError == null

    val dirty = remember(rival, dateText, competition, mode, goalsForText, goalsAgainstText, initial) {
        rival != (initial?.rival ?: "") ||
                dateText != (initial?.dateText ?: LocalDate.now().format(dateFormatter())) ||
                competition != (initial?.competition ?: Competition.LIGA) ||
                mode != (if (initial?.isFinished == false) MatchFormMode.SCHEDULED else MatchFormMode.FINISHED) ||
                goalsForText != (initial?.goalsFor?.toString() ?: "0") ||
                goalsAgainstText != (initial?.goalsAgainst?.toString() ?: "0")
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
                .background(
                    brush = Brush.radialGradient(listOf(accent.copy(alpha = 0.28f), Color.Transparent)),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 18.dp, bottom = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { requestBack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = onBg)
                }
                Spacer(Modifier.width(4.dp))
                Column {
                    Text(
                        text = if (initial == null) "Nuevo partido" else "Editar partido",
                        color = onBg,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Programa una fecha futura o registra un resultado final",
                        color = onBg.copy(alpha = 0.65f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = GlassBase.copy(alpha = 0.08f)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ModeChip(
                            selected = mode == MatchFormMode.SCHEDULED,
                            text = "Programar",
                            accent = accent2,
                            onText = onBg,
                            modifier = Modifier.weight(1f),
                            onClick = { mode = MatchFormMode.SCHEDULED }
                        )
                        ModeChip(
                            selected = mode == MatchFormMode.FINISHED,
                            text = "Resultado",
                            accent = accent,
                            onText = onBg,
                            modifier = Modifier.weight(1f),
                            onClick = { mode = MatchFormMode.FINISHED }
                        )
                    }

                    OutlinedTextField(
                        value = rival,
                        onValueChange = {
                            rival = it.take(40)
                            touchedRival = true
                        },
                        singleLine = true,
                        label = { Text("Rival") },
                        leadingIcon = { Icon(Icons.Default.SportsSoccer, null) },
                        isError = touchedRival && rivalError != null,
                        supportingText = { if (touchedRival) Text(rivalError ?: "Nombre del rival correcto") },
                        colors = fieldColors(accent, onBg),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = dateText,
                        onValueChange = {
                            dateText = it.filter { ch -> ch.isDigit() || ch == '/' }.take(10)
                            touchedDate = true
                        },
                        singleLine = true,
                        label = { Text("Fecha (dd/MM/aaaa)") },
                        leadingIcon = { Icon(Icons.Default.CalendarToday, null) },
                        isError = touchedDate && dateError != null,
                        supportingText = {
                            if (touchedDate) Text(dateError ?: calendarConflictError ?: if (isFinished) "Fecha válida" else "Partido programado")
                        },
                        colors = fieldColors(accent, onBg),
                        modifier = Modifier.fillMaxWidth()
                    )

                    CompetitionDropdown(
                        selected = competition,
                        onSelected = { competition = it },
                        accent = accent,
                        onText = onBg
                    )
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = GlassBase.copy(alpha = 0.08f)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isFinished) Icons.Default.SportsScore else Icons.Default.EventAvailable,
                                contentDescription = null,
                                tint = if (isFinished) accent else accent2
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = if (isFinished) "Marcador final" else "Partido programado",
                                color = onBg,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        StatusBadge(
                            label = if (isFinished) computedResult.label else "Pendiente",
                            color = if (isFinished) resultColor(computedResult) else accent2
                        )
                    }

                    if (isFinished) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ScoreBox(
                                title = "Tú",
                                value = goalsForText,
                                onValueChange = { goalsForText = it.onlyScoreDigits() },
                                accent = accent,
                                onText = onBg,
                                modifier = Modifier.weight(1f)
                            )
                            Box(
                                modifier = Modifier
                                    .height(56.dp)
                                    .width(56.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Brush.horizontalGradient(listOf(accent.copy(alpha = 0.35f), accent2.copy(alpha = 0.30f)))),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("-", color = ButtonTextDark, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                            }
                            ScoreBox(
                                title = "Rival",
                                value = goalsAgainstText,
                                onValueChange = { goalsAgainstText = it.onlyScoreDigits() },
                                accent = accent,
                                onText = onBg,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    } else {
                        Text(
                            text = "Se guardará en el calendario sin afectar a estadísticas ni forma reciente hasta que lo edites y marques como resultado final.",
                            color = onBg.copy(alpha = 0.68f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Button(
                        onClick = {
                            touchedRival = true
                            touchedDate = true
                            if (!isValid || parsedDate == null) return@Button

                            onSave(
                                Match(
                                    id = initial?.id ?: 0,
                                    rival = rival.trim(),
                                    dateEpochDay = parsedDate.toEpochDay(),
                                    competition = competition,
                                    goalsFor = if (isFinished) goalsFor else 0,
                                    goalsAgainst = if (isFinished) goalsAgainst else 0,
                                    isFinished = isFinished
                                )
                            )
                        },
                        enabled = isValid,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        val alpha = if (isValid) 1f else 0.35f
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(18.dp))
                                .background(Brush.horizontalGradient(listOf(accent.copy(alpha = alpha), accent2.copy(alpha = alpha)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isFinished) "Guardar resultado" else "Programar partido",
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
                title = { Text("Salir sin guardar", fontWeight = FontWeight.SemiBold) },
                text = { Text("¿Deseas salir sin guardar los cambios del partido?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showExitDialog = false
                            onBack()
                        }
                    ) { Text("Sí", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold) }
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

private fun validateMatchCalendarSpacing(
    selectedDate: LocalDate?,
    currentMatchId: Int,
    matches: List<Match>
): String? {
    if (selectedDate == null) return null

    val conflict = matches
        .filter { it.id != currentMatchId }
        .mapNotNull { match ->
            val distance = abs(match.dateEpochDay - selectedDate.toEpochDay())
            if (distance <= MIN_REST_DAYS_BETWEEN_MATCHES) match to distance else null
        }
        .minByOrNull { it.second }
        ?.first

    return when {
        conflict == null -> null
        conflict.date == selectedDate -> "Ya hay un partido ese día. No puedes registrar dos partidos en la misma fecha."
        else -> "Debe haber 2 días completos de descanso entre partidos. Conflicto con ${conflict.rival} (${conflict.dateText})."
    }
}

@Composable
private fun ModeChip(
    selected: Boolean,
    text: String,
    accent: Color,
    onText: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(44.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = if (selected) accent.copy(alpha = 0.22f) else onText.copy(alpha = 0.06f)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                color = if (selected) accent else onText.copy(alpha = 0.72f),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun StatusBadge(label: String, color: Color) {
    Surface(shape = RoundedCornerShape(14.dp), color = color.copy(alpha = 0.16f)) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            color = color,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompetitionDropdown(
    selected: Competition,
    onSelected: (Competition) -> Unit,
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
            label = { Text("Competición") },
            leadingIcon = { Icon(Icons.Default.Flag, null) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            colors = fieldColors(accent, onText)
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
                    Competition.entries.forEach { c ->
                        val isSelected = c == selected
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = c.label,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                                )
                            },
                            onClick = {
                                onSelected(c)
                                expanded = false
                            },
                            modifier = Modifier.background(if (isSelected) accent.copy(alpha = 0.14f) else Color.Transparent)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScoreBox(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    accent: Color,
    onText: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        color = GlassBase.copy(alpha = 0.06f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, color = onText.copy(alpha = 0.70f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    unfocusedTextColor = onText,
                    focusedTextColor = onText,
                    cursorColor = accent
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun fieldColors(accent: Color, onText: Color) = OutlinedTextFieldDefaults.colors(
    unfocusedBorderColor = onText.copy(alpha = 0.18f),
    focusedBorderColor = accent,
    errorBorderColor = MaterialTheme.colorScheme.error,
    unfocusedLabelColor = onText.copy(alpha = 0.65f),
    focusedLabelColor = accent,
    errorLabelColor = MaterialTheme.colorScheme.error,
    unfocusedTextColor = onText,
    focusedTextColor = onText,
    errorTextColor = onText,
    unfocusedLeadingIconColor = onText.copy(alpha = 0.6f),
    focusedLeadingIconColor = accent,
    errorLeadingIconColor = MaterialTheme.colorScheme.error,
    cursorColor = accent,
    errorCursorColor = MaterialTheme.colorScheme.error
)

private fun String.onlyScoreDigits(): String {
    val cleaned = this.filter { it.isDigit() }.take(2)
    return if (cleaned.isBlank()) "0" else cleaned
}

private fun validateMatchRival(raw: String): String? {
    val txt = raw.trim()
    if (txt.isBlank()) return "El rival es obligatorio."
    if (txt.length < 3) return "El rival debe tener al menos 3 caracteres."
    if (txt.length > 40) return "El rival no puede superar los 40 caracteres."
    if (txt.contains(Regex("\\s{2,}"))) return "Evita usar espacios dobles."
    return null
}

private fun dateFormatter(): DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT)

private fun parseDateOrNull(raw: String): LocalDate? {
    return runCatching { LocalDate.parse(raw.trim(), dateFormatter()) }.getOrNull()
}

private fun validateDateStrict(raw: String, isFinished: Boolean): String? {
    val txt = raw.trim()
    if (txt.isBlank()) return "La fecha es obligatoria."
    if (!Regex("""^\d{2}/\d{2}/\d{4}$""").matches(txt)) return "Formato inválido. Usa dd/MM/aaaa."

    val parsed = parseDateOrNull(txt) ?: return "Fecha no válida. Revisa día/mes."
    if (isFinished && parsed.isAfter(LocalDate.now())) {
        return "Un partido futuro debe guardarse como programado."
    }
    return null
}

private fun resultColor(result: MatchResult): Color = when (result) {
    MatchResult.VICTORIA -> Win
    MatchResult.EMPATE -> Draw
    MatchResult.DERROTA -> Loss
}

private const val MIN_REST_DAYS_BETWEEN_MATCHES = 2L