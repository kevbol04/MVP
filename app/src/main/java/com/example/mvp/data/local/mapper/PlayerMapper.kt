package com.example.mvp.data.local.mapper

import com.example.mvp.data.local.entities.PlayerEntity
import com.example.mvp.ui.screens.players.Player
import com.example.mvp.ui.screens.players.PlayerPosition
import com.example.mvp.ui.screens.players.PlayerStatus

private fun safePosition(value: String): PlayerPosition =
    runCatching { PlayerPosition.valueOf(value) }.getOrElse { PlayerPosition.MED }

private fun safeStatus(value: String): PlayerStatus =
    runCatching { PlayerStatus.valueOf(value) }.getOrElse { PlayerStatus.TITULAR }

fun PlayerEntity.toModel(): Player = Player(
    id = id,
    name = name,
    position = safePosition(position),
    age = age,
    number = number,
    rating = rating,
    status = safeStatus(status)
)

fun Player.toEntity(userId: Long): PlayerEntity = PlayerEntity(
    id = id,
    userId = userId,
    name = name,
    position = position.name,
    age = age,
    number = number,
    rating = rating,
    status = status.name
)