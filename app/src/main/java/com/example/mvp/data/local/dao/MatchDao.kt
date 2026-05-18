package com.example.mvp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mvp.data.local.entities.MatchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {

    @Query("SELECT * FROM matches WHERE user_id = :userId ORDER BY date_epoch_day DESC, id DESC")
    fun observeByUser(userId: Long): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE user_id = :userId AND id = :matchId LIMIT 1")
    fun observeById(userId: Long, matchId: Int): Flow<MatchEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: MatchEntity): Long

    @Query(
        """
        UPDATE matches
        SET rival = :rival,
            date_epoch_day = :dateEpochDay,
            competition = :competition,
            goals_for = :goalsFor,
            goals_against = :goalsAgainst
        WHERE id = :matchId AND user_id = :userId
        """
    )
    suspend fun updateForUser(
        matchId: Int,
        userId: Long,
        rival: String,
        dateEpochDay: Long,
        competition: String,
        goalsFor: Int,
        goalsAgainst: Int
    ): Int

    @Query("DELETE FROM matches WHERE id = :matchId AND user_id = :userId")
    suspend fun deleteByIdForUser(matchId: Int, userId: Long): Int
}