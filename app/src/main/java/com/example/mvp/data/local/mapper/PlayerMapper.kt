package com.example.mvp.data.local.mapper

import com.example.mvp.data.local.entities.PlayerEntity
import com.example.mvp.domain.model.Player
import com.example.mvp.domain.model.PlayerLevel
import com.example.mvp.domain.model.PlayerPosition
import com.example.mvp.domain.model.PlayerStatus
import com.example.mvp.domain.model.PlayerStyle
import com.example.mvp.domain.model.calculateRating
import com.example.mvp.domain.model.defaultStyleFor
import com.example.mvp.domain.model.stylesFor

private fun safePosition(value: String): PlayerPosition =
    runCatching { PlayerPosition.valueOf(value) }.getOrElse { PlayerPosition.MED }

private fun safeStatus(value: String): PlayerStatus =
    runCatching { PlayerStatus.valueOf(value) }.getOrElse { PlayerStatus.DISPONIBLE }

private fun safeLevel(value: String): PlayerLevel =
    runCatching { PlayerLevel.valueOf(value) }.getOrElse { PlayerLevel.BUENO }

private fun safeStyle(value: String, position: PlayerPosition): PlayerStyle {
    val parsed = runCatching { PlayerStyle.valueOf(value) }.getOrElse { defaultStyleFor(position) }
    return if (parsed in stylesFor(position)) parsed else defaultStyleFor(position)
}

fun PlayerEntity.toModel(): Player {
    val safePosition = safePosition(position)
    val safeLevel = safeLevel(level)
    val safeStyle = safeStyle(style, safePosition)
    val calculatedRating = calculateRating(safePosition, safeLevel, safeStyle)

    return Player(
        id = id,
        name = name,
        position = safePosition,
        age = age,
        number = number,
        rating = calculatedRating,
        status = safeStatus(status),
        level = safeLevel,
        style = safeStyle,
        lineupSlot = lineupSlot
    )
}

fun Player.toEntity(userId: Long): PlayerEntity {
    val safeStyle = if (style in stylesFor(position)) style else defaultStyleFor(position)
    val calculatedRating = calculateRating(position, level, safeStyle)

    return PlayerEntity(
        id = id,
        userId = userId,
        name = name,
        position = position.name,
        age = age,
        number = number,
        rating = calculatedRating,
        status = status.name,
        level = level.name,
        style = safeStyle.name,
        lineupSlot = lineupSlot
    )
}