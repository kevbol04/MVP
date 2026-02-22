package com.example.mvp.data.repository

import com.example.mvp.data.local.dao.MatchDao
import com.example.mvp.data.local.mapper.toEntity
import com.example.mvp.data.local.mapper.toModel
import com.example.mvp.domain.repository.MatchRepository
import com.example.mvp.ui.screens.matches.Match
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
        if (match.id == 0) dao.insert(match.toEntity(userId))
        else dao.update(match.toEntity(userId))
    }

    override suspend fun deleteMatch(userId: Long, match: Match) {
        dao.delete(match.toEntity(userId))
    }
}