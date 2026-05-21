package com.example.mvp.domain.model

data class Club(
    val id: Int = 0,
    val name: String = "",
    val stadium: String = "",
    val city: String = "",
    val coachName: String = "",
    val badgeId: String = ClubBadgeDefaults.DEFAULT_ID,
    val customBadgePath: String? = null,
    val selectedFormationId: String = DEFAULT_FORMATION_ID
) {
    val displayName: String
        get() = name.ifBlank { "Mi club" }

    val displayCoach: String
        get() = coachName.ifBlank { "Entrenador sin definir" }

    companion object {
        const val DEFAULT_FORMATION_ID = "442"
    }
}