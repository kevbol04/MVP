package com.example.mvp.ui.screens.matches

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mvp.ui.theme.Draw
import com.example.mvp.ui.theme.GlassBase
import com.example.mvp.ui.theme.Loss
import com.example.mvp.ui.theme.Win

@Composable
fun MatchDetailScreen(
    match: Match,
    onBack: () -> Unit,
    onEdit: (Match) -> Unit
) {
    val bgTop = MaterialTheme.colorScheme.background
    val bgMid = MaterialTheme.colorScheme.surface
    val accent = MaterialTheme.colorScheme.primary
    val accent2 = MaterialTheme.colorScheme.secondary
    val onBg = MaterialTheme.colorScheme.onBackground

    val resultColor = when (match.result) {
        MatchResult.VICTORIA -> Win
        MatchResult.EMPATE -> Draw
        MatchResult.DERROTA -> Loss
    }

    val goalDifference = match.goalsFor - match.goalsAgainst

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(bgTop, bgMid, bgTop)))
    ) {
        Box(
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.TopEnd)
                .offset(x = 80.dp, y = (-70).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            resultColor.copy(alpha = 0.24f),
                            Color.Transparent
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-80).dp, y = 70.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            accent2.copy(alpha = 0.16f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Header(
                onBack = onBack,
                onEdit = { onEdit(match) },
                onText = onBg,
                accent = accent
            )

            ScoreHeroCard(
                match = match,
                resultColor = resultColor,
                accent = accent,
                accent2 = accent2,
                onText = onBg
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MatchInfoCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.SportsSoccer,
                    title = "A favor",
                    value = match.goalsFor.toString(),
                    accent = accent,
                    accent2 = accent2,
                    onText = onBg
                )

                MatchInfoCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.SportsSoccer,
                    title = "En contra",
                    value = match.goalsAgainst.toString(),
                    accent = accent,
                    accent2 = accent2,
                    onText = onBg
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MatchInfoCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.WorkspacePremium,
                    title = "Diferencia",
                    value = if (goalDifference > 0) "+$goalDifference" else goalDifference.toString(),
                    accent = resultColor,
                    accent2 = accent2,
                    onText = onBg
                )

                MatchInfoCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Timer,
                    title = "Duración",
                    value = "90’",
                    accent = accent,
                    accent2 = accent2,
                    onText = onBg
                )
            }

            DetailSection(
                title = "Información del encuentro",
                onText = onBg
            ) {
                DetailLine(
                    label = "Rival",
                    value = match.rival,
                    onText = onBg
                )

                DetailLine(
                    label = "Fecha",
                    value = match.dateText,
                    onText = onBg
                )

                DetailLine(
                    label = "Competición",
                    value = match.competition.label,
                    onText = onBg
                )

                DetailLine(
                    label = "Resultado",
                    value = match.result.label,
                    valueColor = resultColor,
                    onText = onBg
                )
            }
        }
    }
}

@Composable
private fun Header(
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onText: Color,
    accent: Color
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

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Detalle del partido",
                color = onText,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Resumen completo del encuentro",
                color = onText.copy(alpha = 0.65f),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Surface(
            modifier = Modifier
                .size(44.dp)
                .clickable { onEdit() },
            shape = CircleShape,
            color = accent.copy(alpha = 0.14f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar",
                    tint = accent
                )
            }
        }
    }
}

@Composable
private fun ScoreHeroCard(
    match: Match,
    resultColor: Color,
    accent: Color,
    accent2: Color,
    onText: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        color = GlassBase.copy(alpha = 0.08f),
        tonalElevation = 2.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            resultColor.copy(alpha = 0.18f),
                            accent.copy(alpha = 0.10f),
                            accent2.copy(alpha = 0.08f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 42.dp, y = (-44).dp)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                resultColor.copy(alpha = 0.18f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = resultColor.copy(alpha = 0.16f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = resultColor,
                            modifier = Modifier.size(18.dp)
                        )

                        Text(
                            text = match.result.label,
                            color = resultColor,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Text(
                    text = "vs ${match.rival}",
                    color = onText,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "${match.competition.label} · ${match.dateText}",
                    color = onText.copy(alpha = 0.68f),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = GlassBase.copy(alpha = 0.08f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 28.dp, vertical = 18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = match.goalsFor.toString(),
                            color = onText,
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Black
                        )

                        Text(
                            text = "  -  ",
                            color = onText.copy(alpha = 0.45f),
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = match.goalsAgainst.toString(),
                            color = onText,
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchInfoCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    value: String,
    accent: Color,
    accent2: Color,
    onText: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        color = GlassBase.copy(alpha = 0.06f)
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                accent.copy(alpha = 0.34f),
                                accent2.copy(alpha = 0.22f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = title,
                color = onText.copy(alpha = 0.68f),
                style = MaterialTheme.typography.labelLarge
            )

            Text(
                text = value,
                color = onText,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun DetailSection(
    title: String,
    onText: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = GlassBase.copy(alpha = 0.06f)
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
private fun DetailLine(
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
            color = onText.copy(alpha = 0.64f),
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