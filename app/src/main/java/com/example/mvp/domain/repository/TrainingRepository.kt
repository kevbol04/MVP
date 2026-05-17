package com.example.mvp.domain.repository

import com.example.mvp.domain.model.Training
import kotlinx.coroutines.flow.Flow

interface TrainingRepository {
    fun observeTrainings(userId: Long): Flow<List<Training>>
    suspend fun upsertTraining(userId: Long, training: Training)
    suspend fun deleteTraining(userId: Long, training: Training)
    suspend fun toggleTrainingDone(userId: Long, training: Training)
}