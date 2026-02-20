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

@Composable
fun StatsScreen(
    players: List<Player>,
    matches: List<Match>,
    trainings: List<Training>,
    onBack: () -> Unit
) {
    val bgTop = Color(0xFF0B1220)
    val bgMid = Color(0xFF0E2A3B)
    val accent = Color(0xFF00E5FF)
    val accent2 = Color(0xFF7C4DFF)

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
            Header(onBack = onBack, accent = accent)

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatKpiCard(
                    modifier = Modifier.weight(1f),
                    title = "Jugadores",
                    value = totalPlayers.toString(),
                    subtitle = "Titulares $titulares",
                    accent = accent,
                    accent2 = accent2
                )
                StatKpiCard(
                    modifier = Modifier.weight(1f),
                    title = "Partidos",
                    value = totalMatches.toString(),
                    subtitle = "Total registrados",
                    accent = accent,
                    accent2 = accent2
                )
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatKpiCard(
                    modifier = Modifier.weight(1f),
                    title = "Entrenos",
                    value = totalTrainings.toString(),
                    subtitle = "Total registrados",
                    accent = accent,
                    accent2 = accent2
                )
                StatKpiCard(
                    modifier = Modifier.weight(1f),
                    title = "Media Rating",
                    value = if (totalPlayers == 0) "--" else String.format("%.1f", avgRating),
                    subtitle = "Plantilla",
                    accent = accent,
                    accent2 = accent2
                )
            }

            GlassCard(title = "Últimos registros") {
                RecentBlock(title = "Último partido", accent = accent, accent2 = accent2) {
                    if (lastMatch == null) {
                        EmptyLine("Aún no hay partidos.")
                    } else {
                        Text(
                            text = "${lastMatch.competition.label} · vs ${lastMatch.rival}",
                            color = Color.White,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = lastMatch.dateText,
                            color = Color.White.copy(alpha = 0.65f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(8.dp))
                        ScorePill(
                            text = "${lastMatch.goalsFor} - ${lastMatch.goalsAgainst} · ${lastMatch.result.label}",
                            accent = accent
                        )
                    }
                }

                RecentBlock(title = "Último entreno", accent = accent, accent2 = accent2) {
                    if (lastTraining == null) {
                        EmptyLine("Aún no hay entrenos.")
                    } else {
                        Text(
                            text = "${lastTraining.type.label} · ${lastTraining.name}",
                            color = Color.White,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = lastTraining.dateText,
                            color = Color.White.copy(alpha = 0.65f),
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

            GlassCard(title = "Estado de la plantilla") {
                StatBarRow("Titulares", titulares, totalPlayers, Color(0xFF00E676))
                StatBarRow("Suplentes", suplentes, totalPlayers, accent)
                StatBarRow("Lesionados", lesionados, totalPlayers, Color(0xFFFF5252))
            }

            GlassCard(title = "Resultados") {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MiniPill(modifier = Modifier.weight(1f), title = "Victorias", value = wins, color = Color(0xFF00E676))
                    MiniPill(modifier = Modifier.weight(1f), title = "Empates", value = draws, color = accent)
                    MiniPill(modifier = Modifier.weight(1f), title = "Derrotas", value = losses, color = Color(0xFFFF5252))
                }

                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Por competición",
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MiniPill(modifier = Modifier.weight(1f), title = "Liga", value = ligaMatches, color = Color(0xFF00E676))
                    MiniPill(modifier = Modifier.weight(1f), title = "Copa", value = copaMatches, color = accent)
                    MiniPill(modifier = Modifier.weight(1f), title = "Amist.", value = amistosos, color = accent2)
                }

                Spacer(Modifier.height(10.dp))
                StatBarRow("Goles a favor", goalsFor, (goalsFor + goalsAgainst).coerceAtLeast(1), Color(0xFF00E676))
                StatBarRow("Goles en contra", goalsAgainst, (goalsFor + goalsAgainst).coerceAtLeast(1), Color(0xFFFF5252))
            }

            GlassCard(title = "Carga de entrenamiento") {
                StatRow(label = "Minutos totales", value = totalMinutes.toString())
                StatRow(
                    label = "Media por sesión",
                    value = if (totalTrainings == 0) "--" else String.format("%.0f min", avgMinutes)
                )
            }

            GlassCard(title = "Jugador mejor valorado") {
                if (bestPlayer == null) {
                    Text(
                        text = "Aún no hay jugadores.",
                        color = Color.White.copy(alpha = 0.72f),
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
                                color = Color(0xFF061018),
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(Modifier.width(12.dp))

                        Column(Modifier.weight(1f)) {
                            Text(
                                text = bestPlayer.name,
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${bestPlayer.position} · #${bestPlayer.number} · ${bestPlayer.age} años",
                                color = Color.White.copy(alpha = 0.65f),
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
private fun Header(onBack: () -> Unit, accent: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = Color.White
            )
        }

        Spacer(Modifier.width(6.dp))

        Column(Modifier.weight(1f)) {
            Text(
                text = "Estadísticas",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Resumen del equipo",
                color = Color.White.copy(alpha = 0.65f),
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
    accent2: Color
) {
    Surface(
        modifier = modifier,
        color = Color.White.copy(alpha = 0.06f),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                color = Color.White.copy(alpha = 0.70f),
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = value,
                color = Color.White,
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
                    color = Color.White.copy(alpha = 0.65f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun GlassCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        color = Color.White.copy(alpha = 0.06f),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = title,
                color = Color.White,
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
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        color = Color.White.copy(alpha = 0.06f),
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
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
            content()
        }
    }
}

@Composable
private fun EmptyLine(text: String) {
    Text(
        text = text,
        color = Color.White.copy(alpha = 0.72f),
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
    color: Color
) {
    val safeTotal = total.coerceAtLeast(1)
    val pct = (value.toFloat() / safeTotal).coerceIn(0f, 1f)

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.75f),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = value.toString(),
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(Color.White.copy(alpha = 0.08f))
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
private fun StatRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.72f),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            color = Color.White,
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
    color: Color
) {
    Surface(
        modifier = modifier,
        color = Color.White.copy(alpha = 0.06f),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = Color.White.copy(alpha = 0.70f),
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