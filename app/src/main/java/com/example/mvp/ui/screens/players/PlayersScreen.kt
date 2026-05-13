package com.example.mvp.ui.screens.players

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mvp.ui.components.BottomBarDestination
import com.example.mvp.ui.components.EmptyState
import com.example.mvp.ui.components.ProFootballBottomBar
import com.example.mvp.ui.theme.ButtonTextDark
import com.example.mvp.ui.theme.GlassBase
import com.example.mvp.ui.theme.Loss
import kotlin.math.min

enum class PlayerPosition(val short: String, val label: String) {
    POR("POR", "Portero"),
    DEF("DEF", "Defensa"),
    MED("MED", "Mediocentro"),
    DEL("DEL", "Delantero")
}

enum class PlayerStatus(val label: String) {
    DISPONIBLE("Disponible"),
    LESIONADO("Lesionado")
}

data class Player(
    val id: Int,
    val name: String,
    val position: PlayerPosition,
    val age: Int,
    val number: Int,
    val rating: Int,
    val status: PlayerStatus,
    val lineupSlot: String? = null
)

private enum class PlayersTab { Squad, Lineup }

private data class LineupRule(
    val position: PlayerPosition,
    val title: String,
    val max: Int
)

private data class LineupSlot(
    val id: String,
    val position: PlayerPosition,
    val label: String
)

private data class TacticalFormation(
    val id: String,
    val label: String,
    val defenders: Int,
    val midfielders: Int,
    val forwards: Int
) {
    fun rules(): List<LineupRule> = listOf(
        LineupRule(PlayerPosition.DEL, "DEL", forwards),
        LineupRule(PlayerPosition.MED, "MED", midfielders),
        LineupRule(PlayerPosition.DEF, "DEF", defenders),
        LineupRule(PlayerPosition.POR, "POR", 1)
    )
}

private val availableFormations = listOf(
    TacticalFormation(id = "442", label = "4-4-2", defenders = 4, midfielders = 4, forwards = 2),
    TacticalFormation(id = "433", label = "4-3-3", defenders = 4, midfielders = 3, forwards = 3),
    TacticalFormation(id = "343", label = "3-4-3", defenders = 3, midfielders = 4, forwards = 3),
    TacticalFormation(id = "424", label = "4-2-4", defenders = 4, midfielders = 2, forwards = 4)
)

private fun formationById(id: String?): TacticalFormation =
    availableFormations.firstOrNull { it.id == id } ?: availableFormations.first()

private fun slotId(position: PlayerPosition, index: Int): String = "${position.name}_${index + 1}"

private fun slotPosition(slotId: String?): PlayerPosition? {
    val prefix = slotId?.substringBefore("_") ?: return null
    return runCatching { PlayerPosition.valueOf(prefix) }.getOrNull()
}

private fun benchSlotId(index: Int): String = "BENCH_${index + 1}"

private fun isBenchSlot(slotId: String?): Boolean = slotId?.startsWith("BENCH_") == true

private fun isLineupSlot(slotId: String?): Boolean = slotPosition(slotId) != null

