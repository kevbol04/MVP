package com.example.mvp.domain.model

import kotlin.math.roundToInt

data class PlayerAttributes(
    val ritmo: Int,
    val tiro: Int,
    val pase: Int,
    val regate: Int,
    val defensa: Int,
    val fisico: Int,
    val estirada: Int? = null,
    val paradas: Int? = null,
    val reflejos: Int? = null,
    val colocacion: Int? = null,
    val saque: Int? = null
) {
    fun fieldAttributes(): List<Pair<String, Int>> = listOf(
        "Ritmo" to ritmo,
        "Tiro" to tiro,
        "Pase" to pase,
        "Regate" to regate,
        "Defensa" to defensa,
        "Físico" to fisico
    )

    fun goalkeeperAttributes(): List<Pair<String, Int>> = listOf(
        "Estirada" to (estirada ?: ritmo),
        "Paradas" to (paradas ?: defensa),
        "Reflejos" to (reflejos ?: regate),
        "Colocación" to (colocacion ?: fisico),
        "Saque" to (saque ?: pase)
    )
}

fun defaultStyleFor(position: PlayerPosition): PlayerStyle = when (position) {
    PlayerPosition.POR -> PlayerStyle.SEGURO
    PlayerPosition.DEF -> PlayerStyle.DEFENSIVO
    PlayerPosition.MED -> PlayerStyle.ORGANIZADOR
    PlayerPosition.DEL -> PlayerStyle.FINALIZADOR
}

fun stylesFor(position: PlayerPosition): List<PlayerStyle> = when (position) {
    PlayerPosition.POR -> listOf(PlayerStyle.SEGURO, PlayerStyle.REFLEJOS, PlayerStyle.BUEN_SAQUE, PlayerStyle.COMPLETO)
    PlayerPosition.DEF -> listOf(PlayerStyle.DEFENSIVO, PlayerStyle.FISICO, PlayerStyle.RAPIDO, PlayerStyle.LATERAL_OFENSIVO, PlayerStyle.LATERAL_DEFENSIVO, PlayerStyle.COMPLETO)
    PlayerPosition.MED -> listOf(PlayerStyle.ORGANIZADOR, PlayerStyle.BOX_TO_BOX, PlayerStyle.DEFENSIVO, PlayerStyle.OFENSIVO, PlayerStyle.COMPLETO)
    PlayerPosition.DEL -> listOf(PlayerStyle.FINALIZADOR, PlayerStyle.RAPIDO, PlayerStyle.REGATEADOR, PlayerStyle.FISICO, PlayerStyle.COMPLETO)
}

fun generateAttributes(position: PlayerPosition, level: PlayerLevel, style: PlayerStyle): PlayerAttributes {
    val base = level.base

    fun v(delta: Int): Int = (base + delta).coerceIn(40, 99)

    val attrs = when (position) {
        PlayerPosition.POR -> when (style) {
            PlayerStyle.REFLEJOS -> PlayerAttributes(v(-8), v(-28), v(-5), v(-12), v(-10), v(-4), estirada = v(3), paradas = v(4), reflejos = v(8), colocacion = v(0), saque = v(-5))
            PlayerStyle.BUEN_SAQUE -> PlayerAttributes(v(-10), v(-26), v(2), v(-14), v(-8), v(-3), estirada = v(-1), paradas = v(0), reflejos = v(2), colocacion = v(2), saque = v(9))
            PlayerStyle.COMPLETO -> PlayerAttributes(v(-7), v(-25), v(-2), v(-10), v(-7), v(-2), estirada = v(3), paradas = v(3), reflejos = v(3), colocacion = v(3), saque = v(3))
            else -> PlayerAttributes(v(-8), v(-27), v(-4), v(-12), v(-8), v(-2), estirada = v(2), paradas = v(6), reflejos = v(2), colocacion = v(5), saque = v(-1))
        }

        PlayerPosition.DEF -> when (style) {
            PlayerStyle.FISICO -> PlayerAttributes(v(-2), v(-16), v(-5), v(-8), v(5), v(10))
            PlayerStyle.RAPIDO -> PlayerAttributes(v(9), v(-14), v(-4), v(0), v(3), v(2))
            PlayerStyle.LATERAL_OFENSIVO -> PlayerAttributes(v(7), v(-9), v(5), v(4), v(0), v(0))
            PlayerStyle.LATERAL_DEFENSIVO -> PlayerAttributes(v(2), v(-15), v(-2), v(-4), v(8), v(5))
            PlayerStyle.COMPLETO -> PlayerAttributes(v(2), v(-10), v(2), v(0), v(5), v(4))
            else -> PlayerAttributes(v(0), v(-16), v(-4), v(-6), v(9), v(6))
        }

        PlayerPosition.MED -> when (style) {
            PlayerStyle.BOX_TO_BOX -> PlayerAttributes(v(3), v(0), v(4), v(2), v(4), v(6))
            PlayerStyle.DEFENSIVO -> PlayerAttributes(v(-1), v(-8), v(3), v(-2), v(8), v(6))
            PlayerStyle.OFENSIVO -> PlayerAttributes(v(1), v(5), v(6), v(6), v(-5), v(0))
            PlayerStyle.COMPLETO -> PlayerAttributes(v(2), v(1), v(5), v(4), v(2), v(3))
            else -> PlayerAttributes(v(-1), v(-3), v(9), v(5), v(0), v(0))
        }

        PlayerPosition.DEL -> when (style) {
            PlayerStyle.RAPIDO -> PlayerAttributes(v(10), v(2), v(-7), v(5), v(-25), v(-2))
            PlayerStyle.REGATEADOR -> PlayerAttributes(v(5), v(3), v(-3), v(10), v(-24), v(-5))
            PlayerStyle.FISICO -> PlayerAttributes(v(0), v(5), v(-8), v(-3), v(-22), v(10))
            PlayerStyle.COMPLETO -> PlayerAttributes(v(4), v(6), v(-2), v(5), v(-20), v(3))
            else -> PlayerAttributes(v(2), v(10), v(-7), v(3), v(-24), v(1))
        }
    }

    return attrs
}

fun calculateRating(position: PlayerPosition, level: PlayerLevel, style: PlayerStyle): Int {
    val attrs = generateAttributes(position, level, style)

    val rating = when (position) {
        PlayerPosition.POR -> {
            val reflejos = attrs.reflejos ?: 0
            val paradas = attrs.paradas ?: 0
            val estirada = attrs.estirada ?: 0
            val colocacion = attrs.colocacion ?: 0
            val saque = attrs.saque ?: 0
            reflejos * 0.30 + paradas * 0.30 + estirada * 0.20 + colocacion * 0.15 + saque * 0.05
        }
        PlayerPosition.DEF -> attrs.defensa * 0.40 + attrs.fisico * 0.25 + attrs.ritmo * 0.15 + attrs.pase * 0.10 + attrs.regate * 0.05 + attrs.tiro * 0.05
        PlayerPosition.MED -> attrs.pase * 0.30 + attrs.regate * 0.25 + attrs.fisico * 0.15 + attrs.defensa * 0.15 + attrs.ritmo * 0.10 + attrs.tiro * 0.05
        PlayerPosition.DEL -> attrs.tiro * 0.35 + attrs.ritmo * 0.25 + attrs.regate * 0.20 + attrs.fisico * 0.10 + attrs.pase * 0.10
    }

    return rating.roundToInt().coerceIn(40, 99)
}