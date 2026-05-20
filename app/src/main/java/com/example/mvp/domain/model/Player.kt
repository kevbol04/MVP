package com.example.mvp.domain.model

const val PLAYER_MIN_AGE = 16
const val PLAYER_MAX_AGE = 40

/**
 * Modelo de dominio del jugador.
 *
 * La valoración no se recibe desde fuera ni se guarda como estado editable del modelo,
 * porque eso permitía tener dos verdades distintas:
 * - rating
 * - position + level + style
 *
 * Ahora la única fuente de verdad es position + level + style.
 * Si el estilo no corresponde a la posición, se usa automáticamente el estilo por defecto
 * de esa posición para evitar estados imposibles.
 */
data class Player(
    val id: Int = 0,
    val name: String,
    val position: PlayerPosition,
    val age: Int,
    val number: Int,
    val status: PlayerStatus,
    val level: PlayerLevel = PlayerLevel.BUENO,
    val style: PlayerStyle = defaultStyleFor(position),
    val lineupSlot: String? = null
) {
    val effectiveStyle: PlayerStyle
        get() = style.takeIf { it in stylesFor(position) } ?: defaultStyleFor(position)

    val rating: Int
        get() = calculateRating(position, level, effectiveStyle)

    /**
     * Devuelve un jugador en un estado coherente antes de persistirlo o mostrarlo.
     */
    fun normalized(): Player = copy(
        name = name.trim(),
        age = age.coerceIn(PLAYER_MIN_AGE, PLAYER_MAX_AGE),
        number = number.coerceIn(1, 99),
        style = effectiveStyle,
        lineupSlot = lineupSlot?.takeIf { it.isNotBlank() && status != PlayerStatus.LESIONADO }
    )
}