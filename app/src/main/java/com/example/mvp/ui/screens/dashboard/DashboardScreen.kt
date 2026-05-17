package com.example.mvp.ui.screens.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mvp.ui.components.BottomBarDestination
import com.example.mvp.ui.components.ProFootballBottomBar
import com.example.mvp.R
import com.example.mvp.domain.model.Club
import com.example.mvp.domain.model.ClubBadgeDefaults
import com.example.mvp.domain.model.Match
import com.example.mvp.domain.model.MatchResult
import com.example.mvp.domain.model.Player
import com.example.mvp.domain.model.PlayerStatus
import com.example.mvp.domain.model.Training
import com.example.mvp.ui.theme.ButtonTextDark
import com.example.mvp.ui.theme.Draw
import com.example.mvp.ui.theme.GlassBase
import com.example.mvp.ui.theme.Loss
import com.example.mvp.ui.theme.Win
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    username: String = "Usuario",
    club: Club? = null,
    trainings: List<Training> = emptyList(),
    matches: List<Match> = emptyList(),
    players: List<Player> = emptyList(),
    onGoTraining: () -> Unit = {},
    onGoTrainingHistory: () -> Unit = {},
    onGoMatches: () -> Unit = {},
    onGoPlayers: () -> Unit = {},
    onGoStats: () -> Unit = {},
    onGoClub: () -> Unit = {},
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                top = 42.dp,
                bottom = bottomBarHeight + 18.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                DashboardHeader(
                    username = username,
                    accent = accent,
                    accent2 = accent2,
                    onText = onBg,
                    onGoSettings = onGoSettings
                )
            }

            item {
                ClubDashboardCard(
                    club = club,
                    username = username,
                    summary = summary,
                    accent = accent,
                    accent2 = accent2,
                    onText = onBg
                )
            }

            item {
                QuickActionsRow(
                    accent = accent,
                    accent2 = accent2,
                    onText = onBg,
                    onCreateTraining = onCreateTraining,
                    onCreateMatch = onCreateMatch,
                    onCreatePlayer = onCreatePlayer,
                    onGoStats = onGoStats
                )
            }

            item { SectionTitle(title = "Actividad reciente", onText = onBg) }

            item {
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
private fun ClubDashboardCard(
    club: Club?,
    username: String,
    summary: DashboardSummary,
    accent: Color,
    accent2: Color,
    onText: Color
) {
    val clubName = club?.displayName ?: "Configura tu club"
    val season = club?.season?.takeIf { it.isNotBlank() } ?: "Temporada sin definir"
    val city = club?.city?.takeIf { it.isNotBlank() } ?: "Ciudad sin definir"
    val coach = club?.coachName?.takeIf { it.isNotBlank() } ?: username

    val cardShape = RoundedCornerShape(30.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        GlassBase.copy(alpha = 0.145f),
                        accent.copy(alpha = 0.075f),
                        accent2.copy(alpha = 0.095f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(
                        Color.White.copy(alpha = 0.10f),
                        accent.copy(alpha = 0.18f),
                        Color.White.copy(alpha = 0.04f)
                    )
                ),
                shape = cardShape
            )
    ) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.TopEnd)
                .offset(x = 46.dp, y = (-58).dp)
                .background(
                    Brush.radialGradient(
                        listOf(accent.copy(alpha = 0.22f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-42).dp, y = 44.dp)
                .background(
                    Brush.radialGradient(
                        listOf(accent2.copy(alpha = 0.16f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 17.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(23.dp))
                        .background(Color.White.copy(alpha = 0.055f))
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(23.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    ClubBadgeEmblem(
                        badgeId = club?.badgeId ?: ClubBadgeDefaults.DEFAULT_ID,
                        size = 60.dp,
                        accent = accent,
                        accent2 = accent2
                    )
                }

                Spacer(Modifier.width(15.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Club actual",
                        color = accent,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = clubName,
                        color = onText,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "$season · $city",
                        color = onText.copy(alpha = 0.72f),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Míster: $coach",
                        color = onText.copy(alpha = 0.58f),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            HorizontalDivider(color = onText.copy(alpha = 0.075f))

            ClubRecentForm(
                recentResults = summary.recentResults,
                accent = accent,
                onText = onText
            )
        }
    }
}

@Composable
private fun ClubBadgeEmblem(
    badgeId: String,
    size: androidx.compose.ui.unit.Dp,
    accent: Color,
    accent2: Color
) {
    val badge = dashboardBadgePresetById(badgeId)

    Image(
        painter = painterResource(id = badge.drawableRes),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier.size(size)
    )
}

private data class DashboardBadgePreset(
    val id: String,
    val drawableRes: Int
)

private fun dashboardBadgePresets(): List<DashboardBadgePreset> = listOf(
    DashboardBadgePreset("royal_blue", R.drawable.club_badge_classic_gold),
    DashboardBadgePreset("galaxy_purple", R.drawable.club_badge_silver_star),
    DashboardBadgePreset("ocean_cyan", R.drawable.club_badge_purple_ball),
    DashboardBadgePreset("green_star", R.drawable.club_badge_blue_stadium),
    DashboardBadgePreset("fire_red", R.drawable.club_badge_green_city),
    DashboardBadgePreset("gold_crown", R.drawable.club_badge_orange_crown)
)

private fun dashboardBadgePresetById(id: String): DashboardBadgePreset {
    val cleanId = ClubBadgeDefaults.sanitize(id)
    return dashboardBadgePresets().firstOrNull { it.id == cleanId } ?: dashboardBadgePresets().first()
}

@Composable
private fun ClubRecentForm(
    recentResults: List<MatchResult>,
    accent: Color,
    onText: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = "Forma reciente",
                color = onText.copy(alpha = 0.88f),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Últimos partidos",
                color = onText.copy(alpha = 0.52f),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (recentResults.isEmpty()) {
            Text(
                text = "— — —",
                color = accent,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black,
                maxLines = 1
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                recentResults.forEach { result ->
                    FormDot(result = result, fallback = accent)
                }
            }
        }
    }
}

@Composable
private fun FormDot(result: MatchResult, fallback: Color) {
    val color = result.resultColor()
    val letter = when (result) {
        MatchResult.VICTORIA -> "V"
        MatchResult.EMPATE -> "E"
        MatchResult.DERROTA -> "D"
    }

    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.18f))
            .border(
                width = 1.dp,
                color = color.copy(alpha = 0.30f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Black,
            maxLines = 1
        )
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
    Column(verticalArrangement = Arrangement.spacedBy(11.dp)) {
        SectionTitle(title = "Accesos rápidos", onText = onText)

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
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

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
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
    Row(
        modifier = modifier
            .height(58.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(34.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(accent.copy(alpha = 0.90f))
        )

        Spacer(Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
        }

        Spacer(Modifier.width(10.dp))

        Text(
            text = title,
            color = onText,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = if (isCreateAction) Icons.Default.Add else Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = onText.copy(alpha = 0.45f),
            modifier = Modifier.size(18.dp)
        )
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
    Column(verticalArrangement = Arrangement.spacedBy(11.dp)) {
        if (summary.lastTraining == null && summary.lastMatch == null && summary.lastPlayer == null) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = GlassBase.copy(alpha = 0.08f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 13.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Timeline,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(12.dp))
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(21.dp))
            }
            Spacer(Modifier.width(12.dp))
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
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    maxLines = 1
                )
            }
        }

        HorizontalDivider(
            color = onText.copy(alpha = 0.07f),
            modifier = Modifier.padding(start = 50.dp)
        )
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
    val recentResults: List<MatchResult>,
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
                recentResults = matches
                    .sortedByDescending { it.parsedDate() ?: LocalDate.MIN }
                    .take(5)
                    .map { it.result },
                lastTraining = orderedTrainings.firstOrNull(),
                lastMatch = matches.maxByOrNull { it.id },
                lastPlayer = players.maxByOrNull { it.id }
            )
        }
    }
}

private fun Match.parsedDate(): LocalDate? {
    return try {
        LocalDate.parse(dateText, DateTimeFormatter.ofPattern("dd/MM/uuuu"))
    } catch (_: Exception) {
        null
    }
}

private fun Training.parsedDate(): LocalDate? {
    return try {
        LocalDate.parse(dateText, DateTimeFormatter.ofPattern("dd/MM/uuuu"))
    } catch (_: Exception) {
        null
    }
}

private fun Training.isOverdue(today: LocalDate = LocalDate.now()): Boolean {
    val date = parsedDate() ?: return false
    return !isDone && date.isBefore(today)
}

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