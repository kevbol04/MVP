package com.example.mvp.ui.screens.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import com.example.mvp.domain.model.Player
import com.example.mvp.domain.model.PlayerPosition
import com.example.mvp.domain.model.PlayerStatus
import com.example.mvp.domain.model.Match
import com.example.mvp.domain.model.MatchResult
import com.example.mvp.domain.model.Competition
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mvp.ui.components.BottomBarDestination
import com.example.mvp.ui.components.ProFootballBottomBar
import com.example.mvp.domain.model.Training
import com.example.mvp.domain.model.TrainingType
import com.example.mvp.ui.theme.ButtonTextDark
import com.example.mvp.ui.theme.Draw
import com.example.mvp.ui.theme.GlassBase
import com.example.mvp.ui.theme.Loss
import com.example.mvp.ui.theme.Win
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

private enum class StatsSection(val label: String) {
    OVERVIEW("Resumen"),
    TRAINING("Entrenos"),
    MATCHES("Partidos"),
    PLAYERS("Plantilla")
}

@Composable
fun StatsScreen(
    players: List<Player>,
    matches: List<Match>,
    trainings: List<Training>,
    onBack: () -> Unit,

    onGoDashboard: () -> Unit = {},
    onGoTraining: () -> Unit = {},
    onGoMatches: () -> Unit = {},
    onGoPlayers: () -> Unit = {}
) {
    val bgTop = MaterialTheme.colorScheme.background
    val bgMid = MaterialTheme.colorScheme.surface
    val accent = MaterialTheme.colorScheme.primary
    val accent2 = MaterialTheme.colorScheme.secondary
    val onBg = MaterialTheme.colorScheme.onBackground
    val danger = MaterialTheme.colorScheme.error

    val scroll = rememberScrollState()
    var selectedSection by remember { mutableStateOf(StatsSection.OVERVIEW) }

    val today = LocalDate.now()
    val currentMonth = YearMonth.from(today)

    val totalPlayers = players.size
    val titulares = players.count {
        it.lineupSlot?.substringBefore("_") in listOf("POR", "DEF", "MED", "DEL") &&
                it.status != PlayerStatus.LESIONADO
    }
    val suplentes = players.count {
        it.lineupSlot?.startsWith("BENCH_") == true &&
                it.status != PlayerStatus.LESIONADO
    }
    val lesionados = players.count { it.status == PlayerStatus.LESIONADO }
    val availablePlayers = totalPlayers - lesionados
    val availabilityRate = if (totalPlayers == 0) 0f else availablePlayers.toFloat() / totalPlayers.toFloat()
    val avgRating = if (players.isNotEmpty()) players.map { it.rating }.average() else 0.0
    val bestPlayer = players.maxByOrNull { it.rating }

    val totalMatches = matches.size
    val wins = matches.count { it.result == MatchResult.VICTORIA }
    val draws = matches.count { it.result == MatchResult.EMPATE }
    val losses = matches.count { it.result == MatchResult.DERROTA }
    val winRate = if (totalMatches == 0) 0f else wins.toFloat() / totalMatches.toFloat()
    val goalsFor = matches.sumOf { it.goalsFor }
    val goalsAgainst = matches.sumOf { it.goalsAgainst }
    val goalDifference = goalsFor - goalsAgainst
    val avgGoalsFor = if (totalMatches == 0) 0.0 else goalsFor.toDouble() / totalMatches.toDouble()
    val avgGoalsAgainst = if (totalMatches == 0) 0.0 else goalsAgainst.toDouble() / totalMatches.toDouble()
    val ligaMatches = matches.count { it.competition == Competition.LIGA }
    val copaMatches = matches.count { it.competition == Competition.COPA }
    val amistosos = matches.count { it.competition == Competition.AMISTOSO }
    val lastMatch = matches.maxByOrNull { it.date }

    val completedTrainings = trainings.filter { it.isDone }
    val pendingTrainings = trainings.filter { !it.isDone && !it.isOverdue() }
    val overdueTrainings = trainings.filter { !it.isDone && it.isOverdue() }
    val completedCount = completedTrainings.size
    val pendingCount = pendingTrainings.size
    val overdueCount = overdueTrainings.size
    val totalTrainingRecords = trainings.size
    val completionRate = if (totalTrainingRecords == 0) 0f else completedCount.toFloat() / totalTrainingRecords.toFloat()
    val totalMinutes = completedTrainings.sumOf { it.durationMin }
    val avgMinutes = if (completedTrainings.isEmpty()) 0.0 else totalMinutes.toDouble() / completedTrainings.size.toDouble()
    val minutesThisMonth = completedTrainings
        .filter { YearMonth.from(it.date) == currentMonth }
        .sumOf { it.durationMin }
    val completedThisMonth = completedTrainings.count {
        YearMonth.from(it.date) == currentMonth
    }
    val lastTraining = completedTrainings.maxByOrNull { it.date }
    val nextTraining = pendingTrainings.minByOrNull { it.date }
    val favoriteTrainingType = completedTrainings
        .groupingBy { it.type }
        .eachCount()
        .maxWithOrNull(compareBy<Map.Entry<TrainingType, Int>> { it.value }.thenBy { it.key.ordinal })
        ?.key

    val totalRecords = totalPlayers + totalMatches + totalTrainingRecords

    val recentMatches = matches
        .sortedByDescending { it.date }
        .take(5)

    val recentLosses = recentMatches.count { it.result == MatchResult.DERROTA }
    val recentWins = recentMatches.count { it.result == MatchResult.VICTORIA }
    val hasBadForm = totalMatches >= 3 && recentMatches.size >= 3 && recentLosses >= 2 && recentLosses > recentWins
    val hasGoodForm = totalMatches >= 3 && winRate >= 0.60f && recentWins >= recentLosses

    val heroTitle = when {
        totalRecords == 0 -> "Sin datos todavía"
        totalPlayers < 11 -> "Plantilla insuficiente"
        totalPlayers > 0 && (lesionados >= 3 || availabilityRate < 0.75f) -> "Revisar estado físico"
        titulares < 11 -> "Once incompleto"
        suplentes < 7 -> "Banquillo incompleto"
        overdueCount > 0 -> "Cerrar carga pendiente"
        hasBadForm -> "Mala dinámica reciente"
        hasGoodForm -> "Buen rendimiento competitivo"
        else -> "Seguimiento activo"
    }

    val heroMessage = when {
        totalRecords == 0 ->
            "Registra jugadores, partidos y entrenamientos para empezar a ver estadísticas útiles."

        totalPlayers < 11 ->
            "Tienes $totalPlayers jugador${if (totalPlayers == 1) "" else "es"}. Necesitas al menos 11 para formar un once completo."

        totalPlayers > 0 && (lesionados >= 3 || availabilityRate < 0.75f) ->
            "Hay $lesionados jugador${if (lesionados == 1) "" else "es"} lesionado${if (lesionados == 1) "" else "s"}. La prioridad es recuperar disponibilidad en la plantilla."

        titulares < 11 ->
            "El once tiene $titulares/11 jugadores colocados. Completa la alineación antes de analizar el rendimiento del equipo."

        suplentes < 7 ->
            "El banquillo tiene $suplentes/7 jugadores. Puedes completar la convocatoria para tener más alternativas."

        overdueCount > 0 ->
            "Tienes $overdueCount entrenamiento${if (overdueCount == 1) "" else "s"} atrasado${if (overdueCount == 1) "" else "s"}. La prioridad ahora es cerrar la carga pendiente."

        hasBadForm ->
            "En los últimos partidos hay más derrotas que victorias. Revisa resultados, goles encajados y carga de entrenamientos."

        hasGoodForm ->
            "El rendimiento competitivo es positivo: $wins victoria${if (wins == 1) "" else "s"}, $draws empate${if (draws == 1) "" else "s"} y $losses derrota${if (losses == 1) "" else "s"}."

        else ->
            "Balance actual: $wins victorias, $draws empates y $losses derrotas, con ${(completionRate * 100).roundToInt()}% de cumplimiento en entrenos."
    }

    val heroPrimaryChip = when {
        totalPlayers < 11 -> "$totalPlayers jugadores"
        titulares < 11 -> "Once $titulares/11"
        totalMatches == 0 -> "Sin partidos"
        else -> "${(winRate * 100).roundToInt()}% victorias"
    }

    val heroSecondaryChip = when {
        totalPlayers > 0 && lesionados > 0 -> "$lesionados lesionado${if (lesionados == 1) "" else "s"}"
        suplentes < 7 -> "Banquillo $suplentes/7"
        totalTrainingRecords == 0 -> "Sin entrenos"
        else -> "${(completionRate * 100).roundToInt()}% entrenos"
    }

    val heroThirdChip = when {
        overdueCount > 0 -> "$overdueCount atrasado${if (overdueCount == 1) "" else "s"}"
        totalPlayers > 0 -> "$availablePlayers disponible${if (availablePlayers == 1) "" else "s"}"
        else -> "Sin datos"
    }

    val bottomBarHeight = 96.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(bgTop, bgMid, bgTop)))
    ) {
        Box(
            modifier = Modifier
                .size(220.dp)
                .offset(x = 60.dp, y = (-40).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            accent.copy(alpha = 0.28f),
                            Color.Transparent
                        )
                    )
                )
                .align(Alignment.TopEnd)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = bottomBarHeight + 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Header(onBack = onBack, onText = onBg)

            GlobalHeroCard(
                title = heroTitle,
                message = heroMessage,
                primaryChip = heroPrimaryChip,
                secondaryChip = heroSecondaryChip,
                thirdChip = heroThirdChip,
                accent = accent,
                accent2 = accent2,
                danger = danger,
                onText = onBg
            )

            SectionSelector(
                selected = selectedSection,
                onSelected = { selectedSection = it },
                accent = accent,
                accent2 = accent2,
                onText = onBg
            )

            when (selectedSection) {
                StatsSection.OVERVIEW -> OverviewSection(
                    totalPlayers = totalPlayers,
                    totalMatches = totalMatches,
                    completedCount = completedCount,
                    totalMinutes = totalMinutes,
                    winRate = winRate,
                    goalDifference = goalDifference,
                    completionRate = completionRate,
                    overdueCount = overdueCount,
                    avgRating = avgRating,
                    bestPlayer = bestPlayer,
                    lastMatch = lastMatch,
                    lastTraining = lastTraining,
                    nextTraining = nextTraining,
                    accent = accent,
                    accent2 = accent2,
                    onText = onBg,
                    danger = danger
                )

                StatsSection.TRAINING -> TrainingStatsSection(
                    trainings = trainings,
                    completedTrainings = completedTrainings,
                    pendingCount = pendingCount,
                    overdueCount = overdueCount,
                    completedCount = completedCount,
                    totalMinutes = totalMinutes,
                    avgMinutes = avgMinutes,
                    completedThisMonth = completedThisMonth,
                    minutesThisMonth = minutesThisMonth,
                    favoriteTrainingType = favoriteTrainingType,
                    completionRate = completionRate,
                    accent = accent,
                    accent2 = accent2,
                    onText = onBg,
                    danger = danger
                )

                StatsSection.MATCHES -> MatchStatsSection(
                    totalMatches = totalMatches,
                    wins = wins,
                    draws = draws,
                    losses = losses,
                    winRate = winRate,
                    goalsFor = goalsFor,
                    goalsAgainst = goalsAgainst,
                    goalDifference = goalDifference,
                    avgGoalsFor = avgGoalsFor,
                    avgGoalsAgainst = avgGoalsAgainst,
                    ligaMatches = ligaMatches,
                    copaMatches = copaMatches,
                    amistosos = amistosos,
                    matches = matches,
                    accent = accent,
                    accent2 = accent2,
                    onText = onBg
                )

                StatsSection.PLAYERS -> PlayerStatsSection(
                    players = players,
                    totalPlayers = totalPlayers,
                    titulares = titulares,
                    suplentes = suplentes,
                    lesionados = lesionados,
                    avgRating = avgRating,
                    bestPlayer = bestPlayer,
                    availabilityRate = availabilityRate,
                    accent = accent,
                    accent2 = accent2,
                    onText = onBg
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        ProFootballBottomBar(
            selected = BottomBarDestination.Stats,
            onSelect = { destination ->
                when (destination) {
                    BottomBarDestination.Training -> onGoTraining()
                    BottomBarDestination.Players -> onGoPlayers()
                    BottomBarDestination.Dashboard -> onGoDashboard()
                    BottomBarDestination.Matches -> onGoMatches()
                    BottomBarDestination.Stats -> Unit
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 12.dp)
        )
    }
}

@Composable
private fun Header(
    onBack: () -> Unit,
    onText: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = onText
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Estadísticas",
                color = onText,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Análisis real del equipo",
                color = onText.copy(alpha = 0.65f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun GlobalHeroCard(
    title: String,
    message: String,
    primaryChip: String,
    secondaryChip: String,
    thirdChip: String,
    accent: Color,
    accent2: Color,
    danger: Color,
    onText: Color
) {
    val thirdIsWarning = thirdChip.contains("atrasado", ignoreCase = true)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = GlassBase.copy(alpha = 0.08f),
        shape = RoundedCornerShape(30.dp),
        tonalElevation = 2.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            accent.copy(alpha = 0.20f),
                            accent2.copy(alpha = 0.12f),
                            GlassBase.copy(alpha = 0.08f)
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(170.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 42.dp, y = (-42).dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                accent.copy(alpha = 0.16f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = GlassBase.copy(alpha = 0.08f)
                ) {
                    Text(
                        text = "Prioridad actual",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        color = onText.copy(alpha = 0.82f),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = title,
                    color = onText,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = message,
                    color = onText.copy(alpha = 0.76f),
                    style = MaterialTheme.typography.bodyMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HeroInfoChip(
                        text = primaryChip,
                        accent = accent,
                        onText = onText,
                        modifier = Modifier.weight(1f)
                    )
                    HeroInfoChip(
                        text = secondaryChip,
                        accent = accent2,
                        onText = onText,
                        modifier = Modifier.weight(1f)
                    )
                    HeroInfoChip(
                        text = thirdChip,
                        accent = if (thirdIsWarning) danger else accent,
                        onText = onText,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionSelector(
    selected: StatsSection,
    onSelected: (StatsSection) -> Unit,
    accent: Color,
    accent2: Color,
    onText: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = GlassBase.copy(alpha = 0.06f),
        shape = RoundedCornerShape(22.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            StatsSection.entries.forEach { section ->
                SectionChip(
                    modifier = Modifier.weight(1f),
                    text = section.label,
                    selected = selected == section,
                    accent = accent,
                    accent2 = accent2,
                    onText = onText,
                    onClick = { onSelected(section) }
                )
            }
        }
    }
}

@Composable
private fun SectionChip(
    modifier: Modifier,
    text: String,
    selected: Boolean,
    accent: Color,
    accent2: Color,
    onText: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(42.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (selected) GlassBase.copy(alpha = 0.10f) else Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (selected) {
                        Brush.horizontalGradient(listOf(accent.copy(alpha = 0.30f), accent2.copy(alpha = 0.22f)))
                    } else {
                        Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
                    }
                )
                .padding(horizontal = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = if (selected) ButtonTextDark else onText.copy(alpha = 0.72f),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun OverviewSection(
    totalPlayers: Int,
    totalMatches: Int,
    completedCount: Int,
    totalMinutes: Int,
    winRate: Float,
    goalDifference: Int,
    completionRate: Float,
    overdueCount: Int,
    avgRating: Double,
    bestPlayer: Player?,
    lastMatch: Match?,
    lastTraining: Training?,
    nextTraining: Training?,
    accent: Color,
    accent2: Color,
    onText: Color,
    danger: Color
) {
    KpiGrid(
        first = KpiData("Jugadores", totalPlayers.toString(), if (totalPlayers == 0) "Sin plantilla" else "Plantilla"),
        second = KpiData("Partidos", totalMatches.toString(), "${(winRate * 100).roundToInt()}% victorias"),
        third = KpiData("Entrenos", completedCount.toString(), "$totalMinutes min hechos"),
        fourth = KpiData("Media OVR", if (totalPlayers == 0) "--" else formatDecimal(avgRating), bestPlayer?.name ?: "Sin datos"),
        accent = accent,
        accent2 = accent2,
        onText = onText
    )

    GlassCard(title = "Partidos & entrenamientos", onText = onText) {
        StatBarRow(
            label = "Rendimiento competitivo",
            value = (winRate * 100).roundToInt(),
            total = 100,
            color = when {
                winRate >= 0.60f -> Win
                winRate >= 0.35f -> Draw
                else -> Loss
            },
            onText = onText,
            suffix = "%"
        )

        StatBarRow(
            label = "Cumplimiento de entrenos",
            value = (completionRate * 100).roundToInt(),
            total = 100,
            color = if (overdueCount > 0) danger else accent,
            onText = onText,
            suffix = "%"
        )

        StatRow(
            label = "Diferencia de goles",
            value = if (goalDifference > 0) "+$goalDifference" else goalDifference.toString(),
            valueColor = when {
                goalDifference > 0 -> Win
                goalDifference < 0 -> Loss
                else -> Draw
            },
            onText = onText
        )

        StatRow(
            label = "Entrenos atrasados",
            value = overdueCount.toString(),
            valueColor = if (overdueCount > 0) danger else Win,
            onText = onText
        )
    }

    GlassCard(title = "Últimos movimientos", onText = onText) {
        RecentBlock(title = "Último partido", accent = accent, accent2 = accent2, onText = onText) {
            if (lastMatch == null) {
                EmptyLine(text = "Aún no hay partidos registrados.", onText = onText)
            } else {
                Text(
                    text = "${lastMatch.competition.label} · vs ${lastMatch.rival}",
                    color = onText,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = lastMatch.dateText,
                    color = onText.copy(alpha = 0.65f),
                    style = MaterialTheme.typography.bodyMedium
                )
                ScorePill(
                    text = "${lastMatch.goalsFor} - ${lastMatch.goalsAgainst} · ${lastMatch.result.label}",
                    color = resultColor(lastMatch.result)
                )
            }
        }

        RecentBlock(title = "Último entrenamiento hecho", accent = accent, accent2 = accent2, onText = onText) {
            if (lastTraining == null) {
                EmptyLine(text = "Aún no hay entrenamientos completados.", onText = onText)
            } else {
                Text(
                    text = "${lastTraining.type.label} · ${lastTraining.name}",
                    color = onText,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${lastTraining.dateText} · ${lastTraining.durationMin} min",
                    color = onText.copy(alpha = 0.65f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        RecentBlock(title = "Próximo entrenamiento", accent = accent, accent2 = accent2, onText = onText) {
            if (nextTraining == null) {
                EmptyLine(text = "No tienes entrenamientos próximos pendientes.", onText = onText)
            } else {
                Text(
                    text = nextTraining.name,
                    color = onText,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${nextTraining.dateText} · ${nextTraining.durationMin} min · ${nextTraining.type.label}",
                    color = onText.copy(alpha = 0.65f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun TrainingStatsSection(
    trainings: List<Training>,
    completedTrainings: List<Training>,
    pendingCount: Int,
    overdueCount: Int,
    completedCount: Int,
    totalMinutes: Int,
    avgMinutes: Double,
    completedThisMonth: Int,
    minutesThisMonth: Int,
    favoriteTrainingType: TrainingType?,
    completionRate: Float,
    accent: Color,
    accent2: Color,
    onText: Color,
    danger: Color
) {
    KpiGrid(
        first = KpiData("Hechos", completedCount.toString(), "Completados"),
        second = KpiData("Pendientes", pendingCount.toString(), "Por hacer"),
        third = KpiData("Atrasados", overdueCount.toString(), "Revisar"),
        fourth = KpiData("Minutos", totalMinutes.toString(), "Acumulados"),
        accent = accent,
        accent2 = accent2,
        onText = onText
    )

    GlassCard(title = "Carga y cumplimiento", onText = onText) {
        StatBarRow(
            label = "Cumplimiento total",
            value = (completionRate * 100).roundToInt(),
            total = 100,
            color = if (overdueCount > 0) danger else accent,
            onText = onText,
            suffix = "%"
        )

        StatRow(label = "Sesiones hechas", value = completedCount.toString(), onText = onText)
        StatRow(label = "Sesiones pendientes", value = pendingCount.toString(), onText = onText)
        StatRow(
            label = "Sesiones atrasadas",
            value = overdueCount.toString(),
            valueColor = if (overdueCount > 0) danger else Win,
            onText = onText
        )
        StatRow(label = "Media por sesión", value = if (completedCount == 0) "--" else "${formatWhole(avgMinutes)} min", onText = onText)
        StatRow(label = "Tipo más trabajado", value = favoriteTrainingType?.label ?: "--", onText = onText)
    }

    GlassCard(title = "Este mes", onText = onText) {
        StatRow(label = "Entrenos completados", value = completedThisMonth.toString(), onText = onText)
        StatRow(label = "Minutos completados", value = "$minutesThisMonth min", onText = onText)
        val monthlyMessage = when {
            completedThisMonth == 0 -> "Todavía no hay entrenamientos completados este mes."
            completedThisMonth < 4 -> "Hay actividad, pero aún es una carga baja."
            completedThisMonth < 8 -> "Buen ritmo mensual."
            else -> "Carga mensual alta y constante."
        }
        EmptyLine(text = monthlyMessage, onText = onText)
    }

    GlassCard(title = "Distribución por tipo", onText = onText) {
        TrainingTypeDistribution(
            trainings = completedTrainings,
            accent = accent,
            accent2 = accent2,
            onText = onText
        )
    }
}

@Composable
private fun MatchStatsSection(
    totalMatches: Int,
    wins: Int,
    draws: Int,
    losses: Int,
    winRate: Float,
    goalsFor: Int,
    goalsAgainst: Int,
    goalDifference: Int,
    avgGoalsFor: Double,
    avgGoalsAgainst: Double,
    ligaMatches: Int,
    copaMatches: Int,
    amistosos: Int,
    matches: List<Match>,
    accent: Color,
    accent2: Color,
    onText: Color
) {
    KpiGrid(
        first = KpiData("Partidos", totalMatches.toString(), "Jugados"),
        second = KpiData("Victorias", wins.toString(), "${(winRate * 100).roundToInt()}%"),
        third = KpiData("Goles +", goalsFor.toString(), "A favor"),
        fourth = KpiData("DG", if (goalDifference > 0) "+$goalDifference" else goalDifference.toString(), "Diferencia"),
        accent = accent,
        accent2 = accent2,
        onText = onText
    )

    GlassCard(title = "Balance competitivo", onText = onText) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ResultPill(modifier = Modifier.weight(1f), title = "Victorias", value = wins, color = Win, onText = onText)
            ResultPill(modifier = Modifier.weight(1f), title = "Empates", value = draws, color = Draw, onText = onText)
            ResultPill(modifier = Modifier.weight(1f), title = "Derrotas", value = losses, color = Loss, onText = onText)
        }

        Spacer(modifier = Modifier.height(6.dp))

        StatBarRow(label = "Victorias", value = wins, total = totalMatches, color = Win, onText = onText)
        StatBarRow(label = "Empates", value = draws, total = totalMatches, color = Draw, onText = onText)
        StatBarRow(label = "Derrotas", value = losses, total = totalMatches, color = Loss, onText = onText)
    }

    GlassCard(title = "Goles", onText = onText) {
        StatRow(label = "Goles a favor", value = goalsFor.toString(), onText = onText)
        StatRow(label = "Goles en contra", value = goalsAgainst.toString(), onText = onText)
        StatRow(
            label = "Diferencia de goles",
            value = if (goalDifference > 0) "+$goalDifference" else goalDifference.toString(),
            valueColor = when {
                goalDifference > 0 -> Win
                goalDifference < 0 -> Loss
                else -> Draw
            },
            onText = onText
        )
        StatRow(label = "Goles a favor por partido", value = if (totalMatches == 0) "--" else formatDecimal(avgGoalsFor), onText = onText)
        StatRow(label = "Goles en contra por partido", value = if (totalMatches == 0) "--" else formatDecimal(avgGoalsAgainst), onText = onText)
    }

    GlassCard(title = "Competiciones", onText = onText) {
        StatBarRow(label = "Liga", value = ligaMatches, total = totalMatches, color = Win, onText = onText)
        StatBarRow(label = "Copa", value = copaMatches, total = totalMatches, color = accent, onText = onText)
        StatBarRow(label = "Amistosos", value = amistosos, total = totalMatches, color = accent2, onText = onText)
    }

    GlassCard(title = "Forma reciente", onText = onText) {
        val recent = matches
            .sortedByDescending { it.date }
            .take(5)

        if (recent.isEmpty()) {
            EmptyLine(text = "Aún no hay partidos para analizar la forma reciente.", onText = onText)
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                recent.forEach { match ->
                    ResultLetterPill(result = match.result)
                }
            }
            EmptyLine(text = "Últimos ${recent.size} partidos.", onText = onText)
        }
    }
}

@Composable
private fun PlayerStatsSection(
    players: List<Player>,
    totalPlayers: Int,
    titulares: Int,
    suplentes: Int,
    lesionados: Int,
    avgRating: Double,
    bestPlayer: Player?,
    availabilityRate: Float,
    accent: Color,
    accent2: Color,
    onText: Color
) {
    KpiGrid(
        first = KpiData("Plantilla", totalPlayers.toString(), "Jugadores"),
        second = KpiData("Disponibles", (totalPlayers - lesionados).toString(), "${(availabilityRate * 100).roundToInt()}%"),
        third = KpiData("Media OVR", if (totalPlayers == 0) "--" else formatDecimal(avgRating), "Nivel"),
        fourth = KpiData("Lesionados", lesionados.toString(), "Bajas"),
        accent = accent,
        accent2 = accent2,
        onText = onText
    )

    GlassCard(title = "Jugador destacado", onText = onText) {
        BestPlayerBlock(bestPlayer = bestPlayer, accent = accent, accent2 = accent2, onText = onText)
    }

    GlassCard(title = "Distribución por posición", onText = onText) {
        PlayerPosition.entries.forEach { position ->
            StatBarRow(
                label = position.label,
                value = players.count { it.position == position },
                total = totalPlayers,
                color = when (position) {
                    PlayerPosition.POR -> accent2
                    PlayerPosition.DEF -> Win
                    PlayerPosition.MED -> accent
                    PlayerPosition.DEL -> Draw
                },
                onText = onText
            )
        }
    }
}

private data class KpiData(
    val title: String,
    val value: String,
    val subtitle: String
)

@Composable
private fun KpiGrid(
    first: KpiData,
    second: KpiData,
    third: KpiData,
    fourth: KpiData,
    accent: Color,
    accent2: Color,
    onText: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        KpiCard(modifier = Modifier.weight(1f), data = first, accent = accent, accent2 = accent2, onText = onText)
        KpiCard(modifier = Modifier.weight(1f), data = second, accent = accent, accent2 = accent2, onText = onText)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        KpiCard(modifier = Modifier.weight(1f), data = third, accent = accent, accent2 = accent2, onText = onText)
        KpiCard(modifier = Modifier.weight(1f), data = fourth, accent = accent, accent2 = accent2, onText = onText)
    }
}

@Composable
private fun KpiCard(
    modifier: Modifier = Modifier,
    data: KpiData,
    accent: Color,
    accent2: Color,
    onText: Color
) {
    Surface(
        modifier = modifier,
        color = GlassBase.copy(alpha = 0.06f),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = data.title,
                color = onText.copy(alpha = 0.70f),
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = data.value,
                color = onText,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .height(6.dp)
                        .width(28.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Brush.horizontalGradient(listOf(accent, accent2)))
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = data.subtitle,
                    color = onText.copy(alpha = 0.65f),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun GlassCard(
    title: String,
    onText: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = GlassBase.copy(alpha = 0.06f),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                color = onText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            content()
        }
    }
}

@Composable
private fun HeroInfoChip(
    text: String,
    accent: Color,
    onText: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = accent.copy(alpha = 0.14f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = onText,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ResultPill(
    modifier: Modifier = Modifier,
    title: String,
    value: Int,
    color: Color,
    onText: Color
) {
    Surface(
        modifier = modifier,
        color = GlassBase.copy(alpha = 0.06f),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = onText.copy(alpha = 0.70f),
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value.toString(),
                color = color,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun StatBarRow(
    label: String,
    value: Int,
    total: Int,
    color: Color,
    onText: Color,
    suffix: String = ""
) {
    val safeTotal = total.coerceAtLeast(1)
    val pct = (value.toFloat() / safeTotal.toFloat()).coerceIn(0f, 1f)

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = onText.copy(alpha = 0.75f),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            val displayValue = when {
                total == 0 -> "0$suffix"
                suffix.isNotBlank() -> "$value$suffix"
                else -> "$value · ${(pct * 100).roundToInt()}%"
            }

            Text(
                text = displayValue,
                color = onText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(GlassBase.copy(alpha = 0.08f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(pct)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(999.dp))
                    .background(color.copy(alpha = 0.90f))
            )
        }
    }
}

@Composable
private fun StatRow(
    label: String,
    value: String,
    onText: Color,
    valueColor: Color = onText
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = onText.copy(alpha = 0.72f),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = value,
            color = valueColor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

@Composable
private fun BestPlayerBlock(
    bestPlayer: Player?,
    accent: Color,
    accent2: Color,
    onText: Color
) {
    Surface(
        color = GlassBase.copy(alpha = 0.06f),
        shape = RoundedCornerShape(18.dp)
    ) {
        if (bestPlayer == null) {
            Text(
                text = "Aún no hay jugadores registrados.",
                modifier = Modifier.padding(14.dp),
                color = onText.copy(alpha = 0.72f),
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Brush.horizontalGradient(listOf(accent, accent2))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = bestPlayer.name.take(1).uppercase(),
                        color = ButtonTextDark,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Jugador mejor valorado",
                        color = onText.copy(alpha = 0.62f),
                        style = MaterialTheme.typography.labelMedium
                    )

                    Text(
                        text = bestPlayer.name,
                        color = onText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "${bestPlayer.position.label} · #${bestPlayer.number} · ${bestPlayer.age} años",
                        color = onText.copy(alpha = 0.65f),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    color = accent.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = bestPlayer.rating.toString(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        color = accent,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun TrainingTypeDistribution(
    trainings: List<Training>,
    accent: Color,
    accent2: Color,
    onText: Color
) {
    if (trainings.isEmpty()) {
        EmptyLine(text = "Aún no hay datos para analizar la carga por tipo.", onText = onText)
        return
    }

    TrainingType.entries.forEach { type ->
        val count = trainings.count { it.type == type }
        val color = when (type) {
            TrainingType.FUERZA -> accent
            TrainingType.RESISTENCIA -> accent2
            TrainingType.VELOCIDAD -> Win
            TrainingType.TECNICA -> Draw
            TrainingType.RECUPERACION -> Loss
        }

        StatBarRow(label = type.label, value = count, total = trainings.size, color = color, onText = onText)
    }
}

@Composable
private fun RecentBlock(
    title: String,
    accent: Color,
    accent2: Color,
    onText: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        color = GlassBase.copy(alpha = 0.06f),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .width(32.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Brush.horizontalGradient(listOf(accent, accent2)))
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = title,
                    color = onText,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }

            content()
        }
    }
}

@Composable
private fun EmptyLine(
    text: String,
    onText: Color
) {
    Text(
        text = text,
        color = onText.copy(alpha = 0.72f),
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
private fun ScorePill(
    text: String,
    color: Color
) {
    Surface(
        color = color.copy(alpha = 0.16f),
        shape = RoundedCornerShape(14.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            color = color,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ResultLetterPill(result: MatchResult) {
    val color = resultColor(result)
    val text = when (result) {
        MatchResult.VICTORIA -> "V"
        MatchResult.EMPATE -> "E"
        MatchResult.DERROTA -> "D"
    }

    Surface(
        modifier = Modifier.size(36.dp),
        color = color.copy(alpha = 0.18f),
        shape = CircleShape
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                color = color,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun resultColor(result: MatchResult): Color {
    return when (result) {
        MatchResult.VICTORIA -> Win
        MatchResult.EMPATE -> Draw
        MatchResult.DERROTA -> Loss
    }
}

private fun Training.isOverdue(): Boolean {
    return !isDone && date.isBefore(LocalDate.now())
}

private fun parseDateOrNull(dateText: String): LocalDate? {
    return try {
        LocalDate.parse(dateText, DateTimeFormatter.ofPattern("dd/MM/uuuu"))
    } catch (_: Exception) {
        null
    }
}


private fun formatDecimal(value: Double): String {
    return String.format(Locale.getDefault(), "%.1f", value)
}

private fun formatWhole(value: Double): String {
    return String.format(Locale.getDefault(), "%.0f", value)
}