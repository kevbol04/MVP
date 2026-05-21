package com.example.mvp.ui.screens.club

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.filled.UploadFile
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Stadium
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mvp.R
import com.example.mvp.domain.model.Club
import com.example.mvp.domain.model.ClubBadgeDefaults
import com.example.mvp.ui.components.ClubBadgeImage
import com.example.mvp.ui.theme.ButtonTextDark
import com.example.mvp.ui.theme.GlassBase
import java.io.File

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
    val context = LocalContext.current

    var name by rememberSaveable { mutableStateOf("") }
    var stadium by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }
    var coachName by rememberSaveable { mutableStateOf("") }
    var badgeId by rememberSaveable { mutableStateOf(ClubBadgeDefaults.DEFAULT_ID) }
    var customBadgePath by rememberSaveable { mutableStateOf<String?>(null) }
    var badgeErrorText by rememberSaveable { mutableStateOf<String?>(null) }

    var nameTouched by rememberSaveable { mutableStateOf(false) }
    var stadiumTouched by rememberSaveable { mutableStateOf(false) }
    var cityTouched by rememberSaveable { mutableStateOf(false) }
    var coachTouched by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(club?.id, club?.coachName, club?.badgeId, club?.customBadgePath, fallbackCoachName) {
        name = club?.name.orEmpty()
        stadium = club?.stadium.orEmpty()
        city = club?.city.orEmpty()
        coachName = club?.coachName.orEmpty().ifBlank { fallbackCoachName }
        badgeId = ClubBadgeDefaults.sanitize(club?.badgeId.orEmpty())
        customBadgePath = club?.customBadgePath?.takeIf { it.isNotBlank() }
        badgeErrorText = null
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult

        if (!isRealPngImage(context, uri)) {
            badgeErrorText = "Solo se permiten imágenes PNG."
            return@rememberLauncherForActivityResult
        }

        val savedPath = saveClubBadgeToInternalStorage(context, uri)

        if (savedPath != null) {
            customBadgePath?.let { oldPath ->
                runCatching { File(oldPath).delete() }
            }

            customBadgePath = savedPath
            badgeId = ClubBadgeDefaults.CUSTOM_ID
            badgeErrorText = null
        } else {
            badgeErrorText = "No se pudo cargar la imagen seleccionada."
        }
    }

    val nameErrorText = when {
        name.isBlank() -> "El nombre del club es obligatorio"
        name.trim().length < 3 -> "El nombre debe tener al menos 3 caracteres"
        else -> null
    }

    val stadiumErrorText = when {
        stadium.isBlank() -> "El estadio es obligatorio"
        stadium.trim().length < 3 -> "El estadio debe tener al menos 3 caracteres"
        else -> null
    }

    val cityErrorText = when {
        city.isBlank() -> "La ciudad es obligatoria"
        city.trim().length < 2 -> "La ciudad debe tener al menos 2 caracteres"
        else -> null
    }

    val coachErrorText = when {
        coachName.isBlank() -> "El entrenador es obligatorio"
        coachName.trim().length < 2 -> "El entrenador debe tener al menos 2 caracteres"
        else -> null
    }

    val nameError = nameTouched && nameErrorText != null
    val stadiumError = stadiumTouched && stadiumErrorText != null
    val cityError = cityTouched && cityErrorText != null
    val coachError = coachTouched && coachErrorText != null

    val canSave = nameErrorText == null &&
            stadiumErrorText == null &&
            cityErrorText == null &&
            coachErrorText == null

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
                subtitle = if (isInitialSetup) "Elige identidad y empieza tu carrera" else "Datos deportivos principales",
                badgeId = badgeId,
                customBadgePath = customBadgePath,
                accent = accent,
                accent2 = accent2,
                onText = onBg,
                showBack = !isInitialSetup,
                onBack = onBack
            )

            ClubPreviewCard(
                name = name,
                stadium = stadium,
                city = city,
                coachName = coachName,
                badgeId = badgeId,
                customBadgePath = customBadgePath,
                accent = accent,
                accent2 = accent2,
                onText = onBg
            )

            SectionTitle("Escudo del club", onBg)

            BadgePicker(
                selectedBadgeId = badgeId,
                customBadgePath = customBadgePath,
                onSelected = { selectedId ->
                    badgeId = selectedId
                    customBadgePath = null
                    badgeErrorText = null
                },
                onPickCustom = {
                    imagePickerLauncher.launch(arrayOf("image/*"))
                },
                accent = accent,
                accent2 = accent2,
                onText = onBg
            )

            badgeErrorText?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, top = 2.dp)
                )
            }

            SectionTitle("Datos del club", onBg)

            PremiumFormPanel(onText = onBg, accent = accent) {
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
                    supportingText = if (nameError) nameErrorText else null
                )

                ClubTextField(
                    value = stadium,
                    onValueChange = {
                        stadium = it
                        stadiumTouched = true
                    },
                    label = "Estadio",
                    icon = Icons.Default.Stadium,
                    accent = accent,
                    onText = onBg,
                    isError = stadiumError,
                    supportingText = if (stadiumError) stadiumErrorText else null
                )

                ClubTextField(
                    value = city,
                    onValueChange = {
                        city = it
                        cityTouched = true
                    },
                    label = "Ciudad",
                    icon = Icons.Default.LocationCity,
                    accent = accent2,
                    onText = onBg,
                    isError = cityError,
                    supportingText = if (cityError) cityErrorText else null
                )

                ClubTextField(
                    value = coachName,
                    onValueChange = {
                        coachName = it
                        coachTouched = true
                    },
                    label = "Entrenador",
                    icon = Icons.Default.Person,
                    accent = accent,
                    onText = onBg,
                    isError = coachError,
                    supportingText = if (coachError) coachErrorText else null
                )
            }

            PremiumSaveButton(
                text = if (isInitialSetup) "Crear club" else "Guardar club",
                enabled = canSave,
                accent = accent,
                accent2 = accent2,
                onText = onBg,
                onClick = {
                    nameTouched = true
                    stadiumTouched = true
                    cityTouched = true
                    coachTouched = true

                    if (canSave) {
                        onSave(
                            Club(
                                id = club?.id ?: 0,
                                name = name.trim(),
                                stadium = stadium.trim(),
                                city = city.trim(),
                                coachName = coachName.trim(),
                                badgeId = badgeId,
                                customBadgePath = customBadgePath?.takeIf { it.isNotBlank() },
                                selectedFormationId = club?.selectedFormationId ?: Club.DEFAULT_FORMATION_ID
                            )
                        )
                    }
                }
            )

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
    customBadgePath: String?,
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
            customBadgePath = customBadgePath,
            size = 46.dp
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
    stadium: String,
    city: String,
    coachName: String,
    badgeId: String,
    customBadgePath: String?,
    accent: Color,
    accent2: Color,
    onText: Color
) {
    val shape = RoundedCornerShape(34.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(232.dp)
            .shadow(18.dp, shape, clip = false)
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF10273A),
                        Color(0xFF123B49),
                        Color(0xFF071623)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        accent.copy(alpha = 0.60f),
                        Color.White.copy(alpha = 0.10f),
                        accent2.copy(alpha = 0.42f)
                    )
                ),
                shape = shape
            )
    ) {
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.TopEnd)
                .offset(x = 84.dp, y = (-88).dp)
                .background(
                    Brush.radialGradient(
                        listOf(accent.copy(alpha = 0.45f), Color.Transparent)
                    ),
                    CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(220.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-88).dp, y = 88.dp)
                .background(
                    Brush.radialGradient(
                        listOf(accent2.copy(alpha = 0.28f), Color.Transparent)
                    ),
                    CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color.Black.copy(alpha = 0.18f))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(28.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    ClubBadgeEmblem(
                        badgeId = badgeId,
                        customBadgePath = customBadgePath,
                        size = 76.dp
                    )
                }

                Spacer(Modifier.size(16.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        text = name.ifBlank { "Mi club" },
                        color = onText,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = city.ifBlank { "Ciudad sin definir" },
                        color = onText.copy(alpha = 0.74f),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PremiumMiniInfoCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Stadium,
                    title = "ESTADIO",
                    value = stadium.ifBlank { "Sin definir" },
                    accent = accent,
                    onText = onText
                )
                PremiumMiniInfoCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Person,
                    title = "ENTRENADOR",
                    value = coachName.ifBlank { "Sin definir" },
                    accent = accent2,
                    onText = onText
                )
            }
        }
    }
}

