package com.example.mvp.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mvp.ui.theme.ButtonTextDark
import com.example.mvp.ui.theme.GlassBase

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    username: String = "Usuario",
    onBack: () -> Unit = {},
    onOpenAccount: () -> Unit = {},
    onOpenPrivacy: () -> Unit = {},
    onOpenAbout: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val bgTop = MaterialTheme.colorScheme.background
    val bgMid = MaterialTheme.colorScheme.surface
    val accent = MaterialTheme.colorScheme.primary
    val accent2 = MaterialTheme.colorScheme.secondary
    val onBg = MaterialTheme.colorScheme.onBackground

    var notifications by remember { mutableStateOf(true) }
    var matchReminders by remember { mutableStateOf(true) }
    var trainingReminders by remember { mutableStateOf(false) }
    var biometric by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(bgTop, bgMid, bgTop)))
            .padding(horizontal = 20.dp)
    ) {
        Box(
            modifier = Modifier
                .size(240.dp)
                .align(Alignment.TopStart)
                .offset(x = (-70).dp, y = (-50).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(accent2.copy(alpha = 0.22f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 22.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                SettingsHeader(
                    username = username,
                    accent = accent,
                    accent2 = accent2,
                    onText = onBg,
                    onBack = onBack
                )
            }

            item { SectionTitle(title = "Preferencias", onText = onBg) }

            item {
                GlassCard {
                    SettingsSwitchRow(
                        title = "Notificaciones",
                        subtitle = "Alertas generales de la app",
                        icon = Icons.Default.Notifications,
                        accent = accent,
                        accent2 = accent2,
                        onText = onBg,
                        checked = notifications,
                        onCheckedChange = { notifications = it }
                    )
                    DividerGlass()
                    SettingsSwitchRow(
                        title = "Recordatorios de partidos",
                        subtitle = "Avisos antes de cada encuentro",
                        icon = Icons.Default.SportsSoccer,
                        accent = accent,
                        accent2 = accent2,
                        onText = onBg,
                        checked = matchReminders,
                        onCheckedChange = { matchReminders = it },
                        enabled = notifications
                    )
                    DividerGlass()
                    SettingsSwitchRow(
                        title = "Recordatorios de entrenamiento",
                        subtitle = "Sesiones planificadas y objetivos",
                        icon = Icons.Default.FitnessCenter,
                        accent = accent,
                        accent2 = accent2,
                        onText = onBg,
                        checked = trainingReminders,
                        onCheckedChange = { trainingReminders = it },
                        enabled = notifications
                    )
                }
            }

            item { SectionTitle(title = "Seguridad", onText = onBg) }

            item {
                GlassCard {
                    SettingsSwitchRow(
                        title = "Acceso biométrico",
                        subtitle = "Huella/Face ID para entrar",
                        icon = Icons.Default.Fingerprint,
                        accent = accent,
                        accent2 = accent2,
                        onText = onBg,
                        checked = biometric,
                        onCheckedChange = { biometric = it }
                    )
                    DividerGlass()
                    SettingsNavRow(
                        title = "Privacidad",
                        subtitle = "Permisos y datos",
                        icon = Icons.Default.Security,
                        accent = accent,
                        accent2 = accent2,
                        onText = onBg,
                        onClick = onOpenPrivacy
                    )
                }
            }

            item { SectionTitle(title = "Cuenta", onText = onBg) }

            item {
                GlassCard {
                    SettingsNavRow(
                        title = "Perfil y cuenta",
                        subtitle = "Nombre, email, preferencias",
                        icon = Icons.Default.Person,
                        accent = accent,
                        accent2 = accent2,
                        onText = onBg,
                        onClick = onOpenAccount
                    )
                    DividerGlass()
                    SettingsNavRow(
                        title = "Acerca de",
                        subtitle = "Versión, licencia y soporte",
                        icon = Icons.Default.Info,
                        accent = accent,
                        accent2 = accent2,
                        onText = onBg,
                        onClick = onOpenAbout,
                        trailingChip = "v1.0"
                    )
                }
            }

            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .clickable { onLogout() },
                    shape = RoundedCornerShape(22.dp),
                    color = GlassBase.copy(alpha = 0.08f)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            IconBubble(
                                icon = Icons.Default.Logout,
                                accent = accent,
                                accent2 = accent2
                            )
                            Column {
                                Text(
                                    text = "Cerrar sesión",
                                    color = onBg,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Salir de tu cuenta en este dispositivo",
                                    color = onBg.copy(alpha = 0.70f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = onBg.copy(alpha = 0.55f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsHeader(
    username: String,
    accent: Color,
    accent2: Color,
    onText: Color,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Surface(
            modifier = Modifier
                .size(44.dp)
                .clickable { onBack() },
            shape = CircleShape,
            color = GlassBase.copy(alpha = 0.10f),
            tonalElevation = 0.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            listOf(accent.copy(alpha = 0.35f), accent2.copy(alpha = 0.20f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = ButtonTextDark
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "Configuración",
                color = onText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Ajustes de la app · $username",
                color = onText.copy(alpha = 0.78f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun GlassCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = GlassBase.copy(alpha = 0.08f)
    ) {
        Column(modifier = Modifier.fillMaxWidth(), content = content)
    }
}

@Composable
private fun DividerGlass() {
    Divider(
        modifier = Modifier.padding(horizontal = 14.dp),
        color = Color.White.copy(alpha = 0.06f)
    )
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color,
    accent2: Color,
    onText: Color,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconBubble(icon = icon, accent = accent, accent2 = accent2)
            Column {
                Text(
                    text = title,
                    color = if (enabled) onText else onText.copy(alpha = 0.45f),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    color = if (enabled) onText.copy(alpha = 0.70f) else onText.copy(alpha = 0.35f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Switch(
            checked = checked,
            enabled = enabled,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun SettingsNavRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color,
    accent2: Color,
    onText: Color,
    trailingChip: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconBubble(icon = icon, accent = accent, accent2 = accent2)
            Column {
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
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (!trailingChip.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = accent.copy(alpha = 0.16f)
                ) {
                    Text(
                        text = trailingChip,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        color = accent,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = onText.copy(alpha = 0.55f)
            )
        }
    }
}

@Composable
private fun IconBubble(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color,
    accent2: Color
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