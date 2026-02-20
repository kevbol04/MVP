package com.example.mvp.ui.screens.matches

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mvp.ui.theme.ButtonTextDark
import com.example.mvp.ui.theme.Draw
import com.example.mvp.ui.theme.GlassBase
import com.example.mvp.ui.theme.Loss
import com.example.mvp.ui.theme.Win

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchFormScreen(
    modifier: Modifier = Modifier,
    initial: Match? = null,
    onBack: () -> Unit = {},
    onSave: (Match) -> Unit = {}
) {
    val bgTop = MaterialTheme.colorScheme.background
    val bgMid = MaterialTheme.colorScheme.surface
    val accent = MaterialTheme.colorScheme.primary
    val accent2 = MaterialTheme.colorScheme.secondary
    val onBg = MaterialTheme.colorScheme.onBackground

    var rival by remember { mutableStateOf(initial?.rival ?: "") }
    var dateText by remember { mutableStateOf(initial?.dateText ?: "") }
    var competition by remember { mutableStateOf(initial?.competition ?: Competition.LIGA) }

    var goalsForText by remember { mutableStateOf(initial?.goalsFor?.toString() ?: "0") }
    var goalsAgainstText by remember { mutableStateOf(initial?.goalsAgainst?.toString() ?: "0") }

    var showExitDialog by remember { mutableStateOf(false) }

    val goalsFor = goalsForText.toIntOrNull() ?: 0
    val goalsAgainst = goalsAgainstText.toIntOrNull() ?: 0

    val computedResult = remember(goalsFor, goalsAgainst) {
        when {
            goalsFor > goalsAgainst -> MatchResult.VICTORIA
            goalsFor == goalsAgainst -> MatchResult.EMPATE
            else -> MatchResult.DERROTA
        }
    }

    val dirty = remember(rival, dateText, competition, goalsForText, goalsAgainstText, initial) {
        val initRival = initial?.rival ?: ""
        val initDate = initial?.dateText ?: ""
        val initComp = initial?.competition ?: Competition.LIGA
        val initFor = initial?.goalsFor?.toString() ?: "0"
        val initAgainst = initial?.goalsAgainst?.toString() ?: "0"

        rival != initRival ||
                dateText != initDate ||
                competition != initComp ||
                goalsForText != initFor ||
                goalsAgainstText != initAgainst
    }

    fun requestBack() {
        if (dirty) showExitDialog = true else onBack()
    }

    val (badgeBase, badgeText) = when (computedResult) {
        MatchResult.VICTORIA -> Win to Win
        MatchResult.EMPATE -> Draw to Draw
        MatchResult.DERROTA -> Loss to Loss
    }
    val badgeBg = badgeBase.copy(alpha = 0.16f)
    val badgeFg = badgeText

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
                    text = "Partidos",
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
                        text = if (initial == null) "Añadir partido" else "Editar partido",
                        color = onBg,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    OutlinedTextField(
                        value = rival,
                        onValueChange = { rival = it },
                        singleLine = true,
                        label = { Text("Rival") },
                        leadingIcon = { Icon(Icons.Default.SportsSoccer, null) },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = onBg.copy(alpha = 0.18f),
                            focusedBorderColor = accent,
                            unfocusedLabelColor = onBg.copy(alpha = 0.65f),
                            focusedLabelColor = accent,
                            unfocusedTextColor = onBg,
                            focusedTextColor = onBg,
                            unfocusedLeadingIconColor = onBg.copy(alpha = 0.6f),
                            focusedLeadingIconColor = accent,
                            cursorColor = accent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = dateText,
                        onValueChange = { dateText = it },
                        singleLine = true,
                        label = { Text("Fecha (dd/mm/aaaa)") },
                        leadingIcon = { Icon(Icons.Default.CalendarToday, null) },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = onBg.copy(alpha = 0.18f),
                            focusedBorderColor = accent,
                            unfocusedLabelColor = onBg.copy(alpha = 0.65f),
                            focusedLabelColor = accent,
                            unfocusedTextColor = onBg,
                            focusedTextColor = onBg,
                            unfocusedLeadingIconColor = onBg.copy(alpha = 0.6f),
                            focusedLeadingIconColor = accent,
                            cursorColor = accent
                        ),
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
                shape = RoundedCornerShape(22.dp),
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
                        Text(
                            text = "Marcador",
                            color = onBg,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = badgeBg
                        ) {
                            Text(
                                text = computedResult.label,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                color = badgeFg,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

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
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(accent.copy(alpha = 0.35f), accent2.copy(alpha = 0.30f))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "-",
                                color = ButtonTextDark,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black
                            )
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

                    val enabled = rival.isNotBlank() && dateText.isNotBlank()
                    Button(
                        onClick = {
                            onSave(
                                Match(
                                    id = initial?.id ?: 0,
                                    rival = rival.trim(),
                                    dateText = dateText.trim(),
                                    competition = competition,
                                    goalsFor = goalsFor,
                                    goalsAgainst = goalsAgainst,
                                    result = computedResult
                                )
                            )
                        },
                        enabled = enabled,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
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
                                text = "Guardar partido",
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
                title = { Text("Salir sin guardar") },
                text = { Text("¿Desea salir sin guardar el partido actual?") },
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
            onDismissRequest = { expanded = false }
        ) {
            Competition.entries.forEach { c ->
                DropdownMenuItem(
                    text = { Text(c.label) },
                    onClick = {
                        onSelected(c)
                        expanded = false
                    }
                )
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
            Text(
                text = title,
                color = onText.copy(alpha = 0.70f),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )

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

private fun String.onlyScoreDigits(): String {
    val cleaned = this.filter { it.isDigit() }.take(2)
    return if (cleaned.isBlank()) "0" else cleaned
}