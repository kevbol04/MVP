package com.example.mvp.data.repository

import com.example.mvp.data.local.dao.TrainingDao
import com.example.mvp.data.local.mapper.toEntity
import com.example.mvp.data.local.mapper.toModel
import com.example.mvp.domain.model.Training
import com.example.mvp.domain.repository.TrainingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TrainingRepositoryImpl @Inject constructor(
    private val dao: TrainingDao
) : TrainingRepository {

    override fun observeTrainings(userId: Long): Flow<List<Training>> {
        return dao.observeByUser(userId).map { list -> list.map { it.toModel() } }
    }

    override suspend fun upsertTraining(userId: Long, training: Training) {
        require(userId > 0L) { "La sesión no es válida." }

        val entity = training.toEntity(userId)
        validateTraining(entity.name, entity.durationMin)

        if (training.id == 0) {
            val newId = dao.insert(entity.copy(id = 0))
            check(newId > 0L) { "No se pudo guardar el entrenamiento." }
        } else {
            val rows = dao.updateForUser(
                trainingId = entity.id,
                userId = userId,
                name = entity.name,
                dateEpochDay = entity.dateEpochDay,
                durationMin = entity.durationMin,
                type = entity.type,
                isDone = entity.isDone
            )

            check(rows > 0) { "No se pudo actualizar el entrenamiento. Puede que ya no exista." }
        }
    }

    override suspend fun deleteTraining(userId: Long, training: Training) {
        require(userId > 0L) { "La sesión no es válida." }

        if (training.id == 0) return

        val rows = dao.deleteByIdForUser(trainingId = training.id, userId = userId)
        check(rows > 0) { "No se pudo eliminar el entrenamiento. Puede que ya no exista." }
    }

    override suspend fun toggleTrainingDone(userId: Long, training: Training) {
        upsertTraining(userId, training.copy(isDone = !training.isDone))
    }

    private fun validateTraining(name: String, durationMin: Int) {
        require(name.isNotBlank()) { "El nombre del entrenamiento no puede estar vacío." }
        require(durationMin > 0) { "La duración del entrenamiento debe ser mayor que 0." }
    }
}