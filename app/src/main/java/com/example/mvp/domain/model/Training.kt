package com.example.mvp.domain.model

data class Training(
    val id: Int = 0,
    val name: String,
    val dateText: String,
    val durationMin: Int,
    val type: TrainingType,
    val isDone: Boolean = false
)