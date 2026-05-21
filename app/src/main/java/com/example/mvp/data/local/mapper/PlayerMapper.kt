package com.example.mvp.data.local.mapper

import com.example.mvp.data.local.entities.PlayerEntity
import com.example.mvp.domain.model.Player
import com.example.mvp.domain.model.PlayerLevel
import com.example.mvp.domain.model.PlayerPosition
import com.example.mvp.domain.model.PlayerStatus
import com.example.mvp.domain.model.PlayerStyle
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

    return Player(
        id = id,
        name = name,
        position = safePosition,
        age = age,
        number = number,
        status = safeStatus(status),
        level = safeLevel,
        style = safeStyle,
        lineupSlot = lineupSlot
    ).repairedFromStorage()
}

fun Player.toEntity(userId: Long): PlayerEntity {
    val normalizedPlayer = normalized()

    return PlayerEntity(
        id = normalizedPlayer.id,
        userId = userId,
        name = normalizedPlayer.name,
        position = normalizedPlayer.position.name,
        age = normalizedPlayer.age,
        number = normalizedPlayer.number,
        status = normalizedPlayer.status.name,
        level = normalizedPlayer.level.name,
        style = normalizedPlayer.effectiveStyle.name,
        lineupSlot = normalizedPlayer.lineupSlot
    )
}