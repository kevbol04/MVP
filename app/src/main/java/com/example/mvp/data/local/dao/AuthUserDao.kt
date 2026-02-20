package com.example.mvp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mvp.data.local.entities.AuthUserEntity

@Dao
interface AuthUserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: AuthUserEntity): Long

    @Query("SELECT * FROM auth_users WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): AuthUserEntity?
}