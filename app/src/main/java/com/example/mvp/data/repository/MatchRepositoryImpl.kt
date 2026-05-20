package com.example.mvp.data.repository

import com.example.mvp.data.local.dao.MatchDao
import com.example.mvp.data.local.mapper.toEntity
import com.example.mvp.data.local.mapper.toModel
import com.example.mvp.domain.model.Match
import com.example.mvp.domain.repository.MatchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
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

        val normalized = match.copy(
            rival = match.rival.trim(),
            goalsFor = if (match.isFinished) match.goalsFor else 0,
            goalsAgainst = if (match.isFinished) match.goalsAgainst else 0
        )
        val entity = normalized.toEntity(userId)
        validateMatch(entity.rival, entity.dateEpochDay, entity.goalsFor, entity.goalsAgainst, entity.isFinished)
        validateMatchCalendarSpacing(userId = userId, matchId = entity.id, dateEpochDay = entity.dateEpochDay)

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
                goalsAgainst = entity.goalsAgainst,
                isFinished = entity.isFinished
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


    private suspend fun validateMatchCalendarSpacing(userId: Long, matchId: Int, dateEpochDay: Long) {
        val sameDayCount = dao.countByDateForUser(
            userId = userId,
            dateEpochDay = dateEpochDay,
            matchId = matchId
        )
        require(sameDayCount == 0) {
            "Ya hay un partido guardado ese día. No puedes tener dos partidos en la misma fecha."
        }

        val conflict = dao.findRestConflictForUser(
            userId = userId,
            matchId = matchId,
            dateEpochDay = dateEpochDay,
            minDateEpochDay = dateEpochDay - MIN_REST_DAYS_BETWEEN_MATCHES,
            maxDateEpochDay = dateEpochDay + MIN_REST_DAYS_BETWEEN_MATCHES
        )

        if (conflict != null) {
            val conflictDate = LocalDate.ofEpochDay(conflict.dateEpochDay).format(repositoryDateFormatter)
            throw IllegalArgumentException(
                "Debe haber 2 días completos de descanso entre partidos. Conflicto con ${conflict.rival} ($conflictDate)."
            )
        }
    }

    private fun validateMatch(
        rival: String,
        dateEpochDay: Long,
        goalsFor: Int,
        goalsAgainst: Int,
        isFinished: Boolean
    ) {
        require(rival.isNotBlank()) { "El rival no puede estar vacío." }
        require(rival.length >= 3) { "El rival debe tener al menos 3 caracteres." }
        require(goalsFor >= 0) { "Los goles a favor no pueden ser negativos." }
        require(goalsAgainst >= 0) { "Los goles en contra no pueden ser negativos." }

        val date = LocalDate.ofEpochDay(dateEpochDay)
        require(!isFinished || !date.isAfter(LocalDate.now())) {
            "No puedes guardar como finalizado un partido con fecha futura. Guárdalo como programado."
        }
    }
}


private const val MIN_REST_DAYS_BETWEEN_MATCHES = 2L
private val repositoryDateFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("es", "ES"))
