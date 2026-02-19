package com.example.mvp.ui.screens.players

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayersScreen(
    modifier: Modifier = Modifier,
    players: List<Player> = defaultPlayers(),
    onBack: () -> Unit = {},
    onCreatePlayer: () -> Unit = {},
    onEditPlayer: (Player) -> Unit = {},
    onOpenPlayer: (Player) -> Unit = {}
) {
    val bgTop = Color(0xFF0B1220)
    val bgMid = Color(0xFF0E2A3B)
    val accent = Color(0xFF00E5FF)
    val accent2 = Color(0xFF7C4DFF)

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

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreatePlayer,
                containerColor = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Brush.horizontalGradient(listOf(accent, accent2))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir", tint = Color(0xFF061018))
                }
            }
        },
        containerColor = Color.Transparent
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(bgTop, bgMid, bgTop)))
                .padding(horizontal = 20.dp)
                .padding(innerPadding)
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
                    .padding(top = 18.dp, bottom = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }

                    Spacer(Modifier.width(4.dp))

                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "Jugadores",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Plantilla · ${players.size} jugadores",
                            color = Color.White.copy(alpha = 0.70f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = accent.copy(alpha = 0.16f)
                    ) {
                        Text(
                            text = "TEAM",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            color = accent,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White.copy(alpha = 0.07f)
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
                            unfocusedTextColor = Color.White,
                            focusedTextColor = Color.White,
                            unfocusedLeadingIconColor = Color.White.copy(alpha = 0.6f),
                            focusedLeadingIconColor = accent,
                            cursorColor = accent,
                            unfocusedPlaceholderColor = Color.White.copy(alpha = 0.45f),
                            focusedPlaceholderColor = Color.White.copy(alpha = 0.45f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                PositionChipsRow(
                    accent = accent,
                    selected = selectedPos,
                    onSelect = { selectedPos = it }
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtered) { player ->
                        PlayerBadgeCard(
                            player = player,
                            accent = accent,
                            accent2 = accent2,
                            onOpen = { onOpenPlayer(player) },
                            onEdit = { onEditPlayer(player) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PositionChipsRow(
    accent: Color,
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
            colors = chipColors(accent),
            border = null
        )
        PlayerPosition.entries.forEach { pos ->
            FilterChip(
                selected = selected == pos,
                onClick = { onSelect(if (selected == pos) null else pos) },
                label = { Text(pos.short) },
                colors = chipColors(accent),
                border = null
            )
        }
    }
}

@Composable
private fun chipColors(accent: Color) = FilterChipDefaults.filterChipColors(
    selectedContainerColor = accent.copy(alpha = 0.18f),
    selectedLabelColor = accent,
    containerColor = Color.White.copy(alpha = 0.06f),
    labelColor = Color.White.copy(alpha = 0.78f)
)

@Composable
private fun PlayerBadgeCard(
    player: Player,
    accent: Color,
    accent2: Color,
    onOpen: () -> Unit,
    onEdit: () -> Unit
) {
    val statusColors = when (player.status) {
        PlayerStatus.TITULAR -> Color(0xFF00E676).copy(alpha = 0.16f) to Color(0xFF00E676)
        PlayerStatus.SUPLENTE -> accent.copy(alpha = 0.16f) to accent
        PlayerStatus.LESIONADO -> Color(0xFFFF5252).copy(alpha = 0.16f) to Color(0xFFFF5252)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.08f)
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
                        color = Color(0xFF061018),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        text = player.name,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${player.position.label} · #${player.number}",
                        color = Color.White.copy(alpha = 0.70f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = accent)
                }
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color.White.copy(alpha = 0.06f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MiniStat(label = "Edad", value = "${player.age}")
                    MiniStat(label = "OVR", value = "${player.rating}", highlight = true, accent = accent)
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
    highlight: Boolean = false,
    accent: Color = Color.Unspecified
) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.60f),
            style = MaterialTheme.typography.labelSmall
        )
        Text(
            text = value,
            color = if (highlight) accent else Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun initials(name: String): String {
    val parts = name.trim().split(" ").filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> "?"
        parts.size == 1 -> parts.first().take(2).uppercase()
        else -> (parts[0].take(1) + parts[1].take(1)).uppercase()
    }
}

private fun defaultPlayers(): List<Player> = listOf(
    Player(1, "Álex Romero", PlayerPosition.POR, 23, 1, 78, PlayerStatus.TITULAR),
    Player(2, "Sergio Vidal", PlayerPosition.DEF, 27, 2, 80, PlayerStatus.TITULAR),
    Player(3, "Mario Costa", PlayerPosition.DEF, 21, 4, 74, PlayerStatus.SUPLENTE),
    Player(4, "Hugo Navarro", PlayerPosition.DEF, 29, 5, 82, PlayerStatus.TITULAR),
    Player(5, "Iván Paredes", PlayerPosition.MED, 24, 6, 79, PlayerStatus.TITULAR),
    Player(6, "Dani Serrano", PlayerPosition.MED, 22, 8, 77, PlayerStatus.SUPLENTE),
    Player(7, "Lucas Prieto", PlayerPosition.MED, 26, 10, 83, PlayerStatus.TITULAR),
    Player(8, "Adrián Molina", PlayerPosition.DEL, 25, 9, 84, PlayerStatus.TITULAR),
    Player(9, "Eric Salas", PlayerPosition.DEL, 20, 11, 73, PlayerStatus.LESIONADO),
    Player(10, "Bruno Sanz", PlayerPosition.DEL, 28, 7, 81, PlayerStatus.SUPLENTE),
)