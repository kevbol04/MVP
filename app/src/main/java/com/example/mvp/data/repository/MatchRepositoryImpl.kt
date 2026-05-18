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
        val entity = match.toEntity(userId)

        if (match.id == 0) {
            dao.insert(entity.copy(id = 0))
        } else {
            dao.updateForUser(
                matchId = entity.id,
                userId = userId,
                rival = entity.rival,
                dateEpochDay = entity.dateEpochDay,
                competition = entity.competition,
                goalsFor = entity.goalsFor,
                goalsAgainst = entity.goalsAgainst
            )
        }
    }

    override suspend fun deleteMatch(userId: Long, match: Match) {
        if (match.id != 0) {
            dao.deleteByIdForUser(matchId = match.id, userId = userId)
        }
    }
}