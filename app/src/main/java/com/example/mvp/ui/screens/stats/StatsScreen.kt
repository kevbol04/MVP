package com.example.mvp.ui.screens.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.mvp.ui.theme.ButtonTextDark
import com.example.mvp.ui.theme.GlassBase
import com.example.mvp.ui.theme.Loss
import com.example.mvp.ui.theme.Win

@Composable
fun StatsScreen(
    players: List<Player>,
    matches: List<Match>,
    trainings: List<Training>,
    onBack: () -> Unit
) {
    val bgTop = MaterialTheme.colorScheme.background
    val bgMid = MaterialTheme.colorScheme.surface
    val accent = MaterialTheme.colorScheme.primary
    val accent2 = MaterialTheme.colorScheme.secondary
    val onBg = MaterialTheme.colorScheme.onBackground

    val scroll = rememberScrollState()

    // ---------------- PLAYERS ----------------
    val totalPlayers = players.size
    val titulares = players.count { it.status == PlayerStatus.TITULAR }
    val suplentes = players.count { it.status == PlayerStatus.SUPLENTE }
    val lesionados = players.count { it.status == PlayerStatus.LESIONADO }

    val avgRating = if (players.isNotEmpty()) players.map { it.rating }.average() else 0.0
    val bestPlayer = players.maxByOrNull { it.rating }

    // ---------------- MATCHES ----------------
    val totalMatches = matches.size
    val wins = matches.count { it.result == MatchResult.VICTORIA }
    val draws = matches.count { it.result == MatchResult.EMPATE }
    val losses = matches.count { it.result == MatchResult.DERROTA }

    val goalsFor = matches.sumOf { it.goalsFor }
    val goalsAgainst = matches.sumOf { it.goalsAgainst }

    val ligaMatches = matches.count { it.competition == Competition.LIGA }
    val copaMatches = matches.count { it.competition == Competition.COPA }
    val amistosos = matches.count { it.competition == Competition.AMISTOSO }

    val lastMatch = matches.firstOrNull()

    // ---------------- TRAININGS ----------------
    val totalTrainings = trainings.size
    val totalMinutes = trainings.sumOf { it.durationMin }
    val avgMinutes = if (trainings.isNotEmpty()) totalMinutes.toDouble() / trainings.size else 0.0

    val lastTraining = trainings.firstOrNull()

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
                        colors = listOf(accent.copy(alpha = 0.28f), Color.Transparent)
                    )
                )
                .align(Alignment.TopEnd)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Header(onBack = onBack, onText = onBg)

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatKpiCard(
                    modifier = Modifier.weight(1f),
                    title = "Jugadores",
                    value = totalPlayers.toString(),
                    subtitle = "Titulares $titulares",
                    accent = accent,
                    accent2 = accent2,
                    onText = onBg
                )
                StatKpiCard(
                    modifier = Modifier.weight(1f),
                    title = "Partidos",
                    value = totalMatches.toString(),
                    subtitle = "Total registrados",
                    accent = accent,
                    accent2 = accent2,
                    onText = onBg
                )
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatKpiCard(
                    modifier = Modifier.weight(1f),
                    title = "Entrenos",
                    value = totalTrainings.toString(),
                    subtitle = "Total registrados",
                    accent = accent,
                    accent2 = accent2,
                    onText = onBg
                )
                StatKpiCard(
                    modifier = Modifier.weight(1f),
                    title = "Media Rating",
                    value = if (totalPlayers == 0) "--" else String.format("%.1f", avgRating),
                    subtitle = "Plantilla",
                    accent = accent,
                    accent2 = accent2,
                    onText = onBg
                )
            }

            GlassCard(title = "Últimos registros", onText = onBg) {
                RecentBlock(title = "Último partido", accent = accent, accent2 = accent2, onText = onBg) {
                    if (lastMatch == null) {
                        EmptyLine("Aún no hay partidos.", onText = onBg)
                    } else {
                        Text(
                            text = "${lastMatch.competition.label} · vs ${lastMatch.rival}",
                            color = onBg,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = lastMatch.dateText,
                            color = onBg.copy(alpha = 0.65f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(8.dp))
                        ScorePill(
                            text = "${lastMatch.goalsFor} - ${lastMatch.goalsAgainst} · ${lastMatch.result.label}",
                            accent = accent
                        )
                    }
                }

                RecentBlock(title = "Último entreno", accent = accent, accent2 = accent2, onText = onBg) {
                    if (lastTraining == null) {
                        EmptyLine("Aún no hay entrenos.", onText = onBg)
                    } else {
                        Text(
                            text = "${lastTraining.type.label} · ${lastTraining.name}",
                            color = onBg,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = lastTraining.dateText,
                            color = onBg.copy(alpha = 0.65f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(8.dp))
                        ScorePill(
                            text = "${lastTraining.durationMin} min",
                            accent = accent
                        )
                    }
                }
            }

            GlassCard(title = "Estado de la plantilla", onText = onBg) {
                StatBarRow("Titulares", titulares, totalPlayers, Win, onText = onBg)
                StatBarRow("Suplentes", suplentes, totalPlayers, accent, onText = onBg)
                StatBarRow("Lesionados", lesionados, totalPlayers, Loss, onText = onBg)
            }

            GlassCard(title = "Resultados", onText = onBg) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MiniPill(modifier = Modifier.weight(1f), title = "Victorias", value = wins, color = Win, onText = onBg)
                    MiniPill(modifier = Modifier.weight(1f), title = "Empates", value = draws, color = accent, onText = onBg)
                    MiniPill(modifier = Modifier.weight(1f), title = "Derrotas", value = losses, color = Loss, onText = onBg)
                }

                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Por competición",
                    color = onBg,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MiniPill(modifier = Modifier.weight(1f), title = "Liga", value = ligaMatches, color = Win, onText = onBg)
                    MiniPill(modifier = Modifier.weight(1f), title = "Copa", value = copaMatches, color = accent, onText = onBg)
                    MiniPill(modifier = Modifier.weight(1f), title = "Amist.", value = amistosos, color = accent2, onText = onBg)
                }

                Spacer(Modifier.height(10.dp))
                val totalGoals = (goalsFor + goalsAgainst).coerceAtLeast(1)
                StatBarRow("Goles a favor", goalsFor, totalGoals, Win, onText = onBg)
                StatBarRow("Goles en contra", goalsAgainst, totalGoals, Loss, onText = onBg)
            }

            GlassCard(title = "Carga de entrenamiento", onText = onBg) {
                StatRow(label = "Minutos totales", value = totalMinutes.toString(), onText = onBg)
                StatRow(
                    label = "Media por sesión",
                    value = if (totalTrainings == 0) "--" else String.format("%.0f min", avgMinutes),
                    onText = onBg
                )
            }

            GlassCard(title = "Jugador mejor valorado", onText = onBg) {
                if (bestPlayer == null) {
                    Text(
                        text = "Aún no hay jugadores.",
                        color = onBg.copy(alpha = 0.72f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
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

                        Spacer(Modifier.width(12.dp))

                        Column(Modifier.weight(1f)) {
                            Text(
                                text = bestPlayer.name,
                                color = onBg,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${bestPlayer.position} · #${bestPlayer.number} · ${bestPlayer.age} años",
                                color = onBg.copy(alpha = 0.65f),
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

            Spacer(Modifier.height(10.dp))
        }
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
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = onText
            )
        }

        Spacer(Modifier.width(6.dp))

        Column(Modifier.weight(1f)) {
            Text(
                text = "Estadísticas",
                color = onText,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Resumen del equipo",
                color = onText.copy(alpha = 0.65f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun StatKpiCard(
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .height(6.dp)
                        .width(28.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Brush.horizontalGradient(listOf(accent, accent2)))
                )
                Spacer(Modifier.width(8.dp))
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
        color = GlassBase.copy(alpha = 0.06f),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
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
                Spacer(Modifier.width(10.dp))
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
private fun EmptyLine(text: String, onText: Color) {
    Text(
        text = text,
        color = onText.copy(alpha = 0.72f),
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
private fun ScorePill(text: String, accent: Color) {
    Surface(
        color = accent.copy(alpha = 0.16f),
        shape = RoundedCornerShape(14.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            color = accent,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
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
    val pct = (value.toFloat() / safeTotal).coerceIn(0f, 1f)

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                color = onText.copy(alpha = 0.75f),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = value.toString(),
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
private fun StatRow(label: String, value: String, onText: Color) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            color = onText.copy(alpha = 0.72f),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            color = onText,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun MiniPill(
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
            Spacer(Modifier.height(6.dp))
            Text(
                text = value.toString(),
                color = color,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}