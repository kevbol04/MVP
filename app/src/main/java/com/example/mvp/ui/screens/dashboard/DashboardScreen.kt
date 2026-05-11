package com.example.mvp.ui.screens.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mvp.ui.components.BottomBarDestination
import com.example.mvp.ui.components.ProFootballBottomBar
import com.example.mvp.ui.screens.matches.Match
import com.example.mvp.ui.screens.matches.MatchResult
import com.example.mvp.ui.screens.players.Player
import com.example.mvp.ui.screens.players.PlayerStatus
import com.example.mvp.ui.screens.training.Training
import com.example.mvp.ui.theme.ButtonTextDark
import com.example.mvp.ui.theme.Draw
import com.example.mvp.ui.theme.GlassBase
import com.example.mvp.ui.theme.Loss
import com.example.mvp.ui.theme.Win
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    username: String = "Usuario",
    trainings: List<Training> = emptyList(),
    matches: List<Match> = emptyList(),
    players: List<Player> = emptyList(),
    onGoTraining: () -> Unit = {},
    onGoMatches: () -> Unit = {},
    onGoPlayers: () -> Unit = {},
    onGoStats: () -> Unit = {},
    onGoSettings: () -> Unit = {},
    onCreateTraining: () -> Unit = {},
    onCreateMatch: () -> Unit = {},
    onCreatePlayer: () -> Unit = {},
    onOpenTraining: (Long) -> Unit = {},
    onOpenMatch: (Long) -> Unit = {},
    onOpenPlayer: (Long) -> Unit = {}
) {
    val bgTop = MaterialTheme.colorScheme.background
    val bgMid = MaterialTheme.colorScheme.surface
    val accent = MaterialTheme.colorScheme.primary
    val accent2 = MaterialTheme.colorScheme.secondary
    val onBg = MaterialTheme.colorScheme.onBackground
    val danger = MaterialTheme.colorScheme.error
    val bottomBarHeight = 92.dp

    val summary = remember(trainings, matches, players) {
        DashboardSummary.from(
            trainings = trainings,
            matches = matches,
            players = players
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(bgTop, bgMid, bgTop)))
    ) {
        Box(
            modifier = Modifier
                .size(230.dp)
                .align(Alignment.TopEnd)
                .offset(x = 72.dp, y = (-50).dp)
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
                .padding(horizontal = 18.dp)
                .padding(top = 42.dp, bottom = bottomBarHeight + 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DashboardHeader(
                username = username,
                accent = accent,
                accent2 = accent2,
                onText = onBg,
                onGoSettings = onGoSettings
            )

            ObjectiveCard(
                summary = summary,
                accent = accent,
                onText = onBg,
                onClick = onGoTraining
            )

            QuickActionsRow(
                accent = accent,
                accent2 = accent2,
                onText = onBg,
                onCreateTraining = onCreateTraining,
                onCreateMatch = onCreateMatch,
                onCreatePlayer = onCreatePlayer,
                onGoStats = onGoStats
            )

            SectionTitle(title = "Resumen rápido", onText = onBg)

            StatsGrid(
                summary = summary,
                accent = accent,
                accent2 = accent2,
                danger = danger,
                onText = onBg,
                onGoTraining = onGoTraining,
                onGoMatches = onGoMatches,
                onGoPlayers = onGoPlayers
            )

            SectionTitle(title = "Actividad reciente", onText = onBg)

            RecentActivityPanel(
                summary = summary,
                accent = accent,
                accent2 = accent2,
                danger = danger,
                onText = onBg,
                onOpenTraining = onOpenTraining,
                onOpenMatch = onOpenMatch,
                onOpenPlayer = onOpenPlayer,
                onCreateTraining = onCreateTraining
            )
        }

        ProFootballBottomBar(
            selected = BottomBarDestination.Dashboard,
            onSelect = { destination ->
                when (destination) {
                    BottomBarDestination.Training -> onGoTraining()
                    BottomBarDestination.Players -> onGoPlayers()
                    BottomBarDestination.Dashboard -> Unit
                    BottomBarDestination.Matches -> onGoMatches()
                    BottomBarDestination.Stats -> onGoStats()
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
private fun DashboardHeader(
    username: String,
    accent: Color,
    accent2: Color,
    onText: Color,
    onGoSettings: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Brush.horizontalGradient(listOf(accent, accent2))),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = usernameInitial(username),
                color = ButtonTextDark,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Text(
                text = "ProFootball",
                color = onText,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Bienvenido, $username",
                color = onText.copy(alpha = 0.68f),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        IconButton(onClick = onGoSettings) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Ajustes",
                tint = onText.copy(alpha = 0.86f)
            )
        }
    }
}

@Composable
private fun ObjectiveCard(
    summary: DashboardSummary,
    accent: Color,
    onText: Color,
    onClick: () -> Unit
) {
    val remaining = (12 - summary.doneTrainings).coerceAtLeast(0)
    val objectiveText = if (remaining == 0) "Objetivo completado" else "Completar $remaining sesiones"

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        shape = RoundedCornerShape(20.dp),
        color = GlassBase.copy(alpha = 0.08f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Objetivo actual",
                    color = onText.copy(alpha = 0.68f),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = objectiveText,
                    color = onText,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            SmallPillButton(text = "VER", accent = accent, onClick = onClick)
        }
    }
}

@Composable
private fun SmallPillButton(text: String, accent: Color, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = accent.copy(alpha = 0.16f),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            color = accent,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
        )
    }
}

