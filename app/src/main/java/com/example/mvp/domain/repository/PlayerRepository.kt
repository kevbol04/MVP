package com.example.mvp.domain.repository

import com.example.mvp.ui.screens.players.Player
import kotlinx.coroutines.flow.Flow

interface PlayerRepository {
    fun observePlayers(userId: Long): Flow<List<Player>>
    fun observePlayer(userId: Long, playerId: Int): Flow<Player?>
    suspend fun save(userId: Long, player: Player)
    suspend fun delete(userId: Long, player: Player)
}