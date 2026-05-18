package com.example.mvp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mvp.data.local.entities.TrainingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingDao {

    @Query("SELECT * FROM trainings WHERE user_id = :userId ORDER BY date_epoch_day DESC, id DESC")
    fun observeByUser(userId: Long): Flow<List<TrainingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TrainingEntity): Long

    @Query(
        """
        UPDATE trainings
        SET name = :name,
            date_epoch_day = :dateEpochDay,
            duration_min = :durationMin,
            type = :type,
            is_done = :isDone
        WHERE id = :trainingId AND user_id = :userId
        """
    )
    suspend fun updateForUser(
        trainingId: Int,
        userId: Long,
        name: String,
        dateEpochDay: Long,
        durationMin: Int,
        type: String,
        isDone: Boolean
    ): Int

    @Query("DELETE FROM trainings WHERE id = :trainingId AND user_id = :userId")
    suspend fun deleteByIdForUser(trainingId: Int, userId: Long): Int
}