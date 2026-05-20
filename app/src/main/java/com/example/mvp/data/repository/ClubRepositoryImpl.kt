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
        require(userId > 0L) { "La sesión no es válida." }
        require(club.name.isNotBlank()) { "El nombre del club no puede estar vacío." }
        require(club.stadium.isNotBlank()) { "El estadio no puede estar vacío." }
        require(club.city.isNotBlank()) { "La ciudad no puede estar vacía." }
        require(club.coachName.isNotBlank()) { "El nombre del entrenador no puede estar vacío." }

        val existing = dao.getClub(userId)
        val entity = club.toEntity(userId)

        if (existing == null) {
            val newId = dao.insert(entity.copy(id = 0))
            check(newId > 0L) { "No se pudo guardar el club." }
        } else {
            val rows = dao.update(entity.copy(id = existing.id))
            check(rows > 0) { "No se pudo actualizar el club. Puede que ya no exista." }
        }
    }

    override suspend fun updateSelectedFormation(userId: Long, formationId: String) {
        require(userId > 0L) { "La sesión no es válida." }

        val normalizedFormationId = formationId.trim().ifBlank { Club.DEFAULT_FORMATION_ID }
        val rows = dao.updateSelectedFormation(
            userId = userId,
            formationId = normalizedFormationId
        )

        check(rows > 0) { "No se pudo actualizar la formación porque el club no existe." }
    }
}