@Composable
private fun QuickActionsRow(
    accent: Color,
    accent2: Color,
    onText: Color,
    onCreateTraining: () -> Unit,
    onCreateMatch: () -> Unit,
    onCreatePlayer: () -> Unit,
    onGoStats: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        SectionTitle(title = "Accesos rápidos", onText = onText)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            QuickActionCard(
                modifier = Modifier.weight(1f),
                title = "Entreno",
                icon = Icons.Default.FitnessCenter,
                accent = accent,
                onText = onText,
                onClick = onCreateTraining
            )
            QuickActionCard(
                modifier = Modifier.weight(1f),
                title = "Partido",
                icon = Icons.Default.SportsSoccer,
                accent = accent2,
                onText = onText,
                onClick = onCreateMatch
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            QuickActionCard(
                modifier = Modifier.weight(1f),
                title = "Jugador",
                icon = Icons.Default.Person,
                accent = accent2,
                onText = onText,
                onClick = onCreatePlayer
            )
            QuickActionCard(
                modifier = Modifier.weight(1f),
                title = "Stats",
                icon = Icons.Default.BarChart,
                accent = accent,
                onText = onText,
                isCreateAction = false,
                onClick = onGoStats
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    modifier: Modifier,
    title: String,
    icon: ImageVector,
    accent: Color,
    onText: Color,
    isCreateAction: Boolean = true,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(54.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = GlassBase.copy(alpha = 0.08f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(9.dp))
            Text(
                text = title,
                color = onText,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.weight(1f))
            Icon(
                imageVector = if (isCreateAction) Icons.Default.Add else Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = onText.copy(alpha = 0.50f),
                modifier = Modifier.size(17.dp)
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String, onText: Color) {
    Text(
        text = title,
        color = onText,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 2.dp)
    )
}

@Composable
private fun StatsGrid(
    summary: DashboardSummary,
    accent: Color,
    accent2: Color,
    danger: Color,
    onText: Color,
    onGoTraining: () -> Unit,
    onGoMatches: () -> Unit,
    onGoPlayers: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SummaryCard(
                modifier = Modifier.weight(1f),
                title = "Cumplimiento",
                value = "${summary.completionRate}%",
                detail = "${summary.doneTrainings}/${summary.totalTrainings} entrenos",
                icon = Icons.Default.CheckCircle,
                color = accent,
                onText = onText,
                onClick = onGoTraining
            )
            SummaryCard(
                modifier = Modifier.weight(1f),
                title = "Atrasados",
                value = summary.overdueTrainings.size.toString(),
                detail = if (summary.overdueTrainings.isEmpty()) "Todo al día" else "Revisar pendientes",
                icon = Icons.Default.WarningAmber,
                color = if (summary.overdueTrainings.isEmpty()) accent2 else danger,
                onText = onText,
                onClick = onGoTraining
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SummaryCard(
                modifier = Modifier.weight(1f),
                title = "Balance",
                value = summary.matchBalance,
                detail = "${summary.matchesPlayed} partidos",
                icon = Icons.Default.SportsSoccer,
                color = summary.balanceColor(fallback = accent2),
                onText = onText,
                onClick = onGoMatches
            )
            SummaryCard(
                modifier = Modifier.weight(1f),
                title = "Plantilla",
                value = summary.availablePlayers.toString(),
                detail = "${summary.playersCount} jugadores · OVR ${summary.averageRating}",
                icon = Icons.Default.Groups,
                color = accent,
                onText = onText,
                onClick = onGoPlayers
            )
        }
    }
}

@Composable
private fun SummaryCard(
    modifier: Modifier,
    title: String,
    value: String,
    detail: String,
    icon: ImageVector,
    color: Color,
    onText: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(80.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = GlassBase.copy(alpha = 0.08f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(17.dp))
            }

            Spacer(Modifier.width(9.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = value,
                    color = onText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = title,
                    color = onText.copy(alpha = 0.86f),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = onText.copy(alpha = 0.38f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun RecentActivityPanel(
    summary: DashboardSummary,
    accent: Color,
    accent2: Color,
    danger: Color,
    onText: Color,
    onOpenTraining: (Long) -> Unit,
    onOpenMatch: (Long) -> Unit,
    onOpenPlayer: (Long) -> Unit,
    onCreateTraining: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        if (summary.lastTraining == null && summary.lastMatch == null && summary.lastPlayer == null) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = GlassBase.copy(alpha = 0.08f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Timeline,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "Aún no hay actividad",
                            color = onText,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Crea tu primer entrenamiento para empezar.",
                            color = onText.copy(alpha = 0.66f),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    SmallTextButton(text = "Crear", onText = accent, onClick = onCreateTraining)
                }
            }
            return
        }

        var shown = 0

        summary.lastTraining?.let { training ->
            if (shown < 3) {
                RecentRow(
                    title = training.name,
                    subtitle = "${training.dateText} · ${training.durationMin} min · ${training.type.label}",
                    chip = training.dashboardStatus(),
                    icon = Icons.Default.FitnessCenter,
                    color = when {
                        training.isDone -> accent
                        training.isOverdue() -> danger
                        else -> accent2
                    },
                    onText = onText,
                    onClick = { onOpenTraining(training.id.toLong()) }
                )
                shown++
            }
        }

        summary.lastMatch?.let { match ->
            if (shown < 3) {
                RecentRow(
                    title = "${match.rival} · ${match.goalsFor}-${match.goalsAgainst}",
                    subtitle = "${match.dateText} · ${match.competition.label}",
                    chip = match.result.label,
                    icon = Icons.Default.SportsSoccer,
                    color = match.result.resultColor(),
                    onText = onText,
                    onClick = { onOpenMatch(match.id.toLong()) }
                )
                shown++
            }
        }

        summary.lastPlayer?.let { player ->
            if (shown < 3) {
                RecentRow(
                    title = player.name,
                    subtitle = "${player.position.label} · #${player.number} · ${player.status.label}",
                    chip = "OVR ${player.rating}",
                    icon = Icons.Default.Person,
                    color = accent2,
                    onText = onText,
                    onClick = { onOpenPlayer(player.id.toLong()) }
                )
            }
        }
    }
}

@Composable
private fun RecentRow(
    title: String,
    subtitle: String,
    chip: String,
    icon: ImageVector,
    color: Color,
    onText: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = GlassBase.copy(alpha = 0.08f),
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = onText,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    color = onText.copy(alpha = 0.62f),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = color.copy(alpha = 0.14f)
            ) {
                Text(
                    text = chip,
                    color = color,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun SmallTextButton(text: String, onText: Color, onClick: () -> Unit) {
    Text(
        text = text,
        color = onText,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    )
}

private data class DashboardSummary(
    val totalTrainings: Int,
    val doneTrainings: Int,
    val overdueTrainings: List<Training>,
    val completionRate: Int,
    val matchesPlayed: Int,
    val wins: Int,
    val draws: Int,
    val losses: Int,
    val playersCount: Int,
    val availablePlayers: Int,
    val averageRating: Int,
    val lastTraining: Training?,
    val lastMatch: Match?,
    val lastPlayer: Player?
) {
    val matchBalance: String
        get() = "$wins-$draws-$losses"

    fun balanceColor(fallback: Color): Color = when {
        matchesPlayed == 0 -> fallback
        wins > losses -> Win
        losses > wins -> Loss
        else -> Draw
    }

    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        fun from(
            trainings: List<Training>,
            matches: List<Match>,
            players: List<Player>
        ): DashboardSummary {
            val today = LocalDate.now()
            val orderedTrainings = trainings.sortedByDescending { it.id }
            val doneTrainings = trainings.filter { it.isDone }
            val pendingTrainings = trainings.filter { !it.isDone }
            val overdueTrainings = pendingTrainings
                .filter { it.isOverdue(today) }
                .sortedWith(compareBy<Training> { it.parsedDate() ?: LocalDate.MAX }.thenBy { it.name.lowercase() })

            val matchesPlayed = matches.size
            val wins = matches.count { it.result == MatchResult.VICTORIA }
            val draws = matches.count { it.result == MatchResult.EMPATE }
            val losses = matches.count { it.result == MatchResult.DERROTA }
            val playersCount = players.size
            val availablePlayers = players.count { it.status != PlayerStatus.LESIONADO }
            val averageRating = if (players.isEmpty()) 0 else players.map { it.rating }.average().toInt()
            val completionRate = if (trainings.isEmpty()) 0 else ((doneTrainings.size * 100f) / trainings.size).toInt()

            return DashboardSummary(
                totalTrainings = trainings.size,
                doneTrainings = doneTrainings.size,
                overdueTrainings = overdueTrainings,
                completionRate = completionRate,
                matchesPlayed = matchesPlayed,
                wins = wins,
                draws = draws,
                losses = losses,
                playersCount = playersCount,
                availablePlayers = availablePlayers,
                averageRating = averageRating,
                lastTraining = orderedTrainings.firstOrNull(),
                lastMatch = matches.maxByOrNull { it.id },
                lastPlayer = players.maxByOrNull { it.id }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun Training.parsedDate(): LocalDate? {
    return try {
        LocalDate.parse(dateText, DateTimeFormatter.ofPattern("dd/MM/uuuu"))
    } catch (_: Exception) {
        null
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun Training.isOverdue(today: LocalDate = LocalDate.now()): Boolean {
    val date = parsedDate() ?: return false
    return !isDone && date.isBefore(today)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun Training.dashboardStatus(): String {
    return when {
        isDone -> "Hecho"
        isOverdue() -> "Atrasado"
        else -> "Pendiente"
    }
}

private fun MatchResult.resultColor(): Color {
    return when (this) {
        MatchResult.VICTORIA -> Win
        MatchResult.EMPATE -> Draw
        MatchResult.DERROTA -> Loss
    }
}

private fun usernameInitial(username: String): String {
    return username.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "U"
}