package com.example.mvp.data.repository

import com.example.mvp.data.local.dao.MatchDao
import com.example.mvp.data.local.mapper.toEntity
import com.example.mvp.data.local.mapper.toModel
import com.example.mvp.domain.model.Match
import com.example.mvp.domain.repository.MatchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MatchRepositoryImpl @Inject constructor(
    private val dao: MatchDao
) : MatchRepository {

    override fun observeMatches(userId: Long): Flow<List<Match>> {
        return dao.observeByUser(userId).map { list -> list.map { it.toModel() } }
    }

    override fun observeMatch(userId: Long, matchId: Int): Flow<Match?> {
        return dao.observeById(userId, matchId).map { it?.toModel() }
    }

    override suspend fun upsertMatch(userId: Long, match: Match) {
        require(userId > 0L) { "La sesión no es válida." }

        val entity = match.toEntity(userId)
        validateMatch(entity.rival, entity.goalsFor, entity.goalsAgainst)

        if (match.id == 0) {
            val newId = dao.insert(entity.copy(id = 0))
            check(newId > 0L) { "No se pudo guardar el partido." }
        } else {
            val rows = dao.updateForUser(
                matchId = entity.id,
                userId = userId,
                rival = entity.rival,
                dateEpochDay = entity.dateEpochDay,
                competition = entity.competition,
                goalsFor = entity.goalsFor,
                goalsAgainst = entity.goalsAgainst
            )

            check(rows > 0) { "No se pudo actualizar el partido. Puede que ya no exista." }
        }
    }

    override suspend fun deleteMatch(userId: Long, match: Match) {
        require(userId > 0L) { "La sesión no es válida." }

        if (match.id == 0) return

        val rows = dao.deleteByIdForUser(matchId = match.id, userId = userId)
        check(rows > 0) { "No se pudo eliminar el partido. Puede que ya no exista." }
    }

    private fun validateMatch(rival: String, goalsFor: Int, goalsAgainst: Int) {
        require(rival.isNotBlank()) { "El rival no puede estar vacío." }
        require(goalsFor >= 0) { "Los goles a favor no pueden ser negativos." }
        require(goalsAgainst >= 0) { "Los goles en contra no pueden ser negativos." }
    }
}