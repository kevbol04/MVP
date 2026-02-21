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

    @Query("SELECT * FROM auth_users WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): AuthUserEntity?

    @Query("UPDATE auth_users SET name = :newName, email = :newEmail WHERE email = :oldEmail")
    suspend fun updateProfileByEmail(oldEmail: String, newName: String, newEmail: String): Int

    @Query("UPDATE auth_users SET passwordHash = :newHash WHERE email = :email")
    suspend fun updatePasswordHash(email: String, newHash: String): Int
}