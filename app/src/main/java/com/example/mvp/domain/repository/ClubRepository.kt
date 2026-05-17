package com.example.mvp.domain.repository

import com.example.mvp.domain.model.Club
import kotlinx.coroutines.flow.Flow

interface ClubRepository {
    fun observeClub(userId: Long): Flow<Club?>
    suspend fun save(userId: Long, club: Club)
}