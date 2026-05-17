package com.example.mvp.ui.screens.club

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Stadium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mvp.R
import com.example.mvp.domain.model.Club
import com.example.mvp.domain.model.ClubBadgeDefaults
import com.example.mvp.ui.theme.ButtonTextDark
import com.example.mvp.ui.theme.GlassBase

@Composable
fun ClubScreen(
    modifier: Modifier = Modifier,
    club: Club?,
    defaultCoachName: String = "",
    isInitialSetup: Boolean = false,
    onBack: () -> Unit = {},
    onCancelInitialSetup: () -> Unit = {},
    onSave: (Club) -> Unit = {}
) {
    val bgTop = MaterialTheme.colorScheme.background
    val bgMid = MaterialTheme.colorScheme.surface
    val accent = MaterialTheme.colorScheme.primary
    val accent2 = MaterialTheme.colorScheme.secondary
    val onBg = MaterialTheme.colorScheme.onBackground

    val fallbackCoachName = defaultCoachName.trim().ifBlank { "Usuario" }

    var name by rememberSaveable { mutableStateOf("") }
    var season by rememberSaveable { mutableStateOf("") }
    var stadium by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }
    var coachName by rememberSaveable { mutableStateOf("") }
    var badgeId by rememberSaveable { mutableStateOf(ClubBadgeDefaults.DEFAULT_ID) }
    var nameTouched by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(club?.id, club?.coachName, club?.badgeId, fallbackCoachName) {
        name = club?.name.orEmpty()
        season = club?.season.orEmpty().ifBlank { "2025/2026" }
        stadium = club?.stadium.orEmpty()
        city = club?.city.orEmpty()
        coachName = club?.coachName.orEmpty().ifBlank { fallbackCoachName }
        badgeId = ClubBadgeDefaults.sanitize(club?.badgeId.orEmpty())
    }

    val nameError = nameTouched && name.isBlank()
    val canSave = name.isNotBlank()

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
                .offset(x = 80.dp, y = (-55).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(accent.copy(alpha = 0.28f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 26.dp, bottom = 26.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ClubHeader(
                clubName = if (isInitialSetup) "Crea tu club" else name.ifBlank { "Configurar club" },
                subtitle = if (isInitialSetup) "Elige identidad y empieza tu carrera" else season.ifBlank { "Datos deportivos principales" },
                badgeId = badgeId,
                accent = accent,
                accent2 = accent2,
                onText = onBg,
                showBack = !isInitialSetup,
                onBack = onBack
            )

            ClubPreviewCard(
                name = name,
                season = season,
                stadium = stadium,
                city = city,
                coachName = coachName,
                badgeId = badgeId,
                accent = accent,
                accent2 = accent2,
                onText = onBg
            )

            SectionTitle("Escudo del club", onBg)

            BadgePicker(
                selectedBadgeId = badgeId,
                onSelected = { badgeId = it },
                accent = accent,
                accent2 = accent2,
                onText = onBg
            )

            SectionTitle("Datos del club", onBg)

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.24f),
                tonalElevation = 0.dp,
                shadowElevation = 0.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ClubTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            nameTouched = true
                        },
                        label = "Nombre del club",
                        icon = Icons.Default.SportsSoccer,
                        accent = accent,
                        onText = onBg,
                        isError = nameError,
                        supportingText = if (nameError) "El nombre del club es obligatorio" else null
                    )
                    ClubTextField(
                        value = season,
                        onValueChange = { season = it },
                        label = "Temporada",
                        icon = Icons.Default.Flag,
                        accent = accent2,
                        onText = onBg,
                        placeholder = "2025/2026"
                    )
                    ClubTextField(
                        value = stadium,
                        onValueChange = { stadium = it },
                        label = "Estadio",
                        icon = Icons.Default.Stadium,
                        accent = accent,
                        onText = onBg
                    )
                    ClubTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = "Ciudad",
                        icon = Icons.Default.LocationCity,
                        accent = accent2,
                        onText = onBg
                    )
                    ClubTextField(
                        value = coachName,
                        onValueChange = { coachName = it },
                        label = "Entrenador",
                        icon = Icons.Default.SportsSoccer,
                        accent = accent,
                        onText = onBg
                    )
                }
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = canSave,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accent,
                    contentColor = ButtonTextDark,
                    disabledContainerColor = GlassBase.copy(alpha = 0.14f),
                    disabledContentColor = onBg.copy(alpha = 0.45f)
                ),
                onClick = {
                    nameTouched = true
                    if (canSave) {
                        onSave(
                            Club(
                                id = club?.id ?: 0,
                                name = name.trim(),
                                season = season.trim(),
                                stadium = stadium.trim(),
                                city = city.trim(),
                                coachName = coachName.trim().ifBlank { fallbackCoachName },
                                badgeId = badgeId
                            )
                        )
                    }
                }
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(Modifier.size(10.dp))
                Text(
                    text = if (isInitialSetup) "Crear club" else "Guardar club",
                    fontWeight = FontWeight.Black
                )
            }

            if (isInitialSetup) {
                Text(
                    text = "Volver al inicio de sesión",
                    color = onBg.copy(alpha = 0.76f),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clip(RoundedCornerShape(999.dp))
                        .clickable(onClick = onCancelInitialSetup)
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun ClubHeader(
    clubName: String,
    subtitle: String,
    badgeId: String,
    accent: Color,
    accent2: Color,
    onText: Color,
    showBack: Boolean,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showBack) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = onText)
            }
        }
        ClubBadgeEmblem(
            badgeId = badgeId,
            size = 46.dp,
            accent = accent,
            accent2 = accent2
        )
        Spacer(Modifier.size(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = clubName,
                color = onText,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                color = onText.copy(alpha = 0.68f),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ClubPreviewCard(
    name: String,
    season: String,
    stadium: String,
    city: String,
    coachName: String,
    badgeId: String,
    accent: Color,
    accent2: Color,
    onText: Color
) {
    val shape = RoundedCornerShape(30.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        GlassBase.copy(alpha = 0.15f),
                        GlassBase.copy(alpha = 0.09f)
                    )
                )
            )
            .border(1.dp, onText.copy(alpha = 0.06f), shape)
            .padding(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ClubBadgeEmblem(
                    badgeId = badgeId,
                    size = 54.dp,
                    accent = accent,
                    accent2 = accent2
                )
                Spacer(Modifier.size(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = name.ifBlank { "Mi club" },
                        color = onText,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = listOf(season, city).filter { it.isNotBlank() }.joinToString(" · ").ifBlank { "Temporada y ciudad sin definir" },
                        color = onText.copy(alpha = 0.70f),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MiniInfoChip(
                    modifier = Modifier.weight(1f),
                    title = "Estadio",
                    value = stadium.ifBlank { "Sin definir" },
                    accent = accent,
                    onText = onText
                )
                MiniInfoChip(
                    modifier = Modifier.weight(1f),
                    title = "Entrenador",
                    value = coachName.ifBlank { "Sin definir" },
                    accent = accent2,
                    onText = onText
                )
            }
        }
    }
}

