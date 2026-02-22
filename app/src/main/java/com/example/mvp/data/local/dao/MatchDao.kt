package com.example.mvp.data.local.dao

import androidx.room.*
import com.example.mvp.data.local.entities.MatchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {

    @Query("SELECT * FROM matches WHERE user_id = :userId ORDER BY id DESC")
    fun observeByUser(userId: Long): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE user_id = :userId AND id = :matchId LIMIT 1")
    fun observeById(userId: Long, matchId: Int): Flow<MatchEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: MatchEntity): Long

    @Update
    suspend fun update(entity: MatchEntity)

    @Delete
    suspend fun delete(entity: MatchEntity)
}