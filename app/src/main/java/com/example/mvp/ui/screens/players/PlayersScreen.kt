package com.example.mvp.ui.screens.players

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

enum class PlayerPosition(val short: String, val label: String) {
    POR("POR", "Portero"),
    DEF("DEF", "Defensa"),
    MED("MED", "Mediocentro"),
    DEL("DEL", "Delantero")
}

enum class PlayerStatus(val label: String) {
    TITULAR("Titular"),
    SUPLENTE("Suplente"),
    LESIONADO("Lesionado")
}

data class Player(
    val id: Int,
    val name: String,
    val position: PlayerPosition,
    val age: Int,
    val number: Int,
    val rating: Int,
    val status: PlayerStatus
)

private enum class BottomTab {
    Training, Matches, Players, Stats
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayersScreen(
    modifier: Modifier = Modifier,
    players: List<Player> = emptyList(),
    onBack: () -> Unit = {},
    onCreatePlayer: () -> Unit = {},
    onEditPlayer: (Player) -> Unit = {},
    onOpenPlayer: (Player) -> Unit = {},

    onDeletePlayer: (Player) -> Unit = {},

    onGoTraining: () -> Unit = {},
    onGoMatches: () -> Unit = {},
    onGoStats: () -> Unit = {}
) {
    val bgTop = MaterialTheme.colorScheme.background
    val bgMid = MaterialTheme.colorScheme.surface
    val accent = MaterialTheme.colorScheme.primary
    val accent2 = MaterialTheme.colorScheme.secondary
    val onBg = MaterialTheme.colorScheme.onBackground

    var query by remember { mutableStateOf("") }
    var selectedPos by remember { mutableStateOf<PlayerPosition?>(null) }

    val filtered = remember(players, query, selectedPos) {
        players.filter { p ->
            val matchesQuery =
                query.isBlank() ||
                        p.name.contains(query, ignoreCase = true) ||
                        p.position.short.contains(query, ignoreCase = true) ||
                        p.position.label.contains(query, ignoreCase = true)

            val matchesPos = selectedPos == null || p.position == selectedPos
            matchesQuery && matchesPos
        }
    }

    var selectedTab by remember { mutableStateOf(BottomTab.Players) }
    val bottomBarHeight = 78.dp

    var pendingDelete by remember { mutableStateOf<Player?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreatePlayer,
                containerColor = Color.Transparent,
                modifier = Modifier.offset(y = (-75).dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Brush.horizontalGradient(listOf(accent, accent2))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir", tint = ButtonTextDark)
                }
            }
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(bgTop, bgMid, bgTop)))
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .padding(innerPadding)
                    .padding(top = 18.dp, bottom = bottomBarHeight + 14.dp),
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

                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "Jugadores",
                            color = onBg,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Plantilla · ${players.size} jugadores",
                            color = onBg.copy(alpha = 0.70f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = GlassBase.copy(alpha = 0.07f)
                ) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        placeholder = { Text("Buscar jugador, posición...") },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent,
                            unfocusedTextColor = onBg,
                            focusedTextColor = onBg,
                            unfocusedLeadingIconColor = onBg.copy(alpha = 0.6f),
                            focusedLeadingIconColor = accent,
                            cursorColor = accent,
                            unfocusedPlaceholderColor = onBg.copy(alpha = 0.45f),
                            focusedPlaceholderColor = onBg.copy(alpha = 0.45f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                PositionChipsRow(
                    accent = accent,
                    onText = onBg,
                    selected = selectedPos,
                    onSelect = { selectedPos = it }
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtered, key = { it.id }) { player ->
                        PlayerBadgeCard(
                            player = player,
                            accent = accent,
                            accent2 = accent2,
                            onText = onBg,
                            onOpen = { onOpenPlayer(player) },
                            onEdit = { onEditPlayer(player) },
                            onDelete = { pendingDelete = player }
                        )
                    }
                }
            }

            BottomMenuBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                accent = accent,
                accent2 = accent2,
                onText = onBg,
                selected = selectedTab,
                onSelect = { tab ->
                    when (tab) {
                        BottomTab.Players -> selectedTab = BottomTab.Players
                        BottomTab.Training -> { selectedTab = BottomTab.Training; onGoTraining() }
                        BottomTab.Matches -> { selectedTab = BottomTab.Matches; onGoMatches() }
                        BottomTab.Stats -> { selectedTab = BottomTab.Stats; onGoStats() }
                    }
                }
            )
        }
    }

    if (pendingDelete != null) {
        val p = pendingDelete!!
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
            tonalElevation = 6.dp,
            shape = RoundedCornerShape(24.dp),
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            title = { Text("Eliminar jugador", fontWeight = FontWeight.SemiBold) },
            text = { Text("Se eliminará a ${p.name}. ¿Deseas continuar?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeletePlayer(p)
                        pendingDelete = null
                    }
                ) {
                    Text(
                        "Eliminar",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }
}

@Composable
private fun BottomMenuBar(
    modifier: Modifier = Modifier,
    accent: Color,
    accent2: Color,
    onText: Color,
    selected: BottomTab,
    onSelect: (BottomTab) -> Unit
) {
    Surface(
        modifier = modifier.height(64.dp),
        shape = RoundedCornerShape(22.dp),
        color = GlassBase.copy(alpha = 0.10f),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            accent.copy(alpha = 0.10f),
                            accent2.copy(alpha = 0.08f)
                        )
                    )
                )
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomMenuItem("Entr", Icons.Default.FitnessCenter, selected == BottomTab.Training, accent, accent2, onText) {
                onSelect(BottomTab.Training)
            }
            BottomMenuItem("Part", Icons.Default.SportsSoccer, selected == BottomTab.Matches, accent, accent2, onText) {
                onSelect(BottomTab.Matches)
            }
            BottomMenuItem("Jug", Icons.Default.Groups, selected == BottomTab.Players, accent, accent2, onText) {
                onSelect(BottomTab.Players)
            }
            BottomMenuItem("Est", Icons.Default.BarChart, selected == BottomTab.Stats, accent, accent2, onText) {
                onSelect(BottomTab.Stats)
            }
        }
    }
}

