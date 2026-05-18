package com.example.mvp.data.repository

import com.example.mvp.data.local.dao.PlayerDao
import com.example.mvp.data.local.mapper.toEntity
import com.example.mvp.data.local.mapper.toModel
import com.example.mvp.domain.model.Player
import com.example.mvp.domain.repository.PlayerRepository
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
            dao.updateForUser(
                playerId = entity.id,
                userId = userId,
                name = entity.name,
                position = entity.position,
                age = entity.age,
                number = entity.number,
                rating = entity.rating,
                status = entity.status,
                lineupSlot = entity.lineupSlot
            )
        }
    }

    override suspend fun delete(userId: Long, player: Player) {
        if (player.id != 0) {
            dao.deleteByIdForUser(playerId = player.id, userId = userId)
        }
    }
}