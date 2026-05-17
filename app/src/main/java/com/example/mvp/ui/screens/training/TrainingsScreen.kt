package com.example.mvp.ui.screens.training

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import com.example.mvp.domain.model.Training
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mvp.ui.components.BottomBarDestination
import com.example.mvp.ui.components.EmptyState
import com.example.mvp.ui.components.ProFootballBottomBar
import com.example.mvp.ui.theme.ButtonTextDark
import com.example.mvp.ui.theme.GlassBase
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.launch

private enum class TrainingTab(val label: String) {
    PENDING("Por hacer"),
    HISTORY("Historial"),
    CALENDAR("Calendario")
}

private fun String.toTrainingTab(): TrainingTab {
    return when (lowercase()) {
        "history", "historial" -> TrainingTab.HISTORY
        "calendar", "calendario" -> TrainingTab.CALENDAR
        else -> TrainingTab.PENDING
    }
}

@Composable
fun TrainingsScreen(
    modifier: Modifier = Modifier,
    trainings: List<Training> = emptyList(),
    initialTab: String = "pending",
    onBack: () -> Unit = {},
    onCreateTraining: () -> Unit = {},
    onEditTraining: (Training) -> Unit = {},
    onDeleteTraining: (Training) -> Unit = {},
    onToggleDone: (Training) -> Unit = {},

    onGoDashboard: () -> Unit = {},
    onGoMatches: () -> Unit = {},
    onGoPlayers: () -> Unit = {},
    onGoStats: () -> Unit = {}
) {
    val bgTop = MaterialTheme.colorScheme.background
    val bgMid = MaterialTheme.colorScheme.surface
    val accent = MaterialTheme.colorScheme.primary
    val accent2 = MaterialTheme.colorScheme.secondary
    val onBg = MaterialTheme.colorScheme.onBackground
    val danger = MaterialTheme.colorScheme.error

    var query by remember { mutableStateOf("") }
    var selectedTab by remember(initialTab) { mutableStateOf(initialTab.toTrainingTab()) }
    var selectedMonth by remember { mutableStateOf(YearMonth.now()) }
    var toDelete by remember { mutableStateOf<Training?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val pending = remember(trainings) { trainings.filter { !it.isDone }.sortedPendingSmart() }
    val history = remember(trainings) { trainings.filter { it.isDone }.sortedByDate(desc = true) }
    val overdueCount = remember(trainings) { trainings.count { it.isOverdue() } }
    val activePendingCount = remember(trainings) { trainings.count { !it.isDone && !it.isOverdue() } }

    val filteredPending = remember(pending, query) { pending.filterByQuery(query) }
    val filteredHistory = remember(history, query) { history.filterByQuery(query) }

    val bottomBarHeight = 96.dp

    val handleToggleDone: (Training) -> Unit = { training ->
        if (!training.isDone && training.isFuturePending()) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "No puedes completar un entrenamiento futuro"
                )
            }
        } else {
            onToggleDone(training)
        }
    }

    Box(
        modifier = modifier
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
                .padding(top = 18.dp, bottom = bottomBarHeight + 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Header(onBack = onBack, onBg = onBg)

            TrainingSummary(
                pending = activePendingCount,
                overdue = overdueCount,
                done = history.size,
                minutesDone = history.sumOf { it.durationMin },
                accent = accent,
                accent2 = accent2,
                danger = danger,
                onBg = onBg
            )

            TabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = Color.Transparent,
                contentColor = accent
            ) {
                TrainingTab.entries.forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = { Text(tab.label, fontWeight = FontWeight.SemiBold) }
                    )
                }
            }

            if (selectedTab != TrainingTab.CALENDAR) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    placeholder = { Text("Buscar por nombre o tipo") },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = onBg.copy(alpha = 0.18f),
                        focusedBorderColor = accent,
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

            when (selectedTab) {
                TrainingTab.PENDING -> TrainingList(
                    trainings = filteredPending,
                    emptyTitle = if (trainings.isEmpty()) "Todavía no tienes entrenamientos" else "No hay entrenamientos pendientes",
                    emptyMessage = if (trainings.isEmpty()) "Crea tu primer entrenamiento para empezar a planificar el trabajo del equipo." else "Los entrenamientos completados se mueven al historial.",
                    accent = accent,
                    danger = danger,
                    onText = onBg,
                    onEdit = onEditTraining,
                    onDelete = { toDelete = it },
                    onToggleDone = handleToggleDone,
                    modifier = Modifier.weight(1f)
                )

                TrainingTab.HISTORY -> TrainingList(
                    trainings = filteredHistory,
                    emptyTitle = "Historial vacío",
                    emptyMessage = "Cuando marques un entrenamiento como hecho, aparecerá aquí.",
                    accent = accent,
                    danger = danger,
                    onText = onBg,
                    onEdit = onEditTraining,
                    onDelete = { toDelete = it },
                    onToggleDone = handleToggleDone,
                    modifier = Modifier.weight(1f)
                )

                TrainingTab.CALENDAR -> TrainingCalendar(
                    month = selectedMonth,
                    trainings = trainings,
                    onPreviousMonth = { selectedMonth = selectedMonth.minusMonths(1) },
                    onNextMonth = { selectedMonth = selectedMonth.plusMonths(1) },
                    accent = accent,
                    accent2 = accent2,
                    danger = danger,
                    onText = onBg,
                    modifier = Modifier.weight(1f)
                )
            }

            Button(
                onClick = onCreateTraining,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Brush.horizontalGradient(listOf(accent, accent2))),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = ButtonTextDark)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Crear entrenamiento",
                            color = ButtonTextDark,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp)
                .padding(bottom = bottomBarHeight + 18.dp)
        )

        ProFootballBottomBar(
            selected = BottomBarDestination.Training,
            onSelect = { destination ->
                when (destination) {
                    BottomBarDestination.Training -> Unit
                    BottomBarDestination.Players -> onGoPlayers()
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

    if (toDelete != null) {
        AlertDialog(
            onDismissRequest = { toDelete = null },
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
            tonalElevation = 6.dp,
            shape = RoundedCornerShape(24.dp),
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            title = { Text("Eliminar entrenamiento", fontWeight = FontWeight.SemiBold) },
            text = { Text("Se eliminará “${toDelete!!.name}”. Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        toDelete?.let(onDeleteTraining)
                        toDelete = null
                    }
                ) {
                    Text("Eliminar", color = danger, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { toDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun Header(onBack: () -> Unit, onBg: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = onBg)
        }
        Spacer(Modifier.width(4.dp))
        Column {
            Text(
                text = "Entrenamientos",
                color = onBg,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Planifica, completa y revisa el trabajo del equipo",
                color = onBg.copy(alpha = 0.65f),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun TrainingSummary(
    pending: Int,
    overdue: Int,
    done: Int,
    minutesDone: Int,
    accent: Color,
    accent2: Color,
    danger: Color,
    onBg: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = GlassBase.copy(alpha = 0.08f)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SummaryChip("Pend.", pending.toString(), accent2, onBg, Modifier.weight(1f))
            SummaryChip("Atras.", overdue.toString(), danger, onBg, Modifier.weight(1f))
            SummaryChip("Hechos", done.toString(), accent, onBg, Modifier.weight(1f))
            SummaryChip("Min", minutesDone.toString(), accent2, onBg, Modifier.weight(1f))
        }
    }
}

@Composable
private fun SummaryChip(label: String, value: String, accent: Color, onBg: Color, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = accent.copy(alpha = 0.12f)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, color = onBg, fontWeight = FontWeight.Bold)
            Text(label, color = onBg.copy(alpha = 0.65f), style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun TrainingList(
    trainings: List<Training>,
    emptyTitle: String,
    emptyMessage: String,
    accent: Color,
    danger: Color,
    onText: Color,
    onEdit: (Training) -> Unit,
    onDelete: (Training) -> Unit,
    onToggleDone: (Training) -> Unit,
    modifier: Modifier = Modifier
) {
    if (trainings.isEmpty()) {
        EmptyState(
            modifier = modifier,
            icon = Icons.Default.FitnessCenter,
            title = emptyTitle,
            message = emptyMessage
        )
    } else {
        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(trainings, key = { it.id }) { training ->
                TrainingRow(
                    training = training,
                    accent = accent,
                    danger = danger,
                    onText = onText,
                    onEdit = { onEdit(training) },
                    onDelete = { onDelete(training) },
                    onToggleDone = { onToggleDone(training) }
                )
            }
        }
    }
}

@Composable
private fun TrainingRow(
    training: Training,
    accent: Color,
    danger: Color,
    onText: Color,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleDone: () -> Unit
) {
    val overdue = remember(training.dateText, training.isDone) {
        training.isOverdue()
    }

    val futurePending = remember(training.dateText, training.isDone) {
        training.isFuturePending()
    }

    val leadingIcon = when {
        training.isDone -> Icons.Default.CheckCircle
        overdue -> Icons.Default.Schedule
        else -> Icons.Default.RadioButtonUnchecked
    }

    val leadingIconDescription = when {
        training.isDone -> "Entrenamiento hecho. Pulsar para marcar como pendiente"
        overdue -> "Entrenamiento atrasado. Pulsar para marcar como hecho"
        futurePending -> "Entrenamiento futuro. No se puede marcar como hecho todavía"
        else -> "Entrenamiento pendiente. Pulsar para marcar como hecho"
    }

    val leadingIconTint = when {
        training.isDone -> accent
        overdue -> danger
        futurePending -> onText.copy(alpha = 0.45f)
        else -> onText.copy(alpha = 0.75f)
    }

    val leadingCircleColor = when {
        training.isDone -> accent.copy(alpha = 0.18f)
        overdue -> danger.copy(alpha = 0.12f)
        futurePending -> onText.copy(alpha = 0.04f)
        else -> onText.copy(alpha = 0.06f)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = GlassBase.copy(alpha = 0.08f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onToggleDone,
                modifier = Modifier.size(44.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = leadingCircleColor,
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = leadingIcon,
                            contentDescription = leadingIconDescription,
                            tint = leadingIconTint,
                            modifier = Modifier.size(
                                when {
                                    training.isDone -> 22.dp
                                    overdue -> 20.dp
                                    else -> 21.dp
                                }
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.width(8.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = training.name,
                    color = onText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "${training.dateText} · ${training.durationMin} min · ${training.type.label}",
                    color = onText.copy(alpha = 0.68f),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                StatusPill(
                    isDone = training.isDone,
                    isOverdue = overdue,
                    accent = accent,
                    danger = danger,
                    onText = onText
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = onText.copy(alpha = 0.7f)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Eliminar",
                        tint = danger.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusPill(
    isDone: Boolean,
    isOverdue: Boolean,
    accent: Color,
    danger: Color,
    onText: Color
) {
    val label = when {
        isDone -> "Hecho"
        isOverdue -> "Atrasado"
        else -> "Pendiente"
    }

    val pillColor = when {
        isDone -> accent.copy(alpha = 0.18f)
        isOverdue -> danger.copy(alpha = 0.16f)
        else -> onText.copy(alpha = 0.08f)
    }

    val textColor = when {
        isDone -> accent
        isOverdue -> danger.copy(alpha = 0.95f)
        else -> onText.copy(alpha = 0.65f)
    }

    Surface(
        shape = RoundedCornerShape(999.dp),
        color = pillColor,
        modifier = Modifier.wrapContentWidth()
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun TrainingCalendar(
    month: YearMonth,
    trainings: List<Training>,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    accent: Color,
    accent2: Color,
    danger: Color,
    onText: Color,
    modifier: Modifier = Modifier
) {
    val formatter = remember { DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es", "ES")) }
    val dayFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/uuuu") }
    val scroll = rememberScrollState()
    var selectedDate by remember(month) { mutableStateOf<LocalDate?>(null) }

    val byDate = remember(trainings) {
        trainings.groupBy { parseTrainingDate(it.dateText) }
    }

    val monthTrainings = remember(trainings, month) {
        trainings
            .filter { parseTrainingDate(it.dateText)?.let { date -> YearMonth.from(date) == month } == true }
            .sortedByDate()
    }

    val selectedDayTrainings = remember(selectedDate, byDate) {
        selectedDate?.let { byDate[it].orEmpty().sortedByDate() }.orEmpty()
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = GlassBase.copy(alpha = 0.08f)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scroll)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = accent)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = month.format(formatter).replaceFirstChar { it.titlecase(Locale("es", "ES")) },
                    color = onText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onPreviousMonth) { Text("<") }
                TextButton(onClick = onNextMonth) { Text(">") }
            }

            CalendarLegend(accent = accent, accent2 = accent2, danger = danger, onText = onText)

            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("L", "M", "X", "J", "V", "S", "D").forEach { day ->
                    Text(
                        text = day,
                        color = onText.copy(alpha = 0.65f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            CalendarGrid(
                month = month,
                trainingsByDate = byDate,
                selectedDate = selectedDate,
                onSelectDate = { selectedDate = it },
                accent = accent,
                accent2 = accent2,
                danger = danger,
                onText = onText
            )

            if (selectedDate != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Entrenamientos del ${selectedDate!!.format(dayFormatter)}",
                        color = onText,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )

                    TextButton(onClick = { selectedDate = null }) {
                        Text("Ver todos", color = accent)
                    }
                }

                if (selectedDayTrainings.isEmpty()) {
                    Text(
                        text = "No hay entrenamientos en este día.",
                        color = onText.copy(alpha = 0.65f),
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    selectedDayTrainings.forEach { training ->
                        CalendarTrainingLine(
                            training = training,
                            accent = accent,
                            accent2 = accent2,
                            danger = danger,
                            onText = onText
                        )
                    }
                }
            } else if (monthTrainings.isEmpty()) {
                Text(
                    text = "No hay entrenamientos en este mes.",
                    color = onText.copy(alpha = 0.65f),
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                Text(
                    text = "Entrenamientos del mes",
                    color = onText,
                    fontWeight = FontWeight.SemiBold
                )

                monthTrainings.take(6).forEach { training ->
                    CalendarTrainingLine(
                        training = training,
                        accent = accent,
                        accent2 = accent2,
                        danger = danger,
                        onText = onText
                    )
                }

                if (monthTrainings.size > 6) {
                    Text(
                        text = "+${monthTrainings.size - 6} entrenamientos más. Pulsa un día del calendario para ver sus entrenamientos.",
                        color = onText.copy(alpha = 0.62f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarLegend(
    accent: Color,
    accent2: Color,
    danger: Color,
    onText: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendItem(label = "Hecho", color = accent, onText = onText)
        LegendItem(label = "Pendiente", color = accent2, onText = onText)
        LegendItem(label = "Atrasado", color = danger, onText = onText)
    }
}

@Composable
private fun LegendItem(label: String, color: Color, onText: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = label,
            color = onText.copy(alpha = 0.68f),
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1
        )
    }
}

@Composable
private fun CalendarTrainingLine(
    training: Training,
    accent: Color,
    accent2: Color,
    danger: Color,
    onText: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(trainingStatusColor(training, accent, accent2, danger))
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "${training.dateText} · ${training.name} · ${training.statusLabel()}",
            color = onText.copy(alpha = 0.78f),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun CalendarGrid(
    month: YearMonth,
    trainingsByDate: Map<LocalDate?, List<Training>>,
    selectedDate: LocalDate?,
    onSelectDate: (LocalDate?) -> Unit,
    accent: Color,
    accent2: Color,
    danger: Color,
    onText: Color
) {
    val firstDay = month.atDay(1)
    val leadingEmptyDays = firstDay.dayOfWeek.value - 1
    val totalCells = leadingEmptyDays + month.lengthOfMonth()
    val rows = (totalCells + 6) / 7

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(rows) { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(7) { col ->
                    val cellIndex = row * 7 + col
                    val day = cellIndex - leadingEmptyDays + 1
                    if (day !in 1..month.lengthOfMonth()) {
                        Spacer(modifier = Modifier.weight(1f).height(42.dp))
                    } else {
                        val date = month.atDay(day)
                        val dayTrainings = trainingsByDate[date].orEmpty()
                        val isSelected = selectedDate == date
                        val hasOverdue = dayTrainings.any { it.isOverdue() }
                        val hasDone = dayTrainings.any { it.isDone }
                        val hasPending = dayTrainings.any { !it.isDone && !it.isOverdue() }
                        val dayAccent = when {
                            hasOverdue -> danger
                            hasPending -> accent2
                            hasDone -> accent
                            else -> onText
                        }
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .clickable { onSelectDate(if (isSelected) null else date) },
                            shape = RoundedCornerShape(12.dp),
                            color = when {
                                isSelected -> dayAccent.copy(alpha = 0.34f)
                                dayTrainings.isNotEmpty() -> dayAccent.copy(alpha = 0.18f)
                                else -> onText.copy(alpha = 0.05f)
                            }
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(day.toString(), color = onText, style = MaterialTheme.typography.labelMedium)
                                if (dayTrainings.isNotEmpty()) {
                                    Text(
                                        text = dayTrainings.size.toString(),
                                        color = dayAccent,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun Training.statusLabel(): String {
    return when {
        isDone -> "Hecho"
        isOverdue() -> "Atrasado"
        else -> "Pendiente"
    }
}

private fun trainingStatusColor(
    training: Training,
    accent: Color,
    accent2: Color,
    danger: Color
): Color {
    return when {
        training.isDone -> accent
        training.isOverdue() -> danger
        else -> accent2
    }
}

private fun List<Training>.filterByQuery(query: String): List<Training> {
    return filter { training ->
        query.isBlank() ||
                training.name.contains(query, ignoreCase = true) ||
                training.type.label.contains(query, ignoreCase = true) ||
                training.dateText.contains(query, ignoreCase = true)
    }
}

private fun List<Training>.sortedPendingSmart(): List<Training> {
    val today = LocalDate.now()
    return sortedWith(
        compareBy<Training> { training ->
            val date = parseTrainingDate(training.dateText)
            when {
                date == null -> 3
                date.isBefore(today) -> 0
                date.isEqual(today) -> 1
                else -> 2
            }
        }.thenBy { parseTrainingDate(it.dateText) }
            .thenBy { it.name.lowercase(Locale.getDefault()) }
            .thenBy { it.id }
    )
}

private fun List<Training>.sortedByDate(desc: Boolean = false): List<Training> {
    return if (desc) {
        sortedWith(compareByDescending<Training> { parseTrainingDate(it.dateText) }.thenByDescending { it.id })
    } else {
        sortedWith(compareBy<Training> { parseTrainingDate(it.dateText) }.thenBy { it.id })
    }
}

private fun Training.isOverdue(): Boolean {
    val date = parseTrainingDate(dateText) ?: return false
    return !isDone && date.isBefore(LocalDate.now())
}


private fun Training.isFuturePending(): Boolean {
    val date = parseTrainingDate(dateText) ?: return false
    return !isDone && date.isAfter(LocalDate.now())
}

private fun parseTrainingDate(text: String): LocalDate? {
    return try {
        LocalDate.parse(text, DateTimeFormatter.ofPattern("dd/MM/uuuu"))
    } catch (_: Exception) {
        null
    }
}