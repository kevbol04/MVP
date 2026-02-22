package com.example.mvp.data.repository

import com.example.mvp.data.local.dao.PlayerDao
import com.example.mvp.data.local.mapper.toEntity
import com.example.mvp.data.local.mapper.toModel
import com.example.mvp.domain.repository.PlayerRepository
import com.example.mvp.ui.screens.players.Player
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlayerRepositoryImpl @Inject constructor(
    private val dao: PlayerDao
) : PlayerRepository {

    override fun observePlayers(userId: Long): Flow<List<Player>> =
        dao.observeAll(userId).map { list -> list.map { it.toModel() } }

    override fun observePlayer(userId: Long, playerId: Int): Flow<Player?> =
        dao.observeById(userId, playerId).map { it?.toModel() }

    override suspend fun save(userId: Long, player: Player) {
        val entity = player.toEntity(userId)

        if (player.id == 0) {
            dao.insert(entity.copy(id = 0))
        } else {
            dao.update(entity)
        }
    }

    override suspend fun delete(userId: Long, player: Player) {
        dao.delete(player.toEntity(userId))
    }
}