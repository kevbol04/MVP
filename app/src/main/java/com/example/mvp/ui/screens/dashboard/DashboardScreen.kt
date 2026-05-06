package com.example.mvp.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mvp.ui.components.BottomBarDestination
import com.example.mvp.ui.components.ProFootballBottomBar
import com.example.mvp.ui.theme.ButtonTextDark
import com.example.mvp.ui.theme.GlassBase

sealed class RecentItem {
    data class Training(val title: String, val subtitle: String, val trainingId: Long) : RecentItem()
    data class Match(val title: String, val subtitle: String, val matchId: Long) : RecentItem()
    data class Player(val title: String, val subtitle: String, val playerId: Long) : RecentItem()
}

private enum class BottomTab { Training, Matches, Players, Stats }

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    username: String = "Usuario",
    sessionsToComplete: Int = 0,
    lastTraining: RecentItem.Training? = null,
    lastMatch: RecentItem.Match? = null,
    lastPlayer: RecentItem.Player? = null,
    onGoTraining: () -> Unit = {},
    onGoMatches: () -> Unit = {},
    onGoPlayers: () -> Unit = {},
    onGoStats: () -> Unit = {},
    onGoSettings: () -> Unit = {},
    onOpenTraining: (Long) -> Unit = {},
    onOpenMatch: (Long) -> Unit = {},
    onOpenPlayer: (Long) -> Unit = {},
) {
    val bgTop = MaterialTheme.colorScheme.background
    val bgMid = MaterialTheme.colorScheme.surface
    val accent = MaterialTheme.colorScheme.primary
    val accent2 = MaterialTheme.colorScheme.secondary
    val onBg = MaterialTheme.colorScheme.onBackground

    val recents = remember(lastTraining, lastMatch, lastPlayer) {
        buildList<RecentItem> {
            lastTraining?.let { add(it) }
            lastMatch?.let { add(it) }
            lastPlayer?.let { add(it) }
        }
    }

    val bottomBarHeight = 96.dp

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(bgTop, bgMid, bgTop)))
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 28.dp, bottom = bottomBarHeight + 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                DashboardHeader(
                    username = username,
                    sessionsToComplete = sessionsToComplete,
                    hasRecentActivity = recents.isNotEmpty(),
                    accent = accent,
                    accent2 = accent2,
                    onText = onBg,
                    onGoSettings = onGoSettings,
                    onOpenObjective = onGoTraining
                )
            }

            item {
                QuickActionsGrid(
                    accent = accent,
                    accent2 = accent2,
                    onText = onBg,
                    onGoTraining = onGoTraining,
                    onGoMatches = onGoMatches,
                    onGoPlayers = onGoPlayers,
                    onGoStats = onGoStats
                )
            }

            item {
                SectionTitle(title = "Recientes", onText = onBg)
            }

            if (recents.isEmpty()) {
                item {
                    EmptyRecentCard(
                        accent = accent,
                        accent2 = accent2,
                        onText = onBg,
                        onCreateTraining = onGoTraining,
                        onCreatePlayer = onGoPlayers,
                        onCreateMatch = onGoMatches
                    )
                }
            } else {
                items(recents) { recent ->
                    when (recent) {
                        is RecentItem.Training -> RecentCard(
                            title = recent.title,
                            subtitle = recent.subtitle,
                            accent = accent,
                            onText = onBg,
                            onClick = { onOpenTraining(recent.trainingId) }
                        )

                        is RecentItem.Match -> RecentCard(
                            title = recent.title,
                            subtitle = recent.subtitle,
                            accent = accent,
                            onText = onBg,
                            onClick = { onOpenMatch(recent.matchId) }
                        )

                        is RecentItem.Player -> RecentCard(
                            title = recent.title,
                            subtitle = recent.subtitle,
                            accent = accent,
                            onText = onBg,
                            onClick = { onOpenPlayer(recent.playerId) }
                        )
                    }
                }
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
    sessionsToComplete: Int,
    hasRecentActivity: Boolean,
    accent: Color,
    accent2: Color,
    onText: Color,
    onGoSettings: () -> Unit,
    onOpenObjective: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "ProFootball",
                    style = MaterialTheme.typography.titleLarge,
                    color = onText,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Bienvenido, $username",
                    style = MaterialTheme.typography.bodyMedium,
                    color = onText.copy(alpha = 0.80f)
                )
            }

            Surface(
                modifier = Modifier
                    .size(44.dp)
                    .clickable { onGoSettings() },
                shape = CircleShape,
                color = GlassBase.copy(alpha = 0.10f),
                tonalElevation = 0.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.radialGradient(
                                listOf(
                                    accent.copy(alpha = 0.35f),
                                    accent2.copy(alpha = 0.18f),
                                    Color.Transparent
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Configuración",
                        tint = ButtonTextDark
                    )
                }
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenObjective() },
            shape = RoundedCornerShape(18.dp),
            color = GlassBase.copy(alpha = 0.06f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Objetivos a cumplir",
                        color = onText.copy(alpha = 0.70f),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = when {
                            sessionsToComplete > 0 ->
                                "Completar $sessionsToComplete sesión${if (sessionsToComplete == 1) "" else "es"}"

                            !hasRecentActivity ->
                                "Empieza creando tu primer registro"

                            else ->
                                "No hay sesiones pendientes"
                        },
                        color = onText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = accent.copy(alpha = 0.18f)
                ) {
                    Text(
                        text = "VER",
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
private fun QuickActionsGrid(
    accent: Color,
    accent2: Color,
    onText: Color,
    onGoTraining: () -> Unit,
    onGoMatches: () -> Unit,
    onGoPlayers: () -> Unit,
    onGoStats: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(title = "Accesos rápidos", onText = onText)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ActionCard(
                title = "Entrenamientos",
                subtitle = "Planifica & crea",
                icon = Icons.Default.FitnessCenter,
                accent = accent,
                accent2 = accent2,
                onText = onText,
                modifier = Modifier.weight(1f),
                onClick = onGoTraining
            )
            ActionCard(
                title = "Partidos",
                subtitle = "Encuentros & resultados",
                icon = Icons.Default.SportsSoccer,
                accent = accent,
                accent2 = accent2,
                onText = onText,
                modifier = Modifier.weight(1f),
                onClick = onGoMatches
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ActionCard(
                title = "Jugadores",
                subtitle = "Plantilla & roles",
                icon = Icons.Default.Groups,
                accent = accent,
                accent2 = accent2,
                onText = onText,
                modifier = Modifier.weight(1f),
                onClick = onGoPlayers
            )
            ActionCard(
                title = "Estadísticas",
                subtitle = "Progreso & datos",
                icon = Icons.Default.BarChart,
                accent = accent,
                accent2 = accent2,
                onText = onText,
                modifier = Modifier.weight(1f),
                onClick = onGoStats
            )
        }
    }
}

@Composable
private fun ActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color,
    accent2: Color,
    onText: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(92.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        color = GlassBase.copy(alpha = 0.08f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(accent.copy(alpha = 0.40f), accent2.copy(alpha = 0.35f))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = ButtonTextDark)
            }

            Column {
                Text(
                    text = title,
                    color = onText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    color = onText.copy(alpha = 0.70f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String, onText: Color) {
    Text(
        text = title,
        color = onText,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun EmptyRecentCard(
    accent: Color,
    accent2: Color,
    onText: Color,
    onCreateTraining: () -> Unit,
    onCreatePlayer: () -> Unit,
    onCreateMatch: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = GlassBase.copy(alpha = 0.08f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(
                                accent.copy(alpha = 0.38f),
                                accent2.copy(alpha = 0.32f)
                            )
                        ),
                        shape = RoundedCornerShape(18.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = ButtonTextDark
                )
            }

            Text(
                text = "Todavía no hay actividad reciente",
                color = onText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 14.dp)
            )

            Text(
                text = "Cuando añadas entrenamientos, partidos o jugadores, aparecerán aquí.",
                color = onText.copy(alpha = 0.68f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onCreateTraining,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accent.copy(alpha = 0.90f),
                    contentColor = ButtonTextDark
                )
            ) {
                Text("Crear entrenamiento")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onCreatePlayer,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = onText
                    )
                ) {
                    Text("Jugador")
                }

                OutlinedButton(
                    onClick = onCreateMatch,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = onText
                    )
                ) {
                    Text("Partido")
                }
            }
        }
    }
}

