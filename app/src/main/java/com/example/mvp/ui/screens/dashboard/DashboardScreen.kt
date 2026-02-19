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
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

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
    onOpenRecent: (RecentItem) -> Unit = {}
) {

    val bgTop = Color(0xFF0B1220)
    val bgMid = Color(0xFF0E2A3B)
    val accent = Color(0xFF00E5FF)
    val accent2 = Color(0xFF7C4DFF)

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
                DashboardHeader(username = username, accent = accent)
            }

            item {
                QuickActionsGrid(
                    accent = accent,
                    accent2 = accent2,
                    onGoTraining = onGoTraining,
                    onGoMatches = onGoMatches,
                    onGoPlayers = onGoPlayers,
                    onGoStats = onGoStats
                )
            }

            item {
                SectionTitle(title = "Recientes")
            }

            items(recents) { recent ->
                RecentCard(
                    item = recent,
                    accent = accent,
                    onClick = { onOpenRecent(recent) }
                )
            }
        }
    }
}

@Composable
private fun DashboardHeader(
    username: String,
    accent: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        Column {
            Text(
                text = "ProFootball",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Bienvenido, $username",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.80f)
            )
        }

        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color.White.copy(alpha = 0.06f)
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
                        color = Color.White.copy(alpha = 0.70f),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = "Completar 1 sesión",
                        color = Color.White,
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
    onGoTraining: () -> Unit,
    onGoMatches: () -> Unit,
    onGoPlayers: () -> Unit,
    onGoStats: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        SectionTitle(title = "Accesos rápidos")

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ActionCard(
                title = "Entrenamientos",
                subtitle = "Planifica y crea",
                icon = Icons.Default.FitnessCenter,
                accent = accent,
                accent2 = accent2,
                modifier = Modifier.weight(1f),
                onClick = onGoTraining
            )

            ActionCard(
                title = "Partidos",
                subtitle = "Resultados y xG",
                icon = Icons.Default.SportsSoccer,
                accent = accent,
                accent2 = accent2,
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
                modifier = Modifier.weight(1f),
                onClick = onGoPlayers
            )

            ActionCard(
                title = "Estadísticas",
                subtitle = "Progreso & datos",
                icon = Icons.Default.BarChart,
                accent = accent,
                accent2 = accent2,
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
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(92.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        color = Color.White.copy(alpha = 0.08f)
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
                Icon(icon, contentDescription = null, tint = Color(0xFF061018))
            }

            Column {
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.70f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = Color.White,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun RecentCard(
    item: RecentItem,
    accent: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        color = Color.White.copy(alpha = 0.08f)
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
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = item.subtitle,
                    color = Color.White.copy(alpha = 0.70f),
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