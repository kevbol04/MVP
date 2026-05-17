package com.example.mvp.domain.model

data class Player(
    val id: Int = 0,
    val name: String,
    val position: PlayerPosition,
    val age: Int,
    val number: Int,
    val rating: Int,
    val status: PlayerStatus,
    val lineupSlot: String? = null
)