@Composable
private fun PremiumMiniInfoCard(
    modifier: Modifier,
    icon: ImageVector,
    title: String,
    value: String,
    accent: Color,
    onText: Color
) {
    val shape = RoundedCornerShape(20.dp)

    Row(
        modifier = modifier
            .height(74.dp)
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.13f),
                        Color.White.copy(alpha = 0.055f)
                    )
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.10f), shape)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size(28.dp)
        )
        Spacer(Modifier.size(10.dp))
        Column {
            Text(
                text = title,
                color = accent,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                maxLines = 1
            )
            Text(
                text = value,
                color = onText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun BadgePicker(
    selectedBadgeId: String,
    customBadgePath: String?,
    onSelected: (String) -> Unit,
    onPickCustom: () -> Unit,
    accent: Color,
    accent2: Color,
    onText: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        val badges = remember { badgePresets() }
        val firstRow = remember { badges.take(2) }
        val restRows = remember { badges.drop(2).chunked(3) }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            CustomBadgeOption(
                modifier = Modifier.weight(1f),
                customBadgePath = customBadgePath,
                selected = customBadgePath != null,
                accent = accent,
                onText = onText,
                onClick = onPickCustom
            )

            firstRow.forEach { badge ->
                BadgeOption(
                    modifier = Modifier.weight(1f),
                    badge = badge,
                    selected = customBadgePath == null && ClubBadgeDefaults.sanitize(selectedBadgeId) == badge.id,
                    accent = accent,
                    accent2 = accent2,
                    onText = onText,
                    onClick = { onSelected(badge.id) }
                )
            }
        }

        restRows.forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                rowItems.forEach { badge ->
                    BadgeOption(
                        modifier = Modifier.weight(1f),
                        badge = badge,
                        selected = customBadgePath == null && ClubBadgeDefaults.sanitize(selectedBadgeId) == badge.id,
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
    val shape = RoundedCornerShape(26.dp)

    Box(
        modifier = modifier
            .height(106.dp)
            .shadow(
                elevation = if (selected) 14.dp else 4.dp,
                shape = shape,
                clip = false
            )
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = if (selected) {
                        listOf(accent.copy(alpha = 0.24f), GlassBase.copy(alpha = 0.14f))
                    } else {
                        listOf(GlassBase.copy(alpha = 0.11f), GlassBase.copy(alpha = 0.055f))
                    }
                )
            )
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) accent.copy(alpha = 0.95f) else onText.copy(alpha = 0.09f),
                shape = shape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.radialGradient(
                            listOf(accent.copy(alpha = 0.22f), Color.Transparent)
                        )
                    )
            )
        }

        ClubBadgeEmblem(
            badgeId = badge.id,
            customBadgePath = null,
            size = if (selected) 68.dp else 60.dp
        )

        if (selected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(accent),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = ButtonTextDark,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun CustomBadgeOption(
    modifier: Modifier,
    customBadgePath: String?,
    selected: Boolean,
    accent: Color,
    onText: Color,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(26.dp)

    Box(
        modifier = modifier
            .height(106.dp)
            .shadow(
                elevation = if (selected) 14.dp else 4.dp,
                shape = shape,
                clip = false
            )
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = if (selected) {
                        listOf(accent.copy(alpha = 0.24f), GlassBase.copy(alpha = 0.14f))
                    } else {
                        listOf(GlassBase.copy(alpha = 0.11f), GlassBase.copy(alpha = 0.055f))
                    }
                )
            )
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) accent.copy(alpha = 0.95f) else onText.copy(alpha = 0.09f),
                shape = shape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (customBadgePath != null) {
            ClubBadgeImage(
                badgeId = ClubBadgeDefaults.DEFAULT_ID,
                customBadgePath = customBadgePath,
                size = if (selected) 68.dp else 60.dp
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.UploadFile,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(30.dp)
                )

                Spacer(Modifier.height(7.dp))

                Text(
                    text = "Cargar imagen",
                    color = onText.copy(alpha = 0.90f),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Black,
                    maxLines = 1
                )
            }
        }

        if (selected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(accent),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = ButtonTextDark,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun ClubBadgeEmblem(
    badgeId: String,
    customBadgePath: String?,
    size: androidx.compose.ui.unit.Dp
) {
    ClubBadgeImage(
        badgeId = badgeId,
        customBadgePath = customBadgePath,
        size = size
    )
}

private data class ClubBadgePreset(
    val id: String,
    val name: String,
    val drawableRes: Int
)

private fun badgePresets(): List<ClubBadgePreset> = listOf(
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

private fun isRealPngImage(context: Context, uri: Uri): Boolean {
    val pngSignature = byteArrayOf(
        0x89.toByte(),
        0x50,
        0x4E,
        0x47,
        0x0D,
        0x0A,
        0x1A,
        0x0A
    )

    return runCatching {
        context.contentResolver.openInputStream(uri)?.use { input ->
            val header = ByteArray(8)
            val bytesRead = input.read(header)

            bytesRead == 8 && header.contentEquals(pngSignature)
        } ?: false
    }.getOrDefault(false)
}

private fun getFileNameFromUri(context: Context, uri: Uri): String? {
    return runCatching {
        context.contentResolver.query(
            uri,
            arrayOf(OpenableColumns.DISPLAY_NAME),
            null,
            null,
            null
        )?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)

            if (cursor.moveToFirst() && nameIndex >= 0) {
                cursor.getString(nameIndex)
            } else {
                null
            }
        }
    }.getOrNull()
}

