package com.example.mvp.ui.screens.matches

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
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
import com.example.mvp.ui.theme.Draw
import com.example.mvp.ui.theme.Loss
import com.example.mvp.ui.theme.Win

enum class MatchResult(val label: String) {
    VICTORIA("Victoria"),
    EMPATE("Empate"),
    DERROTA("Derrota")
}

enum class Competition(val label: String) {
    LIGA("Liga"),
    COPA("Copa"),
    AMISTOSO("Amistoso")
}

data class Match(
    val id: Int,
    val rival: String,
    val dateText: String,
    val competition: Competition,
    val goalsFor: Int,
    val goalsAgainst: Int,
    val result: MatchResult
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchesScreen(
    modifier: Modifier = Modifier,
    matches: List<Match> = sampleMatches(),
    onBack: () -> Unit = {},
    onCreateMatch: () -> Unit = {},
    onEditMatch: (Match) -> Unit = {}
) {
    val bgTop = MaterialTheme.colorScheme.background
    val bgMid = MaterialTheme.colorScheme.surface
    val accent = MaterialTheme.colorScheme.primary
    val accent2 = MaterialTheme.colorScheme.secondary
    val onBg = MaterialTheme.colorScheme.onBackground

    var query by remember { mutableStateOf("") }
    var selectedCompetition by remember { mutableStateOf<Competition?>(null) }

    val filtered = remember(matches, query, selectedCompetition) {
        matches.filter { m ->
            val matchesQuery =
                query.isBlank() ||
                        m.rival.contains(query, ignoreCase = true) ||
                        m.competition.label.contains(query, ignoreCase = true) ||
                        m.result.label.contains(query, ignoreCase = true)

            val matchesCompetition = selectedCompetition == null || m.competition == selectedCompetition
            matchesQuery && matchesCompetition
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateMatch,
                containerColor = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Brush.horizontalGradient(listOf(accent, accent2))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir", tint = ButtonTextDark)
                }
            }
        },
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
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = onBg
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "Partidos",
                            color = onBg,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Resultados y registro de encuentros",
                            color = onBg.copy(alpha = 0.70f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = GlassBase.copy(alpha = 0.07f)
                ) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        placeholder = { Text("Buscar rival, competición...") },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent,
                            unfocusedTextColor = onBg,
                            focusedTextColor = onBg,
                            unfocusedLeadingIconColor = onBg.copy(alpha = 0.6f),
                            focusedLeadingIconColor = accent,
                            cursorColor = accent,
                            unfocusedPlaceholderColor = onBg.copy(alpha = 0.45f),
                            focusedPlaceholderColor = onBg.copy(alpha = 0.45f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                FilterChipsRow(
                    accent = accent,
                    onText = onBg,
                    selectedCompetition = selectedCompetition,
                    onSelectCompetition = { selectedCompetition = it }
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtered) { match ->
                        MatchScoreCard(
                            match = match,
                            accent = accent,
                            accent2 = accent2,
                            onText = onBg,
                            onEdit = { onEditMatch(match) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChipsRow(
    accent: Color,
    onText: Color,
    selectedCompetition: Competition?,
    onSelectCompetition: (Competition?) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        CompetitionChip(
            text = "Liga",
            selected = selectedCompetition == Competition.LIGA,
            accent = accent,
            onText = onText,
            onClick = { onSelectCompetition(if (selectedCompetition == Competition.LIGA) null else Competition.LIGA) }
        )

        CompetitionChip(
            text = "Copa",
            selected = selectedCompetition == Competition.COPA,
            accent = accent,
            onText = onText,
            onClick = { onSelectCompetition(if (selectedCompetition == Competition.COPA) null else Competition.COPA) }
        )

        CompetitionChip(
            text = "Amist.",
            selected = selectedCompetition == Competition.AMISTOSO,
            accent = accent,
            onText = onText,
            onClick = { onSelectCompetition(if (selectedCompetition == Competition.AMISTOSO) null else Competition.AMISTOSO) }
        )
    }
}

@Composable
private fun CompetitionChip(
    text: String,
    selected: Boolean,
    accent: Color,
    onText: Color,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = accent.copy(alpha = 0.18f),
            selectedLabelColor = accent,
            containerColor = GlassBase.copy(alpha = 0.06f),
            labelColor = onText.copy(alpha = 0.78f)
        ),
        border = null
    )
}

@Composable
private fun MatchScoreCard(
    match: Match,
    accent: Color,
    accent2: Color,
    onText: Color,
    onEdit: () -> Unit
) {
    val (badgeBase, badgeText) = when (match.result) {
        MatchResult.VICTORIA -> Win to Win
        MatchResult.EMPATE -> Draw to Draw
        MatchResult.DERROTA -> Loss to Loss
    }
    val badgeBg = badgeBase.copy(alpha = 0.16f)
    val badgeFg = badgeText

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = GlassBase.copy(alpha = 0.08f),
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "vs ${match.rival}",
                        color = onText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${match.dateText} · ${match.competition.label}",
                        color = onText.copy(alpha = 0.70f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = accent)
                }
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = GlassBase.copy(alpha = 0.06f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = badgeBg
                    ) {
                        Text(
                            text = match.result.label,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            color = badgeFg,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(accent.copy(alpha = 0.35f), accent2.copy(alpha = 0.30f))
                                )
                            )
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${match.goalsFor}  -  ${match.goalsAgainst}",
                            color = ButtonTextDark,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Text(
                        text = "90’",
                        color = onText.copy(alpha = 0.55f),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

private fun sampleMatches(): List<Match> = listOf(
    Match(1, "Real Betis", "12/11/2025", Competition.LIGA, 2, 1, MatchResult.VICTORIA),
    Match(2, "Villarreal", "09/11/2025", Competition.LIGA, 1, 1, MatchResult.EMPATE),
    Match(3, "Valencia", "02/11/2025", Competition.COPA, 0, 2, MatchResult.DERROTA),
    Match(4, "Levante", "28/10/2025", Competition.AMISTOSO, 3, 0, MatchResult.VICTORIA)
)