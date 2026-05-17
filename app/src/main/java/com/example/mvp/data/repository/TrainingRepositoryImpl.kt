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
        if (training.id == 0) {
            dao.insert(training.toEntity(userId))
        } else {
            dao.update(training.toEntity(userId))
        }
    }

    override suspend fun deleteTraining(userId: Long, training: Training) {
        dao.delete(training.toEntity(userId))
    }

    override suspend fun toggleTrainingDone(userId: Long, training: Training) {
        dao.update(training.copy(isDone = !training.isDone).toEntity(userId))
    }
}