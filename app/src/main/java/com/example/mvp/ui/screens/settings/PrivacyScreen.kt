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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.VisibilityOff
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
fun PrivacyScreen(
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
                .size(260.dp)
                .align(Alignment.TopEnd)
                .offset(x = 80.dp, y = (-70).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            accent.copy(alpha = 0.28f),
                            Color.Transparent
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-70).dp, y = 60.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            accent2.copy(alpha = 0.18f),
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
            PrivacyHeader(
                onBack = onBack,
                onText = onBg
            )

            PrivacyHeroCard(
                accent = accent,
                accent2 = accent2,
                onText = onBg
            )

            PrivacyStatusRow(
                accent = accent,
                accent2 = accent2,
                onText = onBg
            )

            SectionCard(
                title = "Cómo se gestionan tus datos",
                onText = onBg
            ) {
                PrivacyItem(
                    icon = Icons.Default.PhoneAndroid,
                    title = "Datos en tu dispositivo",
                    description = "La información de jugadores, partidos y entrenamientos se guarda localmente en el móvil."
                )

                PrivacyItem(
                    icon = Icons.Default.Storage,
                    title = "Base de datos local",
                    description = "La app usa Room para organizar los datos sin depender de servidores externos."
                )

                PrivacyItem(
                    icon = Icons.Default.VisibilityOff,
                    title = "Sin publicación externa",
                    description = "ProFootball no comparte tus registros deportivos con servicios online."
                )
            }

            SectionCard(
                title = "Seguridad de la cuenta",
                onText = onBg
            ) {
                PrivacyItem(
                    icon = Icons.Default.Lock,
                    title = "Contraseña protegida",
                    description = "La contraseña no se guarda como texto plano. Se almacena mediante hash seguro con salt."
                )

                PrivacyItem(
                    icon = Icons.Default.Security,
                    title = "Sesión persistente",
                    description = "La sesión se mantiene usando DataStore para evitar iniciar sesión continuamente."
                )

                PrivacyItem(
                    icon = Icons.Default.DeleteOutline,
                    title = "Control de la cuenta",
                    description = "Puedes cerrar sesión o eliminar tu cuenta desde la pantalla de cuenta cuando lo necesites."
                )
            }

            SectionCard(
                title = "Resumen de privacidad",
                onText = onBg
            ) {
                PrivacySummaryLine(
                    label = "Almacenamiento",
                    value = "Local"
                )

                PrivacySummaryLine(
                    label = "Servidor externo",
                    value = "No utilizado"
                )

                PrivacySummaryLine(
                    label = "Contraseña visible",
                    value = "No"
                )

                PrivacySummaryLine(
                    label = "Sesión guardada",
                    value = "Sí"
                )
            }

            SmallNoticeCard(
                accent = accent,
                onText = onBg
            )
        }
    }
}

@Composable
private fun PrivacyHeader(
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
                text = "Privacidad",
                color = onText,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Control y seguridad de tus datos",
                color = onText.copy(alpha = 0.65f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun PrivacyHeroCard(
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
                            accent.copy(alpha = 0.24f),
                            accent2.copy(alpha = 0.16f),
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
                    .offset(x = 38.dp, y = (-46).dp)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                accent.copy(alpha = 0.22f),
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
                        .size(62.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    accent.copy(alpha = 0.72f),
                                    accent2.copy(alpha = 0.62f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    text = "Tus datos están bajo control",
                    color = onText,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "ProFootball está diseñada para funcionar de forma local, manteniendo la información del equipo dentro del dispositivo.",
                    color = onText.copy(alpha = 0.76f),
                    style = MaterialTheme.typography.bodyMedium
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PrivacyChip(
                        text = "Local",
                        color = accent,
                        onText = onText
                    )

                    PrivacyChip(
                        text = "Hash + salt",
                        color = accent2,
                        onText = onText
                    )
                }
            }
        }
    }
}

@Composable
private fun PrivacyStatusRow(
    accent: Color,
    accent2: Color,
    onText: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatusMiniCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Storage,
            title = "Local",
            subtitle = "Datos guardados",
            accent = accent,
            accent2 = accent2,
            onText = onText
        )

        StatusMiniCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.PrivacyTip,
            title = "Seguro",
            subtitle = "Cuenta protegida",
            accent = accent,
            accent2 = accent2,
            onText = onText
        )
    }
}

@Composable
private fun StatusMiniCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String,
    accent: Color,
    accent2: Color,
    onText: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        color = GlassBase.copy(alpha = 0.06f)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                accent.copy(alpha = 0.32f),
                                accent2.copy(alpha = 0.24f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.size(10.dp))

            Column {
                Text(
                    text = title,
                    color = onText,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = subtitle,
                    color = onText.copy(alpha = 0.62f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
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
private fun PrivacyItem(
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
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            accent.copy(alpha = 0.30f),
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
private fun PrivacyChip(
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
private fun PrivacySummaryLine(
    label: String,
    value: String
) {
    val onBg = MaterialTheme.colorScheme.onBackground
    val accent = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = onBg.copy(alpha = 0.64f),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )

        Surface(
            shape = RoundedCornerShape(999.dp),
            color = accent.copy(alpha = 0.13f)
        ) {
            Text(
                text = value,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                color = onBg,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun SmallNoticeCard(
    accent: Color,
    onText: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = accent.copy(alpha = 0.10f)
    ) {
        Text(
            text = "Puedes gestionar tus datos desde la sección de cuenta. Si eliminas tu cuenta, los registros asociados dejarán de estar disponibles.",
            modifier = Modifier.padding(14.dp),
            color = onText.copy(alpha = 0.76f),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}