package com.example.mvp.domain.repository

import com.example.mvp.ui.screens.matches.Match
import kotlinx.coroutines.flow.Flow

interface MatchRepository {
    fun observeMatches(userId: Long): Flow<List<Match>>
    fun observeMatch(userId: Long, matchId: Int): Flow<Match?>
    suspend fun upsertMatch(userId: Long, match: Match)
    suspend fun deleteMatch(userId: Long, match: Match)
}