@Composable
private fun RowScope.BottomMenuItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    accent: Color,
    accent2: Color,
    onText: Color,
    onClick: () -> Unit
) {
    val bgBrush = if (isSelected) {
        Brush.horizontalGradient(listOf(accent.copy(alpha = 0.30f), accent2.copy(alpha = 0.24f)))
    } else {
        Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
    }

    val tint = if (isSelected) ButtonTextDark else onText.copy(alpha = 0.78f)

    Surface(
        modifier = Modifier
            .height(46.dp)
            .weight(1f)
            .padding(horizontal = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = GlassBase.copy(alpha = if (isSelected) 0.12f else 0.02f),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(bgBrush)
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = label, tint = tint)
            Spacer(Modifier.width(8.dp))
            Text(
                text = label,
                color = tint,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun PositionChipsRow(
    accent: Color,
    onText: Color,
    selected: PlayerPosition?,
    onSelect: (PlayerPosition?) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FilterChip(
            selected = selected == null,
            onClick = { onSelect(null) },
            label = { Text("Todos") },
            colors = chipColors(accent, onText),
            border = null
        )
        PlayerPosition.entries.forEach { pos ->
            FilterChip(
                selected = selected == pos,
                onClick = { onSelect(if (selected == pos) null else pos) },
                label = { Text(pos.short) },
                colors = chipColors(accent, onText),
                border = null
            )
        }
    }
}

@Composable
private fun chipColors(accent: Color, onText: Color) =
    FilterChipDefaults.filterChipColors(
        selectedContainerColor = accent.copy(alpha = 0.18f),
        selectedLabelColor = accent,
        containerColor = GlassBase.copy(alpha = 0.06f),
        labelColor = onText.copy(alpha = 0.78f)
    )

@Composable
private fun PlayerBadgeCard(
    player: Player,
    accent: Color,
    accent2: Color,
    onText: Color,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val statusColors = when (player.status) {
        PlayerStatus.TITULAR -> Win.copy(alpha = 0.16f) to Win
        PlayerStatus.SUPLENTE -> accent.copy(alpha = 0.16f) to accent
        PlayerStatus.LESIONADO -> Loss.copy(alpha = 0.16f) to Loss
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
        shape = RoundedCornerShape(24.dp),
        color = GlassBase.copy(alpha = 0.08f)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
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
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        text = player.name,
                        color = onText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${player.position.label} · #${player.number}",
                        color = onText.copy(alpha = 0.70f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = accent)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = GlassBase.copy(alpha = 0.06f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MiniStat(label = "Edad", value = "${player.age}", onText = onText)
                    MiniStat(label = "OVR", value = "${player.rating}", highlight = true, accent = accent, onText = onText)

                    Surface(shape = RoundedCornerShape(14.dp), color = statusColors.first) {
                        Text(
                            text = player.status.label,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            color = statusColors.second,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniStat(
    label: String,
    value: String,
    onText: Color,
    highlight: Boolean = false,
    accent: Color = Color.Unspecified
) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = label,
            color = onText.copy(alpha = 0.60f),
            style = MaterialTheme.typography.labelSmall
        )
        Text(
            text = value,
            color = if (highlight) accent else onText,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
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