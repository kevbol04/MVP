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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mvp.ui.theme.ButtonTextDark
import com.example.mvp.ui.theme.GlassBase

data class RecentItem(
    val title: String,
    val subtitle: String
)

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    username: String = "Usuario",
    onGoTraining: () -> Unit = {},
    onGoMatches: () -> Unit = {},
    onGoPlayers: () -> Unit = {},
    onGoStats: () -> Unit = {},
    onGoSettings: () -> Unit = {},
    onOpenRecent: (RecentItem) -> Unit = {}
) {
    val bgTop = MaterialTheme.colorScheme.background
    val bgMid = MaterialTheme.colorScheme.surface
    val accent = MaterialTheme.colorScheme.primary
    val accent2 = MaterialTheme.colorScheme.secondary
    val onBg = MaterialTheme.colorScheme.onBackground

    val recents = listOf(
        RecentItem("Entrenamiento de Pierna", "12/11/2025 · 90 min · Fuerza"),
        RecentItem("Partido: Real Sociedad vs Real Betis", "Jornada 12 · Estadísticas"),
        RecentItem("Jugador destacado", "Progreso semanal actualizado")
    )

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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 28.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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

            items(recents) { recent ->
                RecentCard(
                    item = recent,
                    accent = accent,
                    onText = onBg,
                    onClick = { onOpenRecent(recent) }
                )
            }
        }
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
                        text = "Objetivo de hoy",
                        color = onText.copy(alpha = 0.70f),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = "Completar 1 sesión",
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
                        text = "ON",
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
private fun RecentCard(
    item: RecentItem,
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
                    text = item.title,
                    color = onText,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = item.subtitle,
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