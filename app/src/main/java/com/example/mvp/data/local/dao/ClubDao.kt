package com.example.mvp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mvp.data.local.entities.ClubEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClubDao {

    @Query("SELECT * FROM clubs WHERE user_id = :userId LIMIT 1")
    fun observeClub(userId: Long): Flow<ClubEntity?>

    @Query("SELECT * FROM clubs WHERE user_id = :userId LIMIT 1")
    suspend fun getClub(userId: Long): ClubEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ClubEntity): Long

    @Update
    suspend fun update(entity: ClubEntity)
}