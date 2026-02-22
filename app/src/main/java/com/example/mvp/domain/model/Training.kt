package com.example.mvp.domain.model

enum class TrainingType(val label: String) {
    FUERZA("Fuerza"),
    RESISTENCIA("Resistencia"),
    VELOCIDAD("Velocidad"),
    TECNICA("Técnica"),
    RECUPERACION("Recuperación")
}

data class Training(
    val id: Long = 0L,
    val name: String,
    val dateText: String,
    val durationMin: Int,
    val type: TrainingType
)