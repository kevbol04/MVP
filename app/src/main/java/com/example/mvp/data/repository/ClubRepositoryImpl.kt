package com.example.mvp.data.repository

import com.example.mvp.data.local.dao.ClubDao
import com.example.mvp.data.local.mapper.toEntity
import com.example.mvp.data.local.mapper.toModel
import com.example.mvp.domain.model.Club
import com.example.mvp.domain.repository.ClubRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ClubRepositoryImpl @Inject constructor(
    private val dao: ClubDao
) : ClubRepository {

    override fun observeClub(userId: Long): Flow<Club?> =
        dao.observeClub(userId).map { it?.toModel() }

    override suspend fun hasClub(userId: Long): Boolean {
        if (userId <= 0L) return false

        return dao.getClub(userId) != null
    }

    override suspend fun save(userId: Long, club: Club) {
        val existing = dao.getClub(userId)
        val entity = club.toEntity(userId)

        if (existing == null) {
            dao.insert(entity.copy(id = 0))
        } else {
            dao.update(entity.copy(id = existing.id))
        }
    }

    override suspend fun updateSelectedFormation(userId: Long, formationId: String) {
        if (userId <= 0L || formationId.isBlank()) return

        if (dao.getClub(userId) == null) return

        dao.updateSelectedFormation(
            userId = userId,
            formationId = formationId.trim().ifBlank { Club.DEFAULT_FORMATION_ID }
        )
    }
}