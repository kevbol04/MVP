package com.example.mvp.ui.screens.training

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

enum class TrainingType(val label: String) {
    FUERZA("Fuerza"),
    RESISTENCIA("Resistencia"),
    VELOCIDAD("Velocidad"),
    TECNICA("Técnica"),
    RECUPERACION("Recuperación")
}

data class Training(
    val id: Int,
    val name: String,
    val dateText: String,
    val durationMin: Int,
    val type: TrainingType,
    val isRecent: Boolean = false
)

private enum class TrainingTab { Todos, Recientes }

@Composable
fun TrainingsScreen(
    modifier: Modifier = Modifier,
    trainings: List<Training> = sampleTrainings(),
    onBack: () -> Unit = {},
    onCreateTraining: () -> Unit = {},
    onEditTraining: (Training) -> Unit = {}
) {
    val bgTop = Color(0xFF0B1220)
    val bgMid = Color(0xFF0E2A3B)
    val accent = Color(0xFF00E5FF)
    val accent2 = Color(0xFF7C4DFF)

    var query by remember { mutableStateOf("") }
    var tab by remember { mutableStateOf(TrainingTab.Todos) }

    val filtered = remember(trainings, query, tab) {
        trainings.filter { t ->
            val matchesTab = when (tab) {
                TrainingTab.Todos -> true
                TrainingTab.Recientes -> t.isRecent
            }
            val matchesQuery =
                query.isBlank() ||
                        t.name.contains(query, ignoreCase = true) ||
                        t.type.label.contains(query, ignoreCase = true)

            matchesTab && matchesQuery
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(bgTop, bgMid, bgTop)))
            .padding(horizontal = 20.dp)
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
                Text(
                    text = "Entrenamientos",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, null) },
                placeholder = { Text("Buscar") },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.White.copy(alpha = 0.18f),
                    focusedBorderColor = accent,
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

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.06f)
            ) {
                Row(
                    modifier = Modifier.padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SegTab(
                        text = "Todos",
                        selected = tab == TrainingTab.Todos,
                        accent = accent,
                        onClick = { tab = TrainingTab.Todos }
                    )

                    SegTab(
                        text = "Recientes",
                        selected = tab == TrainingTab.Recientes,
                        accent = accent,
                        onClick = { tab = TrainingTab.Recientes }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filtered) { t ->
                    TrainingRow(
                        training = t,
                        accent = accent,
                        onEdit = { onEditTraining(t) }
                    )
                }
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
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF061018))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Crear Entrenamiento",
                            color = Color(0xFF061018),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.SegTab(
    text: String,
    selected: Boolean,
    accent: Color,
    onClick: () -> Unit
) {
    val bg = if (selected) accent.copy(alpha = 0.18f) else Color.Transparent
    val fg = if (selected) accent else Color.White.copy(alpha = 0.75f)

    Surface(
        modifier = Modifier
            .weight(1f)
            .height(36.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = bg,
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                color = fg,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun TrainingRow(
    training: Training,
    accent: Color,
    onEdit: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Color.White.copy(alpha = 0.08f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = training.name,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${training.dateText} · ${training.durationMin} min · ${training.type.label}",
                    color = Color.White.copy(alpha = 0.70f),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = accent)
            }
        }
    }
}

private fun sampleTrainings(): List<Training> = listOf(
    Training(1, "Entrenamiento de Pierna", "12/11/2025", 90, TrainingType.FUERZA, isRecent = true),
    Training(2, "Técnica de pase", "10/11/2025", 60, TrainingType.TECNICA, isRecent = true),
    Training(3, "Resistencia aeróbica", "05/11/2025", 45, TrainingType.RESISTENCIA, isRecent = false),
)