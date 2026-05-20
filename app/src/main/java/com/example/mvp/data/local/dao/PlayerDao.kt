package com.example.mvp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mvp.data.local.entities.PlayerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {

    @Query("SELECT * FROM players WHERE user_id = :userId ORDER BY rating DESC, name ASC")
    fun observeAll(userId: Long): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE user_id = :userId AND id = :playerId LIMIT 1")
    fun observeById(userId: Long, playerId: Int): Flow<PlayerEntity?>

    @Query(
        """
        SELECT EXISTS(
            SELECT 1
            FROM players
            WHERE user_id = :userId
              AND number = :number
              AND id != :excludedPlayerId
        )
        """
    )
    suspend fun existsPlayerWithNumber(
        userId: Long,
        number: Int,
        excludedPlayerId: Int
    ): Boolean

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: PlayerEntity): Long

    @Query(
        """
        UPDATE players
        SET lineup_slot = NULL
        WHERE user_id = :userId
          AND lineup_slot = :lineupSlot
          AND id != :excludedPlayerId
        """
    )
    suspend fun clearLineupSlotForOtherPlayers(
        userId: Long,
        lineupSlot: String,
        excludedPlayerId: Int
    ): Int

    @Query(
        """
        UPDATE players
        SET name = :name,
            position = :position,
            age = :age,
            number = :number,
            rating = :rating,
            status = :status,
            level = :level,
            style = :style,
            lineup_slot = :lineupSlot
        WHERE id = :playerId AND user_id = :userId
        """
    )
    suspend fun updateForUser(
        playerId: Int,
        userId: Long,
        name: String,
        position: String,
        age: Int,
        number: Int,
        rating: Int,
        status: String,
        level: String,
        style: String,
        lineupSlot: String?
    ): Int

    @Query("DELETE FROM players WHERE id = :playerId AND user_id = :userId")
    suspend fun deleteByIdForUser(playerId: Int, userId: Long): Int
}
