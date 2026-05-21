package com.example.mvp.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.example.mvp.data.local.dao.PlayerDao
import com.example.mvp.data.local.entities.PlayerEntity
import com.example.mvp.data.local.mapper.toEntity
import com.example.mvp.data.local.mapper.toModel
import com.example.mvp.domain.model.Player
import com.example.mvp.domain.model.PLAYER_MAX_AGE
import com.example.mvp.domain.model.PLAYER_MAX_NUMBER
import com.example.mvp.domain.model.PLAYER_MIN_AGE
import com.example.mvp.domain.model.PLAYER_MIN_NUMBER
import com.example.mvp.domain.model.PlayerValidator
import com.example.mvp.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlayerRepositoryImpl @Inject constructor(
    private val dao: PlayerDao
) : PlayerRepository {

    override fun observePlayers(userId: Long): Flow<List<Player>> =
        dao.observeAll(userId).map { list ->
            list.map { it.toModel() }
                .sortedWith(compareByDescending<Player> { it.rating }.thenBy { it.name })
        }

    override fun observePlayer(userId: Long, playerId: Int): Flow<Player?> =
        dao.observeById(userId, playerId).map { it?.toModel() }

    override suspend fun save(userId: Long, player: Player) {
        require(userId > 0L) { "La sesión no es válida." }

        val normalizedPlayer = player.normalized()
        PlayerValidator.validateForSave(normalizedPlayer)

        val entity = normalizedPlayer.toEntity(userId)
        validateEntity(entity)
        ensureNumberIsAvailable(entity)

        runCatching {
            entity.lineupSlot
                ?.takeIf { it.isNotBlank() }
                ?.let { slot ->
                    dao.clearLineupSlotForOtherPlayers(
                        userId = userId,
                        lineupSlot = slot,
                        excludedPlayerId = entity.id
                    )
                }

            if (player.id == 0) {
                val newId = dao.insert(entity.copy(id = 0))
                check(newId > 0L) { "No se pudo guardar el jugador." }
            } else {
                val rows = dao.updateForUser(
                    playerId = entity.id,
                    userId = userId,
                    name = entity.name,
                    position = entity.position,
                    age = entity.age,
                    number = entity.number,
                    rating = entity.rating,
                    status = entity.status,
                    level = entity.level,
                    style = entity.style,
                    lineupSlot = entity.lineupSlot
                )

                check(rows > 0) { "No se pudo actualizar el jugador. Puede que ya no exista." }
            }
        }.getOrElse { throwable ->
            if (throwable is SQLiteConstraintException) {
                error("No se pudo guardar el jugador porque el dorsal o el hueco de alineación ya está ocupado.")
            }
            throw throwable
        }
    }

    override suspend fun delete(userId: Long, player: Player) {
        require(userId > 0L) { "La sesión no es válida." }

        if (player.id == 0) return

        val rows = dao.deleteByIdForUser(playerId = player.id, userId = userId)
        check(rows > 0) { "No se pudo eliminar el jugador. Puede que ya no exista." }
    }

    private suspend fun ensureNumberIsAvailable(entity: PlayerEntity) {
        val exists = dao.existsPlayerWithNumber(
            userId = entity.userId,
            number = entity.number,
            excludedPlayerId = entity.id
        )

        require(!exists) { "El dorsal #${entity.number} ya está asignado a otro jugador." }
    }

    private fun validateEntity(entity: PlayerEntity) {
        require(entity.userId > 0L) { "La sesión no es válida." }
        require(entity.name.isNotBlank()) { "El nombre del jugador no puede estar vacío." }
        require(entity.age in PLAYER_MIN_AGE..PLAYER_MAX_AGE) {
            "La edad debe estar entre $PLAYER_MIN_AGE y $PLAYER_MAX_AGE."
        }
        require(entity.number in PLAYER_MIN_NUMBER..PLAYER_MAX_NUMBER) {
            "El dorsal debe estar entre $PLAYER_MIN_NUMBER y $PLAYER_MAX_NUMBER."
        }
        require(entity.rating in PLAYER_MIN_NUMBER..PLAYER_MAX_NUMBER) {
            "La valoración debe estar entre $PLAYER_MIN_NUMBER y $PLAYER_MAX_NUMBER."
        }
    }
}