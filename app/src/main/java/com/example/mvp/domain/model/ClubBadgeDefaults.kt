package com.example.mvp.domain.model

object ClubBadgeDefaults {
    const val DEFAULT_ID = "galaxy_purple"
    const val CUSTOM_ID = "custom"

    val ids: List<String> = listOf(
        CUSTOM_ID,
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