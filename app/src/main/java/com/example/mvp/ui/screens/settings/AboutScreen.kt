package com.example.mvp.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.ui.unit.dp
import com.example.mvp.ui.theme.ButtonTextDark
import com.example.mvp.ui.theme.GlassBase

@Composable
fun AboutScreen(
    onBack: () -> Unit
) {
    val bgTop = MaterialTheme.colorScheme.background
    val bgMid = MaterialTheme.colorScheme.surface
    val accent = MaterialTheme.colorScheme.primary
    val accent2 = MaterialTheme.colorScheme.secondary
    val onBg = MaterialTheme.colorScheme.onBackground

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(bgTop, bgMid, bgTop)))
    ) {
        Box(
            modifier = Modifier
                .size(240.dp)
                .align(Alignment.TopEnd)
                .offset(x = 70.dp, y = (-60).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            accent.copy(alpha = 0.30f),
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
                .padding(top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AboutHeader(
                onBack = onBack,
                onText = onBg
            )

            HeroAboutCard(
                accent = accent,
                accent2 = accent2,
                onText = onBg
            )

            SectionCard(
                title = "Qué hace ProFootball",
                onText = onBg
            ) {
                FeatureRow(
                    icon = Icons.Default.Groups,
                    title = "Gestión de jugadores",
                    description = "Control de plantilla, posiciones, dorsales, estado físico y valoración."
                )

                FeatureRow(
                    icon = Icons.Default.FitnessCenter,
                    title = "Entrenamientos",
                    description = "Registro de sesiones, duración, tipo de trabajo y seguimiento de carga."
                )

                FeatureRow(
                    icon = Icons.Default.SportsSoccer,
                    title = "Partidos",
                    description = "Resultados, competición, goles y balance competitivo del equipo."
                )

                FeatureRow(
                    icon = Icons.Default.Analytics,
                    title = "Estadísticas",
                    description = "Análisis visual del rendimiento, plantilla, partidos y carga de trabajo."
                )
            }

            SectionCard(
                title = "Tecnologías principales",
                onText = onBg
            ) {
                TechChipGrid(
                    items = listOf(
                        "Kotlin",
                        "Jetpack Compose",
                        "Material 3",
                        "Room",
                        "Hilt",
                        "DataStore",
                        "StateFlow",
                        "MVVM"
                    )
                )
            }

            SectionCard(
                title = "Arquitectura y calidad",
                onText = onBg
            ) {
                QualityItem(
                    icon = Icons.Default.Storage,
                    title = "Datos locales",
                    text = "La información se almacena en el dispositivo mediante Room."
                )

                QualityItem(
                    icon = Icons.Default.Security,
                    title = "Sesión persistente",
                    text = "El usuario puede mantener la sesión iniciada usando DataStore."
                )

                QualityItem(
                    icon = Icons.Default.Verified,
                    title = "Login protegido",
                    text = "Las contraseñas no se guardan en texto plano y usan hash con salt."
                )

                QualityItem(
                    icon = Icons.Default.Code,
                    title = "Código estructurado",
                    text = "El proyecto está separado en capas de datos, dominio, navegación y UI."
                )
            }

            SectionCard(
                title = "Información del proyecto",
                onText = onBg
            ) {
                InfoLine(label = "Nombre", value = "ProFootball")
                InfoLine(label = "Versión", value = "1.0")
                InfoLine(label = "Tipo", value = "Aplicación de gestión deportiva")
                InfoLine(label = "Autor", value = "Kevin Bloufer")
            }
        }
    }
}

@Composable
private fun AboutHeader(
    onBack: () -> Unit,
    onText: Color
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

        Column {
            Text(
                text = "Acerca de",
                color = onText,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Información del proyecto",
                color = onText.copy(alpha = 0.65f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun HeroAboutCard(
    accent: Color,
    accent2: Color,
    onText: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        color = GlassBase.copy(alpha = 0.08f),
        tonalElevation = 2.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            accent.copy(alpha = 0.22f),
                            accent2.copy(alpha = 0.14f),
                            GlassBase.copy(alpha = 0.06f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 38.dp, y = (-42).dp)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                accent.copy(alpha = 0.20f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    accent.copy(alpha = 0.75f),
                                    accent2.copy(alpha = 0.65f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsSoccer,
                        contentDescription = null,
                        tint = ButtonTextDark,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Text(
                    text = "ProFootball",
                    color = onText,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Una app para centralizar la gestión deportiva de un equipo: plantilla, entrenamientos, partidos y estadísticas en un único lugar.",
                    color = onText.copy(alpha = 0.76f),
                    style = MaterialTheme.typography.bodyMedium
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HeroChip("Local-first", accent, onText)
                    HeroChip("Compose UI", accent2, onText)
                }
            }
        }
    }
}

@Composable
private fun HeroChip(
    text: String,
    color: Color,
    onText: Color
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = color.copy(alpha = 0.16f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            color = onText,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun SectionCard(
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
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
private fun FeatureRow(
    icon: ImageVector,
    title: String,
    description: String
) {
    val accent = MaterialTheme.colorScheme.primary
    val accent2 = MaterialTheme.colorScheme.secondary
    val onBg = MaterialTheme.colorScheme.onBackground

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            accent.copy(alpha = 0.34f),
                            accent2.copy(alpha = 0.28f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ButtonTextDark,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.size(12.dp))

        Column {
            Text(
                text = title,
                color = onBg,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = description,
                color = onBg.copy(alpha = 0.68f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun TechChipGrid(
    items: List<String>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { item ->
                    TechChip(
                        text = item,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun TechChip(
    text: String,
    modifier: Modifier = Modifier
) {
    val accent = MaterialTheme.colorScheme.primary
    val onBg = MaterialTheme.colorScheme.onBackground

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
        color = accent.copy(alpha = 0.13f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = onBg,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun QualityItem(
    icon: ImageVector,
    title: String,
    text: String
) {
    val accent = MaterialTheme.colorScheme.primary
    val onBg = MaterialTheme.colorScheme.onBackground

    Row(
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.size(12.dp))

        Column {
            Text(
                text = title,
                color = onBg,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = text,
                color = onBg.copy(alpha = 0.68f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun InfoLine(
    label: String,
    value: String
) {
    val onBg = MaterialTheme.colorScheme.onBackground

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = onBg.copy(alpha = 0.62f),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            color = onBg,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}