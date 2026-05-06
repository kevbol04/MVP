package com.example.mvp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mvp.ui.theme.GlassBase

enum class BottomBarDestination {
    Training,
    Players,
    Dashboard,
    Matches,
    Stats
}

@Composable
fun ProFootballBottomBar(
    selected: BottomBarDestination,
    onSelect: (BottomBarDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = MaterialTheme.colorScheme.primary
    val accent2 = MaterialTheme.colorScheme.secondary
    val onBg = MaterialTheme.colorScheme.onBackground

    Box(
        modifier = modifier.height(84.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(24.dp),
            color = GlassBase.copy(alpha = 0.10f),
            tonalElevation = 2.dp,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                accent.copy(alpha = 0.08f),
                                accent2.copy(alpha = 0.05f),
                                accent.copy(alpha = 0.06f)
                            )
                        )
                    )
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomSideItem(
                    label = "Entr",
                    icon = Icons.Default.FitnessCenter,
                    selected = selected == BottomBarDestination.Training,
                    onClick = { onSelect(BottomBarDestination.Training) },
                    accent = accent,
                    accent2 = accent2,
                    onText = onBg
                )

                BottomSideItem(
                    label = "Jug",
                    icon = Icons.Default.Groups,
                    selected = selected == BottomBarDestination.Players,
                    onClick = { onSelect(BottomBarDestination.Players) },
                    accent = accent,
                    accent2 = accent2,
                    onText = onBg
                )

                Spacer(modifier = Modifier.width(64.dp))

                BottomSideItem(
                    label = "Part",
                    icon = Icons.Default.SportsSoccer,
                    selected = selected == BottomBarDestination.Matches,
                    onClick = { onSelect(BottomBarDestination.Matches) },
                    accent = accent,
                    accent2 = accent2,
                    onText = onBg
                )

                BottomSideItem(
                    label = "Est",
                    icon = Icons.Default.BarChart,
                    selected = selected == BottomBarDestination.Stats,
                    onClick = { onSelect(BottomBarDestination.Stats) },
                    accent = accent,
                    accent2 = accent2,
                    onText = onBg
                )
            }
        }

        CenterDashboardButton(
            selected = selected == BottomBarDestination.Dashboard,
            onClick = { onSelect(BottomBarDestination.Dashboard) },
            accent = accent,
            accent2 = accent2,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-1).dp)
        )
    }
}

@Composable
private fun RowScope.BottomSideItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    accent: Color,
    accent2: Color,
    onText: Color
) {
    val tint = if (selected) {
        onText
    } else {
        onText.copy(alpha = 0.74f)
    }

    Surface(
        modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .padding(horizontal = 3.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (selected) GlassBase.copy(alpha = 0.08f) else Color.Transparent,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (selected) {
                        Brush.horizontalGradient(
                            listOf(
                                accent.copy(alpha = 0.18f),
                                accent2.copy(alpha = 0.12f)
                            )
                        )
                    } else {
                        Brush.horizontalGradient(
                            listOf(Color.Transparent, Color.Transparent)
                        )
                    }
                )
                .padding(horizontal = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = label,
                color = tint,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun CenterDashboardButton(
    selected: Boolean,
    onClick: () -> Unit,
    accent: Color,
    accent2: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(66.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(
                        accent2.copy(alpha = if (selected) 0.70f else 0.52f),
                        accent.copy(alpha = if (selected) 0.62f else 0.45f),
                        Color.Transparent
                    )
                )
            )
            .padding(5.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            accent.copy(alpha = if (selected) 0.82f else 0.68f),
                            accent2.copy(alpha = if (selected) 0.72f else 0.58f),
                            GlassBase.copy(alpha = 0.12f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Inicio",
                tint = Color.White,
                modifier = Modifier.size(27.dp)
            )
        }
    }
}