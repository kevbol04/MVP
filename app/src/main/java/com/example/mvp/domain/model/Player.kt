package com.example.mvp.domain.model

const val PLAYER_MIN_AGE = 16
const val PLAYER_MAX_AGE = 40
const val PLAYER_MIN_NUMBER = 1
const val PLAYER_MAX_NUMBER = 99
const val PLAYER_MIN_NAME_LENGTH = 3
const val PLAYER_MAX_NAME_LENGTH = 40

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
     * Normaliza únicamente datos seguros, sin alterar valores de negocio.
     *
     * Importante: aquí NO se corrigen edad ni dorsal con coerceIn().
     * Si llegan valores inválidos, deben fallar en PlayerValidator para que el usuario
     * sepa qué dato está mal. Corregirlos en silencio podía guardar una edad/dorsal
     * distinto al introducido y ocultaba errores reales.
     */
    fun normalized(): Player = copy(
        name = name.trim(),
        style = effectiveStyle,
        lineupSlot = lineupSlot
            ?.trim()
            ?.takeIf { it.isNotBlank() && status != PlayerStatus.LESIONADO }
    )

    /**
     * Reparación defensiva para datos antiguos o corruptos que ya estén en la base de datos.
     * No debe usarse antes de guardar datos nuevos del usuario.
     */
    fun repairedFromStorage(): Player = copy(
        name = name.trim().ifBlank { "Jugador sin nombre" },
        age = age.coerceIn(PLAYER_MIN_AGE, PLAYER_MAX_AGE),
        number = number.coerceIn(PLAYER_MIN_NUMBER, PLAYER_MAX_NUMBER),
        style = effectiveStyle,
        lineupSlot = lineupSlot
            ?.trim()
            ?.takeIf { it.isNotBlank() && status != PlayerStatus.LESIONADO }
    )
}