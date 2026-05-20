package com.example.mvp.ui.screens.matches

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mvp.domain.model.Match
import com.example.mvp.domain.model.MatchResult
import com.example.mvp.ui.components.BottomBarDestination
import com.example.mvp.ui.components.EmptyState
import com.example.mvp.ui.components.ProFootballBottomBar
import com.example.mvp.ui.theme.ButtonTextDark
import com.example.mvp.ui.theme.Draw
import com.example.mvp.ui.theme.GlassBase
import com.example.mvp.ui.theme.Loss
import com.example.mvp.ui.theme.Win
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private enum class MatchTab(val label: String) {
    CALENDAR("Calendario"),
    UPCOMING("Próximos"),
    RESULTS("Resultados")
}

@Composable
fun MatchesScreen(
    modifier: Modifier = Modifier,
    matches: List<Match> = emptyList(),
    onBack: () -> Unit = {},
    onCreateMatch: () -> Unit = {},
    onEditMatch: (Match) -> Unit = {},
    onOpenMatch: (Match) -> Unit = {},
    onDeleteMatch: (Match) -> Unit = {},
    onGoDashboard: () -> Unit = {},
    onGoTraining: () -> Unit = {},
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
    var selectedTab by remember { mutableStateOf(MatchTab.CALENDAR) }
    var selectedMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDay by remember(selectedMonth) { mutableStateOf<LocalDate?>(null) }
    var toDelete by remember { mutableStateOf<Match?>(null) }

    val today = LocalDate.now()
    val finished = remember(matches) { matches.filter { it.isFinished } }
    val pendingResults = remember(matches, today) { matches.filter { it.needsResult }.sortedBy { it.date } }
    val scheduled = remember(matches) { matches.filter { !it.isFinished } }
    val futureScheduled = remember(matches, today) { matches.filter { !it.isFinished && !it.date.isBefore(today) } }
    val nextMatch = remember(futureScheduled) { futureScheduled.minByOrNull { it.date } }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateMatch,
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
        bottomBar = {
            ProFootballBottomBar(
                selected = BottomBarDestination.Matches,
                onSelect = { destination ->
                    when (destination) {
                        BottomBarDestination.Training -> onGoTraining()
                        BottomBarDestination.Players -> onGoPlayers()
                        BottomBarDestination.Dashboard -> onGoDashboard()
                        BottomBarDestination.Matches -> Unit
                        BottomBarDestination.Stats -> onGoStats()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 12.dp)
            )
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
                    .size(240.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 70.dp, y = (-50).dp)
                    .background(Brush.radialGradient(listOf(accent.copy(alpha = 0.28f), Color.Transparent)), CircleShape)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp)
                    .padding(innerPadding)
                    .padding(top = 10.dp, bottom = 2.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Header(onBack = onBack, onText = onBg)

                MatchHeroCard(
                    totalScheduled = futureScheduled.size,
                    totalFinished = finished.size,
                    pendingResults = pendingResults.size,
                    nextMatch = nextMatch,
                    accent = accent,
                    accent2 = accent2,
                    onText = onBg
                )

                if (pendingResults.isNotEmpty()) {
                    PendingResultsWarning(
                        pendingResults = pendingResults,
                        onEdit = onEditMatch,
                        danger = danger,
                        onText = onBg
                    )
                }

                TabRow(selectedTabIndex = selectedTab.ordinal, containerColor = Color.Transparent, contentColor = accent) {
                    MatchTab.entries.forEach { tab ->
                        Tab(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            text = { Text(tab.label, fontWeight = FontWeight.SemiBold) }
                        )
                    }
                }

                if (selectedTab != MatchTab.CALENDAR) {
                    SearchBox(
                        query = query,
                        onQuery = { query = it },
                        accent = accent,
                        onText = onBg
                    )
                }

                when (selectedTab) {
                    MatchTab.CALENDAR -> MatchCareerCalendar(
                        month = selectedMonth,
                        selectedDay = selectedDay,
                        matches = matches,
                        onPreviousMonth = {
                            selectedMonth = selectedMonth.minusMonths(1)
                            selectedDay = null
                        },
                        onNextMonth = {
                            selectedMonth = selectedMonth.plusMonths(1)
                            selectedDay = null
                        },
                        onSelectDay = { selectedDay = it },
                        onOpen = onOpenMatch,
                        onEdit = onEditMatch,
                        accent = accent,
                        accent2 = accent2,
                        danger = danger,
                        onText = onBg,
                        modifier = Modifier.weight(1f)
                    )

                    MatchTab.UPCOMING -> MatchList(
                        matches = scheduled
                            .filterByQuery(query)
                            .sortedWith(compareBy<Match> { it.date }.thenBy { it.id }),
                        emptyTitle = if (matches.isEmpty()) "Todavía no tienes partidos" else "No hay partidos programados",
                        emptyMessage = if (matches.isEmpty()) "Crea un resultado o programa el primer partido del calendario." else "Programa un partido desde el botón +.",
                        onOpen = onOpenMatch,
                        onEdit = onEditMatch,
                        onDelete = { toDelete = it },
                        accent = accent,
                        accent2 = accent2,
                        danger = danger,
                        onText = onBg,
                        modifier = Modifier.weight(1f)
                    )

                    MatchTab.RESULTS -> MatchList(
                        matches = finished
                            .filterByQuery(query)
                            .sortedWith(compareByDescending<Match> { it.date }.thenByDescending { it.id }),
                        emptyTitle = "Historial vacío",
                        emptyMessage = "Cuando guardes un resultado final, aparecerá aquí.",
                        onOpen = onOpenMatch,
                        onEdit = onEditMatch,
                        onDelete = { toDelete = it },
                        accent = accent,
                        accent2 = accent2,
                        danger = danger,
                        onText = onBg,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    val deleting = toDelete
    if (deleting != null) {
        AlertDialog(
            onDismissRequest = { toDelete = null },
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
            shape = RoundedCornerShape(24.dp),
            title = { Text("Eliminar partido", fontWeight = FontWeight.SemiBold) },
            text = { Text("¿Seguro que quieres eliminar el partido contra ${deleting.rival}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        toDelete = null
                        onDeleteMatch(deleting)
                    }
                ) { Text("Eliminar", color = danger, fontWeight = FontWeight.SemiBold) }
            },
            dismissButton = {
                TextButton(onClick = { toDelete = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun Header(onBack: () -> Unit, onText: Color) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = onText)
        }
        Spacer(Modifier.width(2.dp))
        Column {
            Text("Partidos", color = onText, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text("Calendario y resultados", color = onText.copy(alpha = 0.65f), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun MatchHeroCard(
    totalScheduled: Int,
    totalFinished: Int,
    pendingResults: Int,
    nextMatch: Match?,
    accent: Color,
    accent2: Color,
    onText: Color
) {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), color = GlassBase.copy(alpha = 0.08f)) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.EventAvailable, contentDescription = null, tint = accent2)
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Plan de partidos", color = onText, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = nextMatch?.let { "Próximo: ${it.rival} · ${it.dateText}" } ?: "Sin próximo partido programado",
                        color = onText.copy(alpha = 0.68f),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HeroStat("Próximos", totalScheduled.toString(), accent2, onText, Modifier.weight(1f))
                HeroStat("Pendientes", pendingResults.toString(), MaterialTheme.colorScheme.error, onText, Modifier.weight(1f))
                HeroStat("Jugados", totalFinished.toString(), accent, onText, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun PendingResultsWarning(
    pendingResults: List<Match>,
    onEdit: (Match) -> Unit,
    danger: Color,
    onText: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = danger.copy(alpha = 0.12f)
    ) {
        val mainPending = pendingResults.first()
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.EventAvailable, contentDescription = null, tint = danger)
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Pendiente de resultado",
                    color = onText,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = "${mainPending.rival} · ${mainPending.dateText}",
                    color = onText.copy(alpha = 0.72f),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (pendingResults.size > 1) {
                    Text(
                        text = "+${pendingResults.size - 1} más",
                        color = danger,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Button(
                onClick = { onEdit(mainPending) },
                colors = ButtonDefaults.buttonColors(containerColor = danger),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text("Añadir", color = ButtonTextDark, maxLines = 1)
            }
        }
    }
}

@Composable
private fun HeroStat(label: String, value: String, accent: Color, onText: Color, modifier: Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(15.dp), color = accent.copy(alpha = 0.13f)) {
        Column(modifier = Modifier.padding(vertical = 7.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, color = onText, fontWeight = FontWeight.Bold)
            Text(label, color = onText.copy(alpha = 0.65f), style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun SearchBox(query: String, onQuery: (String) -> Unit, accent: Color, onText: Color) {
    Surface(shape = RoundedCornerShape(18.dp), color = GlassBase.copy(alpha = 0.07f)) {
        OutlinedTextField(
            value = query,
            onValueChange = onQuery,
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, null) },
            placeholder = { Text("Buscar rival, competición o estado") },
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
}

@Composable
private fun MatchCareerCalendar(
    month: YearMonth,
    selectedDay: LocalDate?,
    matches: List<Match>,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onSelectDay: (LocalDate?) -> Unit,
    onOpen: (Match) -> Unit,
    onEdit: (Match) -> Unit,
    accent: Color,
    accent2: Color,
    danger: Color,
    onText: Color,
    modifier: Modifier = Modifier
) {
    val formatter = remember { DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es", "ES")) }
    val firstDay = remember(month) { month.atDay(1) }
    val leadingEmptyDays = remember(month) { firstDay.dayOfWeek.value - 1 }
    val cells = remember(month) {
        val totalCells = (((leadingEmptyDays + month.lengthOfMonth()) + 6) / 7) * 7
        (0 until totalCells).map { firstDay.plusDays((it - leadingEmptyDays).toLong()) }
    }
    val weeks = remember(cells) { cells.chunked(7) }
    val byDate = remember(matches) { matches.groupBy { it.date } }
    val monthMatches = remember(matches, month) { matches.filter { YearMonth.from(it.date) == month }.sortedBy { it.date } }
    val selectedMatches = remember(selectedDay, byDate) { selectedDay?.let { byDate[it].orEmpty() }.orEmpty().sortedBy { it.date } }
    val visibleMatches = if (selectedDay == null) monthMatches else selectedMatches
    val today = LocalDate.now()

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            shape = RoundedCornerShape(22.dp),
            color = GlassBase.copy(alpha = 0.08f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 9.dp, vertical = 7.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
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
                    IconButton(onClick = onPreviousMonth, modifier = Modifier.size(34.dp)) { Text("<", color = accent2, fontWeight = FontWeight.Bold) }
                    IconButton(onClick = onNextMonth, modifier = Modifier.size(34.dp)) { Text(">", color = accent2, fontWeight = FontWeight.Bold) }
                }

                CalendarLegend(
                    accent = accent,
                    accent2 = accent2,
                    danger = danger,
                    onText = onText
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("L", "M", "X", "J", "V", "S", "D").forEach { label ->
                        Text(
                            text = label,
                            color = onText.copy(alpha = 0.55f),
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    weeks.forEach { week ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            week.forEach { day ->
                                val dayMatches = byDate[day].orEmpty()
                                MonthDayCell(
                                    day = day,
                                    isCurrentMonth = YearMonth.from(day) == month,
                                    isToday = day == today,
                                    isSelected = selectedDay == day,
                                    matchCount = dayMatches.size,
                                    accent = dayAccent(dayMatches, accent, accent2, danger, onText),
                                    onText = onText,
                                    onClick = { onSelectDay(if (selectedDay == day) null else day) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                )
                            }
                        }
                    }
                }

                CompactCalendarFooter(
                    selectedDay = selectedDay,
                    visibleMatches = visibleMatches,
                    accent = accent,
                    accent2 = accent2,
                    danger = danger,
                    onText = onText,
                    onClearDay = { onSelectDay(null) }
                )
            }
        }
    }
}

@Composable
private fun CompactCalendarFooter(
    selectedDay: LocalDate?,
    visibleMatches: List<Match>,
    accent: Color,
    accent2: Color,
    danger: Color,
    onText: Color,
    onClearDay: () -> Unit
) {
    val footerText = when {
        selectedDay == null && visibleMatches.isEmpty() -> "Mes sin partidos programados"
        selectedDay == null -> "${visibleMatches.size} partido(s) en este mes"
        visibleMatches.isEmpty() -> "${selectedDay.format(DateTimeFormatter.ofPattern("dd/MM"))} · Sin partidos"
        visibleMatches.size == 1 -> visibleMatches.first().calendarFooterText(selectedDay)
        else -> "${selectedDay.format(DateTimeFormatter.ofPattern("dd/MM"))} · ${visibleMatches.size} partidos"
    }
    val footerColor = visibleMatches.firstOrNull()?.let { matchStatusColor(it, accent, accent2, danger) } ?: onText.copy(alpha = 0.45f)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = footerColor.copy(alpha = 0.12f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(footerColor)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = footerText,
                color = onText.copy(alpha = 0.82f),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MonthDayCell(
    day: LocalDate,
    isCurrentMonth: Boolean,
    isToday: Boolean,
    isSelected: Boolean,
    matchCount: Int,
    accent: Color,
    onText: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentAlpha = if (isCurrentMonth) 1f else 0.32f
    val hasMatch = matchCount > 0

    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = when {
            isSelected -> accent.copy(alpha = 0.30f)
            hasMatch -> accent.copy(alpha = 0.18f)
            isToday -> onText.copy(alpha = 0.11f)
            else -> onText.copy(alpha = 0.045f)
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
        ) {
            Text(
                text = day.dayOfMonth.toString(),
                color = onText.copy(alpha = contentAlpha),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isToday || isSelected || hasMatch) FontWeight.Black else FontWeight.Bold,
                modifier = Modifier.align(Alignment.TopCenter)
            )

            if (hasMatch) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 4.dp)
                        .width(18.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(accent.copy(alpha = contentAlpha))
                )
            }
        }
    }
}

@Composable
private fun CalendarLegend(accent: Color, accent2: Color, danger: Color, onText: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendDot("Jugado", accent, onText)
        LegendDot("Programado", accent2, onText)
        LegendDot("Atrasado", danger, onText)
    }
}

@Composable
private fun LegendDot(label: String, color: Color, onText: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(4.dp))
        Text(label, color = onText.copy(alpha = 0.66f), style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun MatchList(
    matches: List<Match>,
    emptyTitle: String,
    emptyMessage: String,
    onOpen: (Match) -> Unit,
    onEdit: (Match) -> Unit,
    onDelete: (Match) -> Unit,
    accent: Color,
    accent2: Color,
    danger: Color,
    onText: Color,
    modifier: Modifier = Modifier
) {
    if (matches.isEmpty()) {
        EmptyState(
            modifier = modifier,
            icon = Icons.Default.SportsSoccer,
            title = emptyTitle,
            message = emptyMessage
        )
    } else {
        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            items(matches, key = { it.id }) { match ->
                MatchRow(
                    match = match,
                    onOpen = { onOpen(match) },
                    onEdit = { onEdit(match) },
                    onDelete = { onDelete(match) },
                    accent = accent,
                    accent2 = accent2,
                    danger = danger,
                    onText = onText
                )
            }
        }
    }
}

@Composable
private fun MatchRow(
    match: Match,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    accent: Color,
    accent2: Color,
    danger: Color,
    onText: Color,
    showDelete: Boolean = true
) {
    val statusColor = matchStatusColor(match, accent, accent2, danger)
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onOpen),
        shape = RoundedCornerShape(22.dp),
        color = GlassBase.copy(alpha = 0.08f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = RoundedCornerShape(18.dp), color = statusColor.copy(alpha = 0.16f), modifier = Modifier.size(52.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (match.isFinished) Icons.Default.SportsSoccer else Icons.Default.EventAvailable,
                        contentDescription = null,
                        tint = statusColor
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(match.rival, color = onText, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${match.dateText} · ${match.competition.label}", color = onText.copy(alpha = 0.66f), style = MaterialTheme.typography.bodySmall)
                StatusPill(
                    text = when {
                        match.isFinished -> "${match.scoreText} · ${match.result.label}"
                        match.needsResult -> "Pendiente de resultado"
                        else -> "Programado · por jugar"
                    },
                    color = statusColor
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onEdit, modifier = Modifier.size(38.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = onText.copy(alpha = 0.72f))
                }
                if (showDelete) {
                    IconButton(onClick = onDelete, modifier = Modifier.size(38.dp)) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Eliminar", tint = danger.copy(alpha = 0.9f))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusPill(text: String, color: Color) {
    Surface(shape = RoundedCornerShape(999.dp), color = color.copy(alpha = 0.14f)) {
        Text(text = text, color = color, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
    }
}

private fun List<Match>.filterByQuery(query: String): List<Match> {
    return filter { match ->
        query.isBlank() ||
                match.rival.contains(query, ignoreCase = true) ||
                match.competition.label.contains(query, ignoreCase = true) ||
                match.statusLabel.contains(query, ignoreCase = true) ||
                match.dateText.contains(query, ignoreCase = true)
    }
}

private fun Match.calendarFooterText(selectedDay: LocalDate): String {
    val base = "${selectedDay.format(DateTimeFormatter.ofPattern("dd/MM"))} · $rival"
    return when {
        isFinished -> "$base · $scoreText · ${result.label}"
        needsResult -> "$base · Pendiente de resultado"
        else -> "$base · Programado"
    }
}

private fun matchStatusColor(match: Match, accent: Color, accent2: Color, danger: Color): Color {
    if (match.needsResult) return danger
    if (!match.isFinished) return accent2
    return when (match.result) {
        MatchResult.VICTORIA -> Win
        MatchResult.EMPATE -> Draw
        MatchResult.DERROTA -> Loss
    }
}

private fun dayAccent(dayMatches: List<Match>, accent: Color, accent2: Color, danger: Color, onText: Color): Color {
    return when {
        dayMatches.any { it.needsResult } -> danger
        dayMatches.any { !it.isFinished } -> accent2
        dayMatches.any { it.isFinished } -> accent
        else -> onText
    }
}