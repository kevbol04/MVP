package com.example.mvp.domain.model

data class Club(
    val id: Int = 0,
    val name: String = "",
    val season: String = "",
    val stadium: String = "",
    val city: String = "",
    val coachName: String = "",
    val badgeId: String = ClubBadgeDefaults.DEFAULT_ID
) {
    val displayName: String
        get() = name.ifBlank { "Mi club" }

    val displaySeason: String
        get() = season.ifBlank { "Temporada sin definir" }

    val displayCoach: String
        get() = coachName.ifBlank { "Entrenador sin definir" }
}