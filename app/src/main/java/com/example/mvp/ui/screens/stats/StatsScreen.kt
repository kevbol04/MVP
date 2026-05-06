package com.example.mvp.ui.screens.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import com.example.mvp.ui.screens.matches.Competition
import com.example.mvp.ui.screens.matches.Match
import com.example.mvp.ui.screens.matches.MatchResult
import com.example.mvp.ui.screens.players.Player
import com.example.mvp.ui.screens.players.PlayerStatus
import com.example.mvp.ui.screens.training.Training
import com.example.mvp.ui.screens.training.TrainingType
import com.example.mvp.ui.theme.ButtonTextDark
import com.example.mvp.ui.theme.Draw
import com.example.mvp.ui.theme.GlassBase
import com.example.mvp.ui.theme.Loss
import com.example.mvp.ui.theme.Win
import java.util.Locale

private enum class BottomTab {
    Training, Matches, Players, Stats
}

@Composable
fun StatsScreen(
    players: List<Player>,
    matches: List<Match>,
    trainings: List<Training>,
    onBack: () -> Unit,

    onGoTraining: () -> Unit = {},
    onGoMatches: () -> Unit = {},
    onGoPlayers: () -> Unit = {}
) {
    val bgTop = MaterialTheme.colorScheme.background
    val bgMid = MaterialTheme.colorScheme.surface
    val accent = MaterialTheme.colorScheme.primary
    val accent2 = MaterialTheme.colorScheme.secondary
    val onBg = MaterialTheme.colorScheme.onBackground

    val scroll = rememberScrollState()

    val totalPlayers = players.size
    val titulares = players.count { it.status == PlayerStatus.TITULAR }
    val suplentes = players.count { it.status == PlayerStatus.SUPLENTE }
    val lesionados = players.count { it.status == PlayerStatus.LESIONADO }

    val avgRating = if (players.isNotEmpty()) {
        players.map { it.rating }.average()
    } else {
        0.0
    }

    val bestPlayer = players.maxByOrNull { it.rating }

    val totalMatches = matches.size
    val wins = matches.count { it.result == MatchResult.VICTORIA }
    val draws = matches.count { it.result == MatchResult.EMPATE }
    val losses = matches.count { it.result == MatchResult.DERROTA }

    val winRate = if (totalMatches > 0) {
        wins.toFloat() / totalMatches.toFloat()
    } else {
        0f
    }

    val winRateText = if (totalMatches == 0) {
        "--"
    } else {
        "${(winRate * 100).toInt()}%"
    }

    val goalsFor = matches.sumOf { it.goalsFor }
    val goalsAgainst = matches.sumOf { it.goalsAgainst }
    val goalDifference = goalsFor - goalsAgainst

    val ligaMatches = matches.count { it.competition == Competition.LIGA }
    val copaMatches = matches.count { it.competition == Competition.COPA }
    val amistosos = matches.count { it.competition == Competition.AMISTOSO }

    val lastMatch = matches.maxByOrNull { it.id }

    val totalTrainings = trainings.size
    val totalMinutes = trainings.sumOf { it.durationMin }
    val avgMinutes = if (trainings.isNotEmpty()) {
        totalMinutes.toDouble() / trainings.size
    } else {
        0.0
    }

    val lastTraining = trainings.maxByOrNull { it.id }

    val favoriteTrainingType = trainings
        .groupingBy { it.type }
        .eachCount()
        .maxByOrNull { it.value }
        ?.key

    val totalRecords = totalPlayers + totalMatches + totalTrainings

    val performanceTitle = when {
        totalMatches == 0 -> "Sin partidos registrados"
        winRate >= 0.70f -> "Rendimiento excelente"
        winRate >= 0.50f -> "Buen rendimiento"
        winRate >= 0.30f -> "Rendimiento mejorable"
        else -> "Necesitas reaccionar"
    }

    val performanceMessage = when {
        totalRecords == 0 ->
            "Aún no hay datos suficientes. Añade jugadores, partidos y entrenamientos para generar estadísticas reales."

        totalMatches == 0 ->
            "Ya tienes datos de plantilla o entrenamientos. Registra partidos para medir el rendimiento competitivo."

        winRate >= 0.70f ->
            "El equipo está compitiendo a gran nivel. Mantén la carga de entrenamientos y sigue registrando resultados."

        winRate >= 0.50f ->
            "El equipo está en una línea positiva. Hay margen para mejorar, pero la base es buena."

        winRate >= 0.30f ->
            "Hay resultados positivos, pero necesitas más regularidad. Revisa entrenamientos y rendimiento defensivo."

        else ->
            "Los resultados no acompañan. Prioriza entrenamientos, ajustes tácticos y control de goles encajados."
    }

    var selectedTab by remember { mutableStateOf(BottomTab.Stats) }
    val bottomBarHeight = 78.dp

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
            Header(
                onBack = onBack,
                onText = onBg
            )

            PerformanceHeroCard(
                title = performanceTitle,
                message = performanceMessage,
                winRateText = winRateText,
                winRate = winRate,
                record = "$wins V · $draws E · $losses D",
                goalDifference = goalDifference,
                totalMatches = totalMatches,
                accent = accent,
                accent2 = accent2,
                onText = onBg
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KpiCard(
                    modifier = Modifier.weight(1f),
                    title = "Jugadores",
                    value = totalPlayers.toString(),
                    subtitle = "$titulares titulares",
                    accent = accent,
                    accent2 = accent2,
                    onText = onBg
                )

                KpiCard(
                    modifier = Modifier.weight(1f),
                    title = "Partidos",
                    value = totalMatches.toString(),
                    subtitle = "$wins victorias",
                    accent = accent,
                    accent2 = accent2,
                    onText = onBg
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KpiCard(
                    modifier = Modifier.weight(1f),
                    title = "Entrenos",
                    value = totalTrainings.toString(),
                    subtitle = "$totalMinutes min",
                    accent = accent,
                    accent2 = accent2,
                    onText = onBg
                )

                KpiCard(
                    modifier = Modifier.weight(1f),
                    title = "Media OVR",
                    value = if (totalPlayers == 0) "--" else formatDecimal(avgRating),
                    subtitle = "Plantilla",
                    accent = accent,
                    accent2 = accent2,
                    onText = onBg
                )
            }

            GlassCard(
                title = "Resumen competitivo",
                onText = onBg
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ResultPill(
                        modifier = Modifier.weight(1f),
                        title = "Victorias",
                        value = wins,
                        color = Win,
                        onText = onBg
                    )

                    ResultPill(
                        modifier = Modifier.weight(1f),
                        title = "Empates",
                        value = draws,
                        color = Draw,
                        onText = onBg
                    )

                    ResultPill(
                        modifier = Modifier.weight(1f),
                        title = "Derrotas",
                        value = losses,
                        color = Loss,
                        onText = onBg
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                StatBarRow(
                    label = "Victorias",
                    value = wins,
                    total = totalMatches,
                    color = Win,
                    onText = onBg
                )

                StatBarRow(
                    label = "Empates",
                    value = draws,
                    total = totalMatches,
                    color = Draw,
                    onText = onBg
                )

                StatBarRow(
                    label = "Derrotas",
                    value = losses,
                    total = totalMatches,
                    color = Loss,
                    onText = onBg
                )

                Spacer(modifier = Modifier.height(8.dp))

                StatRow(
                    label = "Goles a favor",
                    value = goalsFor.toString(),
                    onText = onBg
                )

                StatRow(
                    label = "Goles en contra",
                    value = goalsAgainst.toString(),
                    onText = onBg
                )

                StatRow(
                    label = "Diferencia de goles",
                    value = if (goalDifference > 0) "+$goalDifference" else goalDifference.toString(),
                    valueColor = when {
                        goalDifference > 0 -> Win
                        goalDifference < 0 -> Loss
                        else -> Draw
                    },
                    onText = onBg
                )
            }

            GlassCard(
                title = "Partidos por competición",
                onText = onBg
            ) {
                StatBarRow(
                    label = "Liga",
                    value = ligaMatches,
                    total = totalMatches,
                    color = Win,
                    onText = onBg
                )

                StatBarRow(
                    label = "Copa",
                    value = copaMatches,
                    total = totalMatches,
                    color = accent,
                    onText = onBg
                )

                StatBarRow(
                    label = "Amistosos",
                    value = amistosos,
                    total = totalMatches,
                    color = accent2,
                    onText = onBg
                )
            }

            GlassCard(
                title = "Estado de la plantilla",
                onText = onBg
            ) {
                StatBarRow(
                    label = "Titulares",
                    value = titulares,
                    total = totalPlayers,
                    color = Win,
                    onText = onBg
                )

                StatBarRow(
                    label = "Suplentes",
                    value = suplentes,
                    total = totalPlayers,
                    color = accent,
                    onText = onBg
                )

                StatBarRow(
                    label = "Lesionados",
                    value = lesionados,
                    total = totalPlayers,
                    color = Loss,
                    onText = onBg
                )

                Spacer(modifier = Modifier.height(6.dp))

                BestPlayerBlock(
                    bestPlayer = bestPlayer,
                    accent = accent,
                    accent2 = accent2,
                    onText = onBg
                )
            }

            GlassCard(
                title = "Carga de entrenamiento",
                onText = onBg
            ) {
                StatRow(
                    label = "Sesiones registradas",
                    value = totalTrainings.toString(),
                    onText = onBg
                )

                StatRow(
                    label = "Minutos totales",
                    value = totalMinutes.toString(),
                    onText = onBg
                )

                StatRow(
                    label = "Media por sesión",
                    value = if (totalTrainings == 0) "--" else "${formatWhole(avgMinutes)} min",
                    onText = onBg
                )

                StatRow(
                    label = "Tipo más trabajado",
                    value = favoriteTrainingType?.label ?: "--",
                    onText = onBg
                )

                TrainingTypeDistribution(
                    trainings = trainings,
                    accent = accent,
                    accent2 = accent2,
                    onText = onBg
                )
            }

            GlassCard(
                title = "Últimos registros",
                onText = onBg
            ) {
                RecentBlock(
                    title = "Último partido",
                    accent = accent,
                    accent2 = accent2,
                    onText = onBg
                ) {
                    if (lastMatch == null) {
                        EmptyLine(
                            text = "Aún no hay partidos registrados.",
                            onText = onBg
                        )
                    } else {
                        Text(
                            text = "${lastMatch.competition.label} · vs ${lastMatch.rival}",
                            color = onBg,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = lastMatch.dateText,
                            color = onBg.copy(alpha = 0.65f),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        ScorePill(
                            text = "${lastMatch.goalsFor} - ${lastMatch.goalsAgainst} · ${lastMatch.result.label}",
                            color = resultColor(lastMatch.result)
                        )
                    }
                }

                RecentBlock(
                    title = "Último entrenamiento",
                    accent = accent,
                    accent2 = accent2,
                    onText = onBg
                ) {
                    if (lastTraining == null) {
                        EmptyLine(
                            text = "Aún no hay entrenamientos registrados.",
                            onText = onBg
                        )
                    } else {
                        Text(
                            text = "${lastTraining.type.label} · ${lastTraining.name}",
                            color = onBg,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = lastTraining.dateText,
                            color = onBg.copy(alpha = 0.65f),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        ScorePill(
                            text = "${lastTraining.durationMin} min",
                            color = accent
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        BottomMenuBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            accent = accent,
            accent2 = accent2,
            onText = onBg,
            selected = selectedTab,
            onSelect = { tab ->
                when (tab) {
                    BottomTab.Stats -> {
                        selectedTab = BottomTab.Stats
                    }

                    BottomTab.Training -> {
                        selectedTab = BottomTab.Training
                        onGoTraining()
                    }

                    BottomTab.Matches -> {
                        selectedTab = BottomTab.Matches
                        onGoMatches()
                    }

                    BottomTab.Players -> {
                        selectedTab = BottomTab.Players
                        onGoPlayers()
                    }
                }
            }
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
                text = "Análisis completo del equipo",
                color = onText.copy(alpha = 0.65f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun PerformanceHeroCard(
    title: String,
    message: String,
    winRateText: String,
    winRate: Float,
    record: String,
    goalDifference: Int,
    totalMatches: Int,
    accent: Color,
    accent2: Color,
    onText: Color
) {
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
                            accent.copy(alpha = 0.22f),
                            accent2.copy(alpha = 0.14f),
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
                    .offset(x = 40.dp, y = (-30).dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                accent.copy(alpha = 0.18f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = GlassBase.copy(alpha = 0.08f)
                    ) {
                        Text(
                            text = "Resumen del equipo",
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HeroInfoChip(
                            text = record,
                            accent = accent,
                            onText = onText
                        )

                        HeroInfoChip(
                            text = if (totalMatches == 0) {
                                "Sin partidos"
                            } else {
                                "DG ${if (goalDifference > 0) "+$goalDifference" else goalDifference}"
                            },
                            accent = when {
                                goalDifference > 0 -> Win
                                goalDifference < 0 -> Loss
                                else -> Draw
                            },
                            onText = onText
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                PerformanceScoreRing(
                    text = winRateText,
                    progress = winRate,
                    accent = accent,
                    accent2 = accent2,
                    onText = onText
                )
            }
        }
    }
}

@Composable
private fun HeroInfoChip(
    text: String,
    accent: Color,
    onText: Color
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = accent.copy(alpha = 0.14f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            color = onText,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun PerformanceScoreRing(
    text: String,
    progress: Float,
    accent: Color,
    accent2: Color,
    onText: Color
) {
    Box(
        modifier = Modifier.size(112.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxSize(),
            color = accent,
            trackColor = GlassBase.copy(alpha = 0.10f),
            strokeWidth = 10.dp
        )

        Surface(
            modifier = Modifier.size(86.dp),
            shape = CircleShape,
            color = Color.Transparent
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            listOf(
                                accent.copy(alpha = 0.28f),
                                accent2.copy(alpha = 0.18f),
                                GlassBase.copy(alpha = 0.10f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = text,
                        color = onText,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "win rate",
                        color = onText.copy(alpha = 0.62f),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
private fun KpiCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
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
                text = title,
                color = onText.copy(alpha = 0.70f),
                style = MaterialTheme.typography.labelLarge
            )

            Text(
                text = value,
                color = onText,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .height(6.dp)
                        .width(28.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(accent, accent2)
                            )
                        )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = subtitle,
                    color = onText.copy(alpha = 0.65f),
                    style = MaterialTheme.typography.bodySmall
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
                style = MaterialTheme.typography.labelMedium
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
    onText: Color
) {
    val safeTotal = total.coerceAtLeast(1)
    val pct = (value.toFloat() / safeTotal.toFloat()).coerceIn(0f, 1f)

    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = onText.copy(alpha = 0.75f),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = if (total == 0) "0" else "$value · ${(pct * 100).toInt()}%",
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
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            color = valueColor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
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
                        .background(
                            Brush.horizontalGradient(
                                listOf(accent, accent2)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = bestPlayer.name.take(1).uppercase(),
                        color = ButtonTextDark,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Jugador mejor valorado",
                        color = onText.copy(alpha = 0.62f),
                        style = MaterialTheme.typography.labelMedium
                    )

                    Text(
                        text = bestPlayer.name,
                        color = onText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "${bestPlayer.position.label} · #${bestPlayer.number} · ${bestPlayer.age} años",
                        color = onText.copy(alpha = 0.65f),
                        style = MaterialTheme.typography.bodyMedium
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
        EmptyLine(
            text = "Aún no hay datos para analizar la carga por tipo.",
            onText = onText
        )
        return
    }

    Spacer(modifier = Modifier.height(4.dp))

    TrainingType.entries.forEach { type ->
        val count = trainings.count { it.type == type }
        val color = when (type) {
            TrainingType.FUERZA -> accent
            TrainingType.RESISTENCIA -> accent2
            TrainingType.VELOCIDAD -> Win
            TrainingType.TECNICA -> Draw
            TrainingType.RECUPERACION -> Loss
        }

        StatBarRow(
            label = type.label,
            value = count,
            total = trainings.size,
            color = color,
            onText = onText
        )
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .width(32.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(accent, accent2)
                            )
                        )
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
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun BottomMenuBar(
    modifier: Modifier = Modifier,
    accent: Color,
    accent2: Color,
    onText: Color,
    selected: BottomTab,
    onSelect: (BottomTab) -> Unit
) {
    Surface(
        modifier = modifier.height(64.dp),
        shape = RoundedCornerShape(22.dp),
        color = GlassBase.copy(alpha = 0.10f),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            accent.copy(alpha = 0.10f),
                            accent2.copy(alpha = 0.08f)
                        )
                    )
                )
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomMenuItem(
                label = "Entr",
                icon = Icons.Default.FitnessCenter,
                isSelected = selected == BottomTab.Training,
                accent = accent,
                accent2 = accent2,
                onText = onText,
                onClick = { onSelect(BottomTab.Training) }
            )

            BottomMenuItem(
                label = "Part",
                icon = Icons.Default.SportsSoccer,
                isSelected = selected == BottomTab.Matches,
                accent = accent,
                accent2 = accent2,
                onText = onText,
                onClick = { onSelect(BottomTab.Matches) }
            )

            BottomMenuItem(
                label = "Jug",
                icon = Icons.Default.Groups,
                isSelected = selected == BottomTab.Players,
                accent = accent,
                accent2 = accent2,
                onText = onText,
                onClick = { onSelect(BottomTab.Players) }
            )

            BottomMenuItem(
                label = "Est",
                icon = Icons.Default.BarChart,
                isSelected = selected == BottomTab.Stats,
                accent = accent,
                accent2 = accent2,
                onText = onText,
                onClick = { onSelect(BottomTab.Stats) }
            )
        }
    }
}

@Composable
private fun RowScope.BottomMenuItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    accent: Color,
    accent2: Color,
    onText: Color,
    onClick: () -> Unit
) {
    val bgBrush = if (isSelected) {
        Brush.horizontalGradient(
            listOf(
                accent.copy(alpha = 0.30f),
                accent2.copy(alpha = 0.24f)
            )
        )
    } else {
        Brush.horizontalGradient(
            listOf(
                Color.Transparent,
                Color.Transparent
            )
        )
    }

    val tint = if (isSelected) {
        ButtonTextDark
    } else {
        onText.copy(alpha = 0.78f)
    }

    Surface(
        modifier = Modifier
            .height(46.dp)
            .weight(1f)
            .padding(horizontal = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = GlassBase.copy(alpha = if (isSelected) 0.12f else 0.02f),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(bgBrush)
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = label,
                color = tint,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                maxLines = 1
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

private fun formatDecimal(value: Double): String {
    return String.format(Locale.getDefault(), "%.1f", value)
}

private fun formatWhole(value: Double): String {
    return String.format(Locale.getDefault(), "%.0f", value)
}