@Composable
private fun RecentCard(
    title: String,
    subtitle: String,
    accent: Color,
    onText: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        color = GlassBase.copy(alpha = 0.08f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = onText,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    color = onText.copy(alpha = 0.70f),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = accent.copy(alpha = 0.16f)
            ) {
                Text(
                    text = "Ver",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    color = accent,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun BottomMenuBar(
    modifier: Modifier = Modifier,
    accent: Color,
    accent2: Color,
    onText: Color,
    selected: BottomTab?,
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
                        listOf(accent.copy(alpha = 0.10f), accent2.copy(alpha = 0.08f))
                    )
                )
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomMenuItem("Entr", Icons.Default.FitnessCenter, selected == BottomTab.Training, accent, accent2, onText) {
                onSelect(BottomTab.Training)
            }
            BottomMenuItem("Part", Icons.Default.SportsSoccer, selected == BottomTab.Matches, accent, accent2, onText) {
                onSelect(BottomTab.Matches)
            }
            BottomMenuItem("Jug", Icons.Default.Groups, selected == BottomTab.Players, accent, accent2, onText) {
                onSelect(BottomTab.Players)
            }
            BottomMenuItem("Est", Icons.Default.BarChart, selected == BottomTab.Stats, accent, accent2, onText) {
                onSelect(BottomTab.Stats)
            }
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
        Brush.horizontalGradient(listOf(accent.copy(alpha = 0.30f), accent2.copy(alpha = 0.24f)))
    } else {
        Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
    }

    val tint = if (isSelected) ButtonTextDark else onText.copy(alpha = 0.78f)

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
            Icon(icon, contentDescription = label, tint = tint)
            Spacer(Modifier.width(8.dp))
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