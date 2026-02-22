package com.example.mvp.data.local.dao

import androidx.room.*
import com.example.mvp.data.local.entities.PlayerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {

    @Query("SELECT * FROM players WHERE user_id = :userId ORDER BY rating DESC, name ASC")
    fun observeAll(userId: Long): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE user_id = :userId AND id = :playerId LIMIT 1")
    fun observeById(userId: Long, playerId: Int): Flow<PlayerEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PlayerEntity): Long

    @Update
    suspend fun update(entity: PlayerEntity)

    @Delete
    suspend fun delete(entity: PlayerEntity)
}