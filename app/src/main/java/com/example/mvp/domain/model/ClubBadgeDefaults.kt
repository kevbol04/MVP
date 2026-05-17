package com.example.mvp.domain.model

object ClubBadgeDefaults {
    const val DEFAULT_ID = "royal_blue"

    val ids: List<String> = listOf(
        "royal_blue",
        "galaxy_purple",
        "ocean_cyan",
        "green_star",
        "fire_red",
        "gold_crown"
    )

    fun sanitize(id: String): String {
        return ids.firstOrNull { it == id } ?: DEFAULT_ID
    }
}