private val ShirtShape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height

    moveTo(w * 0.30f, h * 0.08f)
    lineTo(w * 0.22f, h * 0.18f)
    lineTo(w * 0.08f, h * 0.18f)
    lineTo(0f, h * 0.34f)
    lineTo(w * 0.12f, h * 0.52f)
    lineTo(w * 0.22f, h * 0.46f)
    lineTo(w * 0.22f, h)
    lineTo(w * 0.78f, h)
    lineTo(w * 0.78f, h * 0.46f)
    lineTo(w * 0.88f, h * 0.52f)
    lineTo(w, h * 0.34f)
    lineTo(w * 0.92f, h * 0.18f)
    lineTo(w * 0.78f, h * 0.18f)
    lineTo(w * 0.70f, h * 0.08f)
    close()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayersScreen(
    modifier: Modifier = Modifier,
    players: List<Player> = emptyList(),
    userId: Long = 0,
    onBack: () -> Unit = {},
    onCreatePlayer: () -> Unit = {},
    onEditPlayer: (Player) -> Unit = {},
    onOpenPlayer: (Player) -> Unit = {},
    onDeletePlayer: (Player) -> Unit = {},
    onSavePlayer: (Player) -> Unit = {},
    onGoDashboard: () -> Unit = {},
    onGoTraining: () -> Unit = {},
    onGoMatches: () -> Unit = {},
    onGoStats: () -> Unit = {}
) {
    val bgTop = MaterialTheme.colorScheme.background
    val bgMid = MaterialTheme.colorScheme.surface
    val accent = MaterialTheme.colorScheme.primary
    val accent2 = MaterialTheme.colorScheme.secondary
    val onBg = MaterialTheme.colorScheme.onBackground
    val danger = MaterialTheme.colorScheme.error

    var query by remember { mutableStateOf("") }
    var selectedPos by remember { mutableStateOf<PlayerPosition?>(null) }
    var selectedTab by remember { mutableStateOf(PlayersTab.Squad) }
    var pendingDelete by remember { mutableStateOf<Player?>(null) }
    var selectedForSwap by remember { mutableStateOf<Player?>(null) }

    val context = LocalContext.current
    val lineupPrefs = remember(context) {
        context.getSharedPreferences("profootball_lineup", 0)
    }
    val formationPreferenceKey = remember(userId) {
        "selected_formation_$userId"
    }
    var selectedFormationId by remember(formationPreferenceKey) {
        mutableStateOf(
            lineupPrefs.getString(
                formationPreferenceKey,
                availableFormations.first().id
            ) ?: availableFormations.first().id
        )
    }
    val selectedFormation = remember(selectedFormationId) {
        formationById(selectedFormationId)
    }

    var showFormationSheet by remember { mutableStateOf(false) }
    var pendingFormation by remember { mutableStateOf<TacticalFormation?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun showMessage(message: String) {
        // Mensajes visuales desactivados para que la gestión del once no moleste.
    }

    fun changeFormation(formation: TacticalFormation) {
        if (formation.id == selectedFormationId) return

        players
            .filter { it.lineupSlot != null }
            .forEach { player ->
                onSavePlayer(player.copy(lineupSlot = null, status = PlayerStatus.DISPONIBLE))
            }

        selectedForSwap = null
        selectedFormationId = formation.id
        lineupPrefs.edit().putString(formationPreferenceKey, formation.id).apply()
    }

    fun canMoveToLineupSlot(player: Player, slot: LineupSlot): Boolean {
        return player.status != PlayerStatus.LESIONADO && player.position == slot.position
    }

    fun tryPromote(player: Player) {
        if (player.status == PlayerStatus.LESIONADO) return
        selectedForSwap = player
    }

    fun handleLineupClick(player: Player) {
        val selected = selectedForSwap

        if (selected == null) {
            selectedForSwap = player
            return
        }

        if (selected.id == player.id) {
            selectedForSwap = null
            return
        }

        if (selected.status == PlayerStatus.LESIONADO || player.status == PlayerStatus.LESIONADO) {
            selectedForSwap = null
            return
        }

        val targetSlot = player.lineupSlot
        val targetPosition = slotPosition(targetSlot)

        if (targetPosition == null || selected.position != targetPosition) {
            return
        }

        val selectedSlot = selected.lineupSlot

        if (isLineupSlot(selectedSlot) && selected.position != player.position) {
            return
        }

        onSavePlayer(selected.copy(lineupSlot = targetSlot, status = PlayerStatus.DISPONIBLE))
        onSavePlayer(player.copy(lineupSlot = selectedSlot, status = PlayerStatus.DISPONIBLE))
        selectedForSwap = null
    }

    fun handleEmptySlotClick(slot: LineupSlot) {
        val selected = selectedForSwap ?: return

        if (!canMoveToLineupSlot(selected, slot)) return

        onSavePlayer(selected.copy(lineupSlot = slot.id, status = PlayerStatus.DISPONIBLE))
        selectedForSwap = null
    }

    fun handleBenchPlayerClick(player: Player) {
        val selected = selectedForSwap

        if (selected == null) {
            selectedForSwap = player
            return
        }

        if (selected.id == player.id) {
            selectedForSwap = null
            return
        }

        if (selected.status == PlayerStatus.LESIONADO || player.status == PlayerStatus.LESIONADO) {
            selectedForSwap = null
            return
        }

        val selectedSlot = selected.lineupSlot
        val benchTargetSlot = player.lineupSlot

        if (!isBenchSlot(benchTargetSlot)) return

        if (isLineupSlot(selectedSlot) && selected.position != player.position) {
            return
        }

        onSavePlayer(selected.copy(lineupSlot = benchTargetSlot, status = PlayerStatus.DISPONIBLE))
        onSavePlayer(player.copy(lineupSlot = selectedSlot, status = PlayerStatus.DISPONIBLE))
        selectedForSwap = null
    }

    fun handleEmptyBenchSlotClick(slotId: String) {
        val selected = selectedForSwap ?: return

        if (selected.status == PlayerStatus.LESIONADO) {
            selectedForSwap = null
            return
        }

        onSavePlayer(selected.copy(lineupSlot = slotId, status = PlayerStatus.DISPONIBLE))
        selectedForSwap = null
    }

    val filtered = remember(players, query, selectedPos) {
        players.filter { p ->
            val matchesQuery = query.isBlank() ||
                    p.name.contains(query, ignoreCase = true) ||
                    p.position.short.contains(query, ignoreCase = true) ||
                    p.position.label.contains(query, ignoreCase = true)

            val matchesPos = selectedPos == null || p.position == selectedPos
            matchesQuery && matchesPos
        }
    }

    val bottomBarHeight = 96.dp

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            if (selectedTab == PlayersTab.Squad) {
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
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                Header(players = players, onBack = onBack, onText = onBg)

                PlayersTabs(
                    selectedTab = selectedTab,
                    onSelected = {
                        selectedTab = it
                        selectedForSwap = null
                    },
                    accent = accent,
                    accent2 = accent2,
                    onText = onBg
                )

                when (selectedTab) {
                    PlayersTab.Squad -> {
                        SquadContent(
                            players = players,
                            filtered = filtered,
                            query = query,
                            selectedPos = selectedPos,
                            accent = accent,
                            accent2 = accent2,
                            onText = onBg,
                            onQueryChange = { query = it },
                            onPositionSelected = { selectedPos = it },
                            onCreatePlayer = onCreatePlayer,
                            onOpenPlayer = onOpenPlayer,
                            onEditPlayer = onEditPlayer,
                            onDeleteRequest = { pendingDelete = it },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    PlayersTab.Lineup -> {
                        LineupContent(
                            players = players,
                            selectedFormation = selectedFormation,
                            selectedForSwap = selectedForSwap,
                            accent = accent,
                            accent2 = accent2,
                            onText = onBg,
                            danger = danger,
                            onFormationSelectorClick = {
                                showFormationSheet = true
                            },
                            onPlayerClick = { handleLineupClick(it) },
                            onEmptySlotClick = { handleEmptySlotClick(it) },
                            onBenchPlayerClick = { handleBenchPlayerClick(it) },
                            onEmptyBenchSlotClick = { handleEmptyBenchSlotClick(it) },
                            onCancelSelection = {
                                selectedForSwap = null
                            },
                            onPromote = { tryPromote(it) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            ProFootballBottomBar(
                selected = BottomBarDestination.Players,
                onSelect = { destination ->
                    when (destination) {
                        BottomBarDestination.Training -> onGoTraining()
                        BottomBarDestination.Players -> Unit
                        BottomBarDestination.Dashboard -> onGoDashboard()
                        BottomBarDestination.Matches -> onGoMatches()
                        BottomBarDestination.Stats -> onGoStats()
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 12.dp)
            )
        }
    }

    if (showFormationSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFormationSheet = false },
            containerColor = Color.Transparent,
            dragHandle = null
        ) {
            FormationPickerSheet(
                selectedFormation = selectedFormation,
                accent = accent,
                accent2 = accent2,
                onText = onBg,
                onDismiss = { showFormationSheet = false },
                onFormationClick = { formation ->
                    if (formation.id == selectedFormationId) {
                        showFormationSheet = false
                    } else {
                        pendingFormation = formation
                        showFormationSheet = false
                    }
                }
            )
        }
    }

    pendingFormation?.let { formation ->
        AlertDialog(
            onDismissRequest = { pendingFormation = null },
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
            tonalElevation = 8.dp,
            shape = RoundedCornerShape(24.dp),
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            title = {
                Text(
                    text = "Cambiar a ${formation.label}",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Al cambiar de formación se vaciarán el once y el banquillo. Los jugadores volverán a No convocados."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        changeFormation(formation)
                        pendingFormation = null
                    }
                ) {
                    Text(
                        text = "Cambiar",
                        color = accent,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingFormation = null }) {
                    Text(
                        text = "Cancelar",
                        color = onBg.copy(alpha = 0.76f)
                    )
                }
            }
        )
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
                    Text("Eliminar", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
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
private fun Header(
    players: List<Player>,
    onBack: () -> Unit,
    onText: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = onText)
        }

        Spacer(Modifier.width(4.dp))

        Column(Modifier.weight(1f)) {
            Text(
                text = "Jugadores",
                color = onText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Plantilla · ${players.size} jugadores",
                color = onText.copy(alpha = 0.70f),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun PlayersTabs(
    selectedTab: PlayersTab,
    onSelected: (PlayersTab) -> Unit,
    accent: Color,
    accent2: Color,
    onText: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = GlassBase.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, onText.copy(alpha = 0.06f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TabButton(
                text = "Plantilla",
                selected = selectedTab == PlayersTab.Squad,
                accent = accent,
                accent2 = accent2,
                onText = onText,
                modifier = Modifier.weight(1f),
                onClick = { onSelected(PlayersTab.Squad) }
            )

            TabButton(
                text = "Once",
                selected = selectedTab == PlayersTab.Lineup,
                accent = accent,
                accent2 = accent2,
                onText = onText,
                modifier = Modifier.weight(1f),
                onClick = { onSelected(PlayersTab.Lineup) }
            )
        }
    }
}

@Composable
private fun TabButton(
    text: String,
    selected: Boolean,
    accent: Color,
    accent2: Color,
    onText: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(48.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = if (selected) Color.Transparent else GlassBase.copy(alpha = 0.03f),
        border = BorderStroke(
            width = if (selected) 1.dp else 0.8.dp,
            color = if (selected) accent.copy(alpha = 0.30f) else onText.copy(alpha = 0.05f)
        )
    ) {
        Box(
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
                            listOf(
                                Color.Transparent,
                                GlassBase.copy(alpha = 0.03f)
                            )
                        )
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = if (selected) accent else onText.copy(alpha = 0.76f),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun SquadContent(
    players: List<Player>,
    filtered: List<Player>,
    query: String,
    selectedPos: PlayerPosition?,
    accent: Color,
    accent2: Color,
    onText: Color,
    onQueryChange: (String) -> Unit,
    onPositionSelected: (PlayerPosition?) -> Unit,
    onCreatePlayer: () -> Unit,
    onOpenPlayer: (Player) -> Unit,
    onEditPlayer: (Player) -> Unit,
    onDeleteRequest: (Player) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(shape = RoundedCornerShape(18.dp), color = GlassBase.copy(alpha = 0.07f)) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, null) },
                placeholder = { Text("Buscar jugador, posición...") },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    unfocusedTextColor = onText,
                    focusedTextColor = onText,
                    unfocusedLeadingIconColor = onText.copy(alpha = 0.6f),
                    focusedLeadingIconColor = accent,
                    cursorColor = accent,
                    unfocusedPlaceholderColor = onText.copy(alpha = 0.45f),
                    focusedPlaceholderColor = onText.copy(alpha = 0.45f)
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        PositionChipsRow(accent = accent, onText = onText, selected = selectedPos, onSelect = onPositionSelected)

        when {
            players.isEmpty() -> {
                EmptyState(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Groups,
                    title = "Todavía no tienes jugadores",
                    message = "Añade tu primer jugador para empezar a crear tu plantilla.",
                    buttonText = "Crear jugador",
                    onButtonClick = onCreatePlayer
                )
            }

            filtered.isEmpty() -> {
                EmptyState(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Search,
                    title = "No hay jugadores con ese filtro",
                    message = "Prueba con otro nombre, posición o vuelve a mostrar todos los jugadores."
                )
            }

            else -> {
                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    PlayerPosition.entries.forEach { position ->
                        val group = filtered
                            .filter { it.position == position }
                            .sortedWith(compareBy<Player> { it.status.ordinal }.thenByDescending { it.rating }.thenBy { it.name })

                        if (group.isNotEmpty()) {
                            item(key = "header_${position.name}") {
                                SectionTitle(title = position.label, count = group.size, onText = onText)
                            }

                            items(group, key = { it.id }) { player ->
                                PlayerBadgeCard(
                                    player = player,
                                    accent = accent,
                                    accent2 = accent2,
                                    onText = onText,
                                    onOpen = { onOpenPlayer(player) },
                                    onEdit = { onEditPlayer(player) },
                                    onDelete = { onDeleteRequest(player) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun LineupContent(
    players: List<Player>,
    selectedFormation: TacticalFormation,
    selectedForSwap: Player?,
    accent: Color,
    accent2: Color,
    onText: Color,
    danger: Color,
    onFormationSelectorClick: () -> Unit,
    onPlayerClick: (Player) -> Unit,
    onEmptySlotClick: (LineupSlot) -> Unit,
    onBenchPlayerClick: (Player) -> Unit,
    onEmptyBenchSlotClick: (String) -> Unit,
    onCancelSelection: () -> Unit,
    onPromote: (Player) -> Unit,
    modifier: Modifier = Modifier
) {
    val starters = players
        .filter { isLineupSlot(it.lineupSlot) && it.status != PlayerStatus.LESIONADO }
        .sortedWith(compareBy<Player> { it.position.ordinal }.thenBy { it.lineupSlot ?: "" })

    val benchSlots = (0 until 7).map { index ->
        val slotId = benchSlotId(index)
        slotId to players.firstOrNull { it.lineupSlot == slotId && it.status != PlayerStatus.LESIONADO }
    }

    val bench = benchSlots.mapNotNull { it.second }

    val outfield = players
        .filter { it.status != PlayerStatus.LESIONADO && it.lineupSlot == null }
        .sortedWith(compareBy<Player> { it.position.ordinal }.thenByDescending { it.rating }.thenBy { it.name })

    val injured = players
        .filter { it.status == PlayerStatus.LESIONADO }
        .sortedWith(compareBy<Player> { it.position.ordinal }.thenByDescending { it.rating }.thenBy { it.name })

    LazyColumn(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            FormationSelectorRow(
                selectedFormation = selectedFormation,
                accent = accent,
                accent2 = accent2,
                onText = onText,
                onClick = onFormationSelectorClick
            )
        }

        item {
            PitchBoard(
                formation = selectedFormation,
                starters = starters,
                selectedForSwap = selectedForSwap,
                accent = accent,
                accent2 = accent2,
                onText = onText,
                danger = danger,
                onPlayerClick = onPlayerClick,
                onEmptySlotClick = onEmptySlotClick
            )
        }

        item { SectionTitle(title = "Banquillo", count = bench.size, onText = onText) }

        item {
            BenchSlotsGrid(
                benchSlots = benchSlots,
                selectedForSwap = selectedForSwap,
                onText = onText,
                accent = accent,
                danger = danger,
                onPlayerClick = onBenchPlayerClick,
                onEmptyBenchSlotClick = onEmptyBenchSlotClick
            )
        }

        item { SectionTitle(title = "No convocados", count = outfield.size + injured.size, onText = onText) }

        if (outfield.isEmpty() && injured.isEmpty()) {
            item { SmallEmptyCard(text = "No hay jugadores fuera de la convocatoria.", onText = onText) }
        } else {
            if (outfield.isNotEmpty()) {
                item {
                    CompactPlayersGrid(
                        players = outfield,
                        selectedForSwap = selectedForSwap,
                        onText = onText,
                        accent = accent,
                        danger = danger,
                        showInjuredBadge = false,
                        onPlayerClick = { player ->
                            if (selectedForSwap == null) onPromote(player) else onPlayerClick(player)
                        }
                    )
                }
            }

            if (injured.isNotEmpty()) {
                item {
                    Text(
                        text = "Lesionados",
                        color = onText.copy(alpha = 0.58f),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                item {
                    CompactPlayersGrid(
                        players = injured,
                        selectedForSwap = null,
                        onText = onText,
                        accent = accent,
                        danger = danger,
                        showInjuredBadge = true,
                        onPlayerClick = {}
                    )
                }
            }
        }
    }
}

@Composable
private fun FormationSelectorRow(
    selectedFormation: TacticalFormation,
    accent: Color,
    accent2: Color,
    onText: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        color = GlassBase.copy(alpha = 0.055f),
        border = BorderStroke(1.dp, onText.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Formación",
                    color = onText,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Configura la estructura del once",
                    color = onText.copy(alpha = 0.58f),
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Surface(
                shape = RoundedCornerShape(999.dp),
                color = accent.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, accent.copy(alpha = 0.26f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = selectedFormation.label,
                        color = accent,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Black
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Cambiar formación",
                        tint = accent,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FormationPickerSheet(
    selectedFormation: TacticalFormation,
    accent: Color,
    accent2: Color,
    onText: Color,
    onDismiss: () -> Unit,
    onFormationClick: (TacticalFormation) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(42.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(onText.copy(alpha = 0.18f))
                    .align(Alignment.CenterHorizontally)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "Seleccionar formación",
                        color = onText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Si eliges una distinta, se vaciará el once y el banquillo.",
                        color = onText.copy(alpha = 0.60f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            availableFormations.forEach { formation ->
                FormationOptionRow(
                    formation = formation,
                    selected = formation.id == selectedFormation.id,
                    accent = accent,
                    accent2 = accent2,
                    onText = onText,
                    onClick = { onFormationClick(formation) }
                )
            }

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = "Cerrar",
                    color = accent,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun FormationOptionRow(
    formation: TacticalFormation,
    selected: Boolean,
    accent: Color,
    accent2: Color,
    onText: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        color = if (selected) accent.copy(alpha = 0.13f) else GlassBase.copy(alpha = 0.055f),
        border = BorderStroke(
            width = if (selected) 1.dp else 0.8.dp,
            color = if (selected) accent.copy(alpha = 0.34f) else onText.copy(alpha = 0.05f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (selected) accent.copy(alpha = 0.18f) else GlassBase.copy(alpha = 0.06f)
            ) {
                Text(
                    text = formation.label,
                    color = if (selected) accent else onText.copy(alpha = 0.82f),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Text(
                text = "${formation.defenders} DEF · ${formation.midfielders} MED · ${formation.forwards} DEL",
                color = onText.copy(alpha = 0.62f),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Seleccionada",
                    tint = accent,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun PitchBoard(
    formation: TacticalFormation,
    starters: List<Player>,
    selectedForSwap: Player?,
    accent: Color,
    accent2: Color,
    onText: Color,
    danger: Color,
    onPlayerClick: (Player) -> Unit,
    onEmptySlotClick: (LineupSlot) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(30.dp),
        color = GlassBase.copy(alpha = 0.08f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(570.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            accent.copy(alpha = 0.09f),
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.18f),
                            accent2.copy(alpha = 0.07f)
                        )
                    )
                )
        ) {
            PitchGrass(
                modifier = Modifier.matchParentSize(),
                accent = accent,
                accent2 = accent2
            )

            PitchLines(
                modifier = Modifier
                    .matchParentSize()
                    .padding(horizontal = 10.dp, vertical = 12.dp),
                lineColor = onText.copy(alpha = 0.16f)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 14.dp, end = 14.dp, top = 22.dp, bottom = 42.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                formation.rules().forEach { rule ->
                    val linePlayers = (0 until rule.max).map { index ->
                        val slot = LineupSlot(slotId(rule.position, index), rule.position, rule.title)
                        slot to starters.firstOrNull { it.lineupSlot == slot.id }
                    }

                    FormationRow(
                        title = rule.title,
                        max = rule.max,
                        slots = linePlayers,
                        selectedForSwap = selectedForSwap,
                        accent = accent,
                        accent2 = accent2,
                        onText = onText,
                        danger = danger,
                        onPlayerClick = onPlayerClick,
                        onEmptySlotClick = onEmptySlotClick
                    )
                }
            }
        }
    }
}

@Composable
private fun PitchGrass(
    modifier: Modifier = Modifier,
    accent: Color,
    accent2: Color
) {
    Canvas(modifier = modifier) {
        val stripeHeight = size.height / 8f
        repeat(8) { index ->
            val color = if (index % 2 == 0) {
                accent.copy(alpha = 0.025f)
            } else {
                accent2.copy(alpha = 0.020f)
            }

            drawRect(
                color = color,
                topLeft = Offset(0f, stripeHeight * index),
                size = Size(size.width, stripeHeight)
            )
        }
    }
}

@Composable
private fun PitchLines(
    modifier: Modifier = Modifier,
    lineColor: Color
) {
    Canvas(modifier = modifier) {
        val stroke = 3.dp.toPx()
        val w = size.width
        val h = size.height
        val radius = min(w, h) * 0.15f

        drawRoundRect(
            color = lineColor,
            topLeft = Offset.Zero,
            size = Size(w, h),
            style = Stroke(width = stroke)
        )

        drawLine(
            color = lineColor,
            start = Offset(0f, h / 2f),
            end = Offset(w, h / 2f),
            strokeWidth = stroke
        )

        drawCircle(
            color = lineColor,
            radius = radius,
            center = Offset(w / 2f, h / 2f),
            style = Stroke(width = stroke)
        )

        val boxWidth = w * 0.38f
        val boxHeight = h * 0.16f
        val smallWidth = w * 0.18f
        val smallHeight = h * 0.07f

        drawRect(
            color = lineColor,
            topLeft = Offset((w - boxWidth) / 2f, 0f),
            size = Size(boxWidth, boxHeight),
            style = Stroke(width = stroke)
        )
        drawRect(
            color = lineColor,
            topLeft = Offset((w - smallWidth) / 2f, 0f),
            size = Size(smallWidth, smallHeight),
            style = Stroke(width = stroke)
        )

        drawRect(
            color = lineColor,
            topLeft = Offset((w - boxWidth) / 2f, h - boxHeight),
            size = Size(boxWidth, boxHeight),
            style = Stroke(width = stroke)
        )
        drawRect(
            color = lineColor,
            topLeft = Offset((w - smallWidth) / 2f, h - smallHeight),
            size = Size(smallWidth, smallHeight),
            style = Stroke(width = stroke)
        )
    }
}


@Composable
private fun FormationRow(
    title: String,
    max: Int,
    slots: List<Pair<LineupSlot, Player?>>,
    selectedForSwap: Player?,
    accent: Color,
    accent2: Color,
    onText: Color,
    danger: Color,
    onPlayerClick: (Player) -> Unit,
    onEmptySlotClick: (LineupSlot) -> Unit
) {
    val horizontalPadding = when (max) {
        1 -> 96.dp
        2 -> 34.dp
        4 -> 4.dp
        else -> 0.dp
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        slots.forEach { (slot, player) ->
            if (player == null) {
                EmptyPitchSlot(
                    slot = slot,
                    onText = onText,
                    selectedForSwap = selectedForSwap,
                    accent = accent,
                    onClick = { onEmptySlotClick(slot) }
                )
            } else {
                PitchPlayerNode(
                    player = player,
                    selected = selectedForSwap?.id == player.id,
                    compatible = selectedForSwap == null || selectedForSwap.position == player.position,
                    accent = accent,
                    accent2 = accent2,
                    onText = onText,
                    danger = danger,
                    onClick = { onPlayerClick(player) }
                )
            }
        }
    }
}

@Composable
private fun PitchPlayerNode(
    player: Player,
    selected: Boolean,
    compatible: Boolean,
    accent: Color,
    accent2: Color,
    onText: Color,
    danger: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .widthIn(min = 68.dp, max = 92.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(999.dp),
            color = when {
                selected -> accent.copy(alpha = 0.20f)
                compatible -> GlassBase.copy(alpha = 0.08f)
                else -> GlassBase.copy(alpha = 0.025f)
            }
        ) {
            Text(
                text = "OVR ${player.rating}",
                color = if (selected) accent else onText.copy(alpha = if (compatible) 0.78f else 0.30f),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        Box(
            modifier = Modifier.size(width = 56.dp, height = 50.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(width = 52.dp, height = 46.dp)
                    .clip(ShirtShape)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                accent.copy(alpha = if (selected) 0.62f else if (compatible) 0.42f else 0.16f),
                                accent2.copy(alpha = if (selected) 0.58f else if (compatible) 0.38f else 0.14f)
                            )
                        )
                    )
                    .border(
                        width = if (selected) 1.6.dp else 1.dp,
                        color = if (selected) accent else onText.copy(alpha = if (compatible) 0.12f else 0.05f),
                        shape = ShirtShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = player.number.toString(),
                    color = ButtonTextDark.copy(alpha = if (compatible || selected) 1f else 0.38f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black
                )
            }

            if (selected) {
                Surface(
                    shape = CircleShape,
                    color = accent,
                    modifier = Modifier.size(16.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "✓",
                            color = ButtonTextDark,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        Text(
            text = shortName(player.name),
            color = onText.copy(alpha = if (compatible || selected) 1f else 0.35f),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        Text(
            text = player.position.short,
            color = if (selected) accent else onText.copy(alpha = if (compatible) 0.68f else 0.28f),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EmptyPitchSlot(
    slot: LineupSlot,
    onText: Color,
    selectedForSwap: Player?,
    accent: Color,
    onClick: () -> Unit
) {
    val compatible = selectedForSwap?.position == slot.position
    val waitingSelection = selectedForSwap != null

    Column(
        modifier = Modifier
            .widthIn(min = 68.dp, max = 92.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(999.dp),
            color = when {
                compatible -> accent.copy(alpha = 0.18f)
                waitingSelection -> GlassBase.copy(alpha = 0.03f)
                else -> GlassBase.copy(alpha = 0.05f)
            }
        ) {
            Text(
                text = if (compatible) "Libre" else "OVR --",
                color = if (compatible) accent else onText.copy(alpha = 0.22f),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        Box(
            modifier = Modifier
                .size(width = 52.dp, height = 46.dp)
                .clip(ShirtShape)
                .background(if (compatible) accent.copy(alpha = 0.16f) else GlassBase.copy(alpha = 0.05f))
                .border(
                    width = if (compatible) 1.5.dp else 1.dp,
                    color = if (compatible) accent.copy(alpha = 0.62f) else onText.copy(alpha = 0.10f),
                    shape = ShirtShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "--",
                color = if (compatible) accent else onText.copy(alpha = 0.22f),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Black
            )
        }

        Text(
            text = if (compatible) "Colocar" else "Vacío",
            color = if (compatible) accent else onText.copy(alpha = 0.26f),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        Text(
            text = slot.position.short,
            color = if (compatible) accent.copy(alpha = 0.82f) else Color.Transparent,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun BenchSlotsGrid(
    benchSlots: List<Pair<String, Player?>>,
    selectedForSwap: Player?,
    onText: Color,
    accent: Color,
    danger: Color,
    onPlayerClick: (Player) -> Unit,
    onEmptyBenchSlotClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        benchSlots.chunked(2).forEach { rowSlots ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowSlots.forEach { (slotId, player) ->
                    if (player != null) {
                        CompactPlayerCard(
                            player = player,
                            selected = selectedForSwap?.id == player.id,
                            onText = onText,
                            accent = accent,
                            danger = danger,
                            showInjuredBadge = false,
                            enabled = true,
                            onClick = { onPlayerClick(player) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        EmptyBenchSlotCard(
                            slotId = slotId,
                            selectedForSwap = selectedForSwap,
                            onText = onText,
                            accent = accent,
                            onClick = { onEmptyBenchSlotClick(slotId) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (rowSlots.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun EmptyBenchSlotCard(
    slotId: String,
    selectedForSwap: Player?,
    onText: Color,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val canReceive = selectedForSwap != null && selectedForSwap.status != PlayerStatus.LESIONADO

    Surface(
        modifier = modifier
            .height(92.dp)
            .clickable(enabled = canReceive) { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (canReceive) accent.copy(alpha = 0.10f) else GlassBase.copy(alpha = 0.045f),
        border = BorderStroke(
            width = if (canReceive) 1.dp else 0.8.dp,
            color = if (canReceive) accent.copy(alpha = 0.35f) else onText.copy(alpha = 0.05f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (canReceive) accent.copy(alpha = 0.16f) else GlassBase.copy(alpha = 0.06f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+",
                    color = if (canReceive) accent else onText.copy(alpha = 0.28f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = if (canReceive) "Enviar aquí" else "Hueco libre",
                    color = if (canReceive) accent else onText.copy(alpha = 0.42f),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Banquillo ${slotId.substringAfter("_")}",
                    color = onText.copy(alpha = if (canReceive) 0.62f else 0.30f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun CompactPlayersGrid(
    players: List<Player>,
    selectedForSwap: Player?,
    onText: Color,
    accent: Color,
    danger: Color,
    showInjuredBadge: Boolean,
    onPlayerClick: (Player) -> Unit
) {
    if (players.isEmpty()) {
        Text(
            text = "Sin jugadores en esta sección",
            color = onText.copy(alpha = 0.55f),
            style = MaterialTheme.typography.bodyMedium
        )
        return
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        players.chunked(2).forEach { rowPlayers ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowPlayers.forEach { player ->
                    CompactPlayerCard(
                        player = player,
                        selected = selectedForSwap?.id == player.id,
                        onText = onText,
                        accent = accent,
                        danger = danger,
                        showInjuredBadge = showInjuredBadge,
                        enabled = player.status != PlayerStatus.LESIONADO,
                        onClick = { onPlayerClick(player) },
                        modifier = Modifier.weight(1f)
                    )
                }

                if (rowPlayers.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun CompactPlayerCard(
    player: Player,
    selected: Boolean,
    onText: Color,
    accent: Color,
    danger: Color,
    showInjuredBadge: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isInjured = player.status == PlayerStatus.LESIONADO
    val mainColor = if (isInjured) danger else accent

    Surface(
        modifier = modifier
            .height(92.dp)
            .clickable(enabled = enabled) { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (selected) accent.copy(alpha = 0.14f) else GlassBase.copy(alpha = 0.08f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(mainColor.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                if (isInjured) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Lesionado",
                        tint = mainColor,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Text(
                        text = player.number.toString(),
                        color = ButtonTextDark,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = shortName(player.name),
                    color = onText,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "OVR ${player.rating}",
                    color = onText.copy(alpha = 0.68f),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun LineupListRow(
    player: Player,
    selected: Boolean,
    onText: Color,
    accent: Color,
    danger: Color,
    enabled: Boolean,
    actionText: String,
    onClick: () -> Unit
) {
    CompactPlayerCard(
        player = player,
        selected = selected,
        onText = onText,
        accent = accent,
        danger = danger,
        showInjuredBadge = player.status == PlayerStatus.LESIONADO,
        enabled = enabled,
        onClick = onClick
    )
}

@Composable
private fun SmallEmptyCard(text: String, onText: Color) {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), color = GlassBase.copy(alpha = 0.06f)) {
        Text(
            text = text,
            color = onText.copy(alpha = 0.62f),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun SectionTitle(
    title: String,
    count: Int,
    onText: Color
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = title,
            color = onText,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = count.toString(),
            color = onText.copy(alpha = 0.55f),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun PositionChipsRow(
    accent: Color,
    onText: Color,
    selected: PlayerPosition?,
    onSelect: (PlayerPosition?) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
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
    val isInjured = player.status == PlayerStatus.LESIONADO
    val statusText = if (isInjured) "Lesionado" else "Disponible"
    val statusColor = if (isInjured) Loss else accent

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() },
        shape = RoundedCornerShape(24.dp),
        color = GlassBase.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, onText.copy(alpha = 0.04f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                accent.copy(alpha = 0.38f),
                                accent2.copy(alpha = 0.30f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = player.position.short,
                    color = ButtonTextDark,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = player.name,
                        color = onText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = statusColor.copy(alpha = 0.14f)
                    ) {
                        Text(
                            text = statusText,
                            color = statusColor,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                        )
                    }
                }

                Text(
                    text = "#${player.number} · ${player.position.label} · ${player.age} años",
                    color = onText.copy(alpha = 0.66f),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "OVR ${player.rating}",
                    color = accent,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(Modifier.width(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onEdit, modifier = Modifier.size(38.dp)) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = onText.copy(alpha = 0.72f)
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(38.dp)) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.92f)
                    )
                }
            }
        }
    }
}

private fun shortName(name: String): String {
    val parts = name.trim().split(" ").filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> name
        parts.size == 1 -> parts.first()
        else -> parts.first() + " " + parts.last().first() + "."
    }
}