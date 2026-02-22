package com.example.mvp.data.local.dao

import androidx.room.*
import com.example.mvp.data.local.entities.TrainingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingDao {

    @Query("SELECT * FROM trainings WHERE user_id = :userId ORDER BY id DESC")
    fun observeByUser(userId: Long): Flow<List<TrainingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TrainingEntity): Long

    @Update
    suspend fun update(entity: TrainingEntity)

    @Delete
    suspend fun delete(entity: TrainingEntity)
}