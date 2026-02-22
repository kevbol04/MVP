package com.example.mvp.domain.model
import com.example.mvp.ui.screens.training.TrainingType

data class Training(
    val id: Long = 0L,
    val name: String,
    val dateText: String,
    val durationMin: Int,
    val type: TrainingType
)