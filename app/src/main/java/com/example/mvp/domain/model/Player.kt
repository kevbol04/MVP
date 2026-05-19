package com.example.mvp.domain.model

data class Player(
    val id: Int = 0,
    val name: String,
    val position: PlayerPosition,
    val age: Int,
    val number: Int,
    val rating: Int,
    val status: PlayerStatus,
    val level: PlayerLevel = PlayerLevel.BUENO,
    val style: PlayerStyle = defaultStyleFor(position),
    val lineupSlot: String? = null
) {
    /**
     * La valoración real del jugador siempre debe salir de la misma regla:
     * posición + nivel + estilo.
     *
     * El campo rating se mantiene por compatibilidad con la base de datos y con las pantallas
     * que ya lo usan, pero antes de guardar o mapear se normaliza con este valor.
     */
    val calculatedRating: Int
        get() = calculateRating(position, level, style)

    fun withCalculatedRating(): Player =
        if (rating == calculatedRating) this else copy(rating = calculatedRating)
}