@Composable
private fun BadgePicker(
    selectedBadgeId: String,
    onSelected: (String) -> Unit,
    accent: Color,
    accent2: Color,
    onText: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        val rows = remember { badgePresets().chunked(3) }
        rows.forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                rowItems.forEach { badge ->
                    BadgeOption(
                        modifier = Modifier.weight(1f),
                        badge = badge,
                        selected = ClubBadgeDefaults.sanitize(selectedBadgeId) == badge.id,
                        accent = accent,
                        accent2 = accent2,
                        onText = onText,
                        onClick = { onSelected(badge.id) }
                    )
                }
                repeat(3 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun BadgeOption(
    modifier: Modifier,
    badge: ClubBadgePreset,
    selected: Boolean,
    accent: Color,
    accent2: Color,
    onText: Color,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(22.dp)
    Box(
        modifier = modifier
            .height(86.dp)
            .clip(shape)
            .background(if (selected) accent.copy(alpha = 0.12f) else GlassBase.copy(alpha = 0.055f))
            .border(
                width = 1.dp,
                color = if (selected) accent.copy(alpha = 0.82f) else onText.copy(alpha = 0.08f),
                shape = shape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        ClubBadgeEmblem(
            badgeId = badge.id,
            size = 52.dp,
            accent = accent,
            accent2 = accent2
        )
    }
}

@Composable
private fun ClubBadgeEmblem(
    badgeId: String,
    size: androidx.compose.ui.unit.Dp,
    accent: Color,
    accent2: Color
) {
    val badge = badgePresetById(badgeId)

    Image(
        painter = painterResource(id = badge.drawableRes),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier.size(size)
    )
}

private data class ClubBadgePreset(
    val id: String,
    val name: String,
    val drawableRes: Int
)

private fun badgePresets(): List<ClubBadgePreset> = listOf(
    ClubBadgePreset("royal_blue", "Clásico", R.drawable.club_badge_classic_gold),
    ClubBadgePreset("galaxy_purple", "Estrella", R.drawable.club_badge_silver_star),
    ClubBadgePreset("ocean_cyan", "Morado", R.drawable.club_badge_purple_ball),
    ClubBadgePreset("green_star", "Estadio", R.drawable.club_badge_blue_stadium),
    ClubBadgePreset("fire_red", "Ciudad", R.drawable.club_badge_green_city),
    ClubBadgePreset("gold_crown", "Corona", R.drawable.club_badge_orange_crown)
)

private fun badgePresetById(id: String): ClubBadgePreset {
    val cleanId = ClubBadgeDefaults.sanitize(id)
    return badgePresets().firstOrNull { it.id == cleanId } ?: badgePresets().first()
}

@Composable
private fun MiniInfoChip(
    modifier: Modifier,
    title: String,
    value: String,
    accent: Color,
    onText: Color
) {
    val shape = RoundedCornerShape(18.dp)

    Column(
        modifier = modifier
            .clip(shape)
            .background(GlassBase.copy(alpha = 0.10f))
            .border(1.dp, onText.copy(alpha = 0.05f), shape)
            .padding(12.dp)
    ) {
        Text(
            text = title,
            color = accent,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            color = onText,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ClubTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    accent: Color,
    onText: Color,
    placeholder: String = "",
    isError: Boolean = false,
    supportingText: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = if (placeholder.isNotBlank()) ({ Text(placeholder) }) else null,
        leadingIcon = {
            Icon(icon, contentDescription = null, tint = accent)
        },
        isError = isError,
        supportingText = supportingText?.let { text -> ({ Text(text) }) },
        singleLine = singleLine,
        minLines = minLines,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = onText,
            unfocusedTextColor = onText,
            focusedBorderColor = accent,
            unfocusedBorderColor = onText.copy(alpha = 0.22f),
            focusedLabelColor = accent,
            unfocusedLabelColor = onText.copy(alpha = 0.62f),
            cursorColor = accent,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        )
    )
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