private fun saveClubBadgeToInternalStorage(context: Context, uri: Uri): String? {
    return runCatching {
        val file = File(context.filesDir, "club_badge_${System.currentTimeMillis()}.png")

        context.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        } ?: return null

        file.absolutePath
    }.getOrNull()
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
private fun PremiumFormPanel(
    onText: Color,
    accent: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content
    )
}

@Composable
private fun PremiumSaveButton(
    text: String,
    enabled: Boolean,
    accent: Color,
    accent2: Color,
    onText: Color,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(18.dp)

    val backgroundBrush = if (enabled) {
        Brush.horizontalGradient(
            colors = listOf(
                accent.copy(alpha = 0.98f),
                accent2.copy(alpha = 0.92f)
            )
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(
                Color(0xFF1A3142).copy(alpha = 0.92f),
                Color(0xFF22364B).copy(alpha = 0.92f)
            )
        )
    }

    val borderColor = if (enabled) {
        Color.White.copy(alpha = 0.10f)
    } else {
        Color.White.copy(alpha = 0.06f)
    }

    val contentColor = if (enabled) {
        ButtonTextDark
    } else {
        onText.copy(alpha = 0.38f)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp, bottom = 8.dp)
            .height(56.dp)
            .clip(shape)
            .background(backgroundBrush)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = shape
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 18.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Save,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.size(10.dp))

        Text(
            text = text,
            color = contentColor,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
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
    val shape = RoundedCornerShape(18.dp)

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.06f),
                        Color.White.copy(alpha = 0.025f)
                    )
                )
            ),
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                fontWeight = FontWeight.Medium
            )
        },
        placeholder = if (placeholder.isNotBlank()) ({ Text(placeholder) }) else null,
        leadingIcon = {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent.copy(alpha = 0.13f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(21.dp)
                )
            }
        },
        trailingIcon = null,
        isError = isError,
        supportingText = supportingText?.let { text -> ({ Text(text) }) },
        singleLine = singleLine,
        minLines = minLines,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        shape = shape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = onText,
            unfocusedTextColor = onText,
            focusedBorderColor = accent,
            unfocusedBorderColor = onText.copy(alpha = 0.20f),
            focusedLabelColor = accent,
            unfocusedLabelColor = onText.copy(alpha = 0.58f),
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, start = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (title.contains("Escudo")) Icons.Default.SportsSoccer else Icons.Default.Flag,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(Modifier.size(10.dp))

        Text(
            text = title,
            color = onText,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black
        )
    }
}