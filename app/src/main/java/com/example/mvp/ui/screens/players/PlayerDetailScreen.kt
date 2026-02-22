package com.example.mvp.ui.screens.players

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mvp.ui.theme.ButtonTextDark
import com.example.mvp.ui.theme.GlassBase
import com.example.mvp.ui.theme.Loss
import com.example.mvp.ui.theme.Win

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerDetailScreen(
    modifier: Modifier = Modifier,
    player: Player,
    onBack: () -> Unit = {},
    onEdit: (Player) -> Unit = {}
) {
    val bgTop = MaterialTheme.colorScheme.background
    val bgMid = MaterialTheme.colorScheme.surface
    val accent = MaterialTheme.colorScheme.primary
    val accent2 = MaterialTheme.colorScheme.secondary
    val onBg = MaterialTheme.colorScheme.onBackground

    val (statusBg, statusFg) = when (player.status) {
        PlayerStatus.TITULAR -> Win.copy(alpha = 0.16f) to Win
        PlayerStatus.SUPLENTE -> accent.copy(alpha = 0.16f) to accent
        PlayerStatus.LESIONADO -> Loss.copy(alpha = 0.16f) to Loss
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(bgTop, bgMid, bgTop)))
            .padding(horizontal = 20.dp)
    ) {
        Box(
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.TopEnd)
                .offset(x = 80.dp, y = (-70).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(accent.copy(alpha = 0.22f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp, bottom = 14.dp),
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
                Text(
                    text = "Detalle jugador",
                    color = onBg,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = GlassBase.copy(alpha = 0.08f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(84.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(accent.copy(alpha = 0.35f), accent2.copy(alpha = 0.30f))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initials(player.name),
                                color = ButtonTextDark,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Spacer(Modifier.width(14.dp))

                        Column(Modifier.weight(1f)) {
                            Text(
                                text = player.name,
                                color = onBg,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${player.position.label} · #${player.number}",
                                color = onBg.copy(alpha = 0.70f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        IconButton(onClick = { onEdit(player) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = accent)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = accent.copy(alpha = 0.16f)
                        ) {
                            Text(
                                text = "OVR ${player.rating}",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                color = accent,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = statusBg
                        ) {
                            Text(
                                text = player.status.label,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                color = statusFg,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard("Edad", "${player.age}", accent, onBg, Modifier.weight(1f))
                        StatCard("Posición", player.position.short, accent, onBg, Modifier.weight(1f))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard("Dorsal", "#${player.number}", accent, onBg, Modifier.weight(1f))
                        StatCard("Nivel", levelLabel(player.rating), accent, onBg, Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    accent: Color,
    onText: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = GlassBase.copy(alpha = 0.08f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                color = onText.copy(alpha = 0.60f),
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = value,
                color = accent,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private fun initials(name: String): String {
    val parts = name.trim().split(" ").filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> "??"
        parts.size == 1 -> parts.first().take(2).uppercase()
        else -> (parts[0].take(1) + parts[1].take(1)).uppercase()
    }
}

private fun levelLabel(rating: Int): String = when {
    rating >= 91 -> "Clase Mundial"
    rating >= 85 -> "Élite"
    rating >= 78 -> "Pro"
    rating >= 70 -> "Medio"
    else -> "Base"
}