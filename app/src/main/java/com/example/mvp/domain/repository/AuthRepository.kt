package com.example.mvp.domain.repository

import com.example.mvp.domain.model.AuthUser

interface AuthRepository {
    suspend fun register(name: String, email: String, rawPassword: String): Result<AuthUser>
    suspend fun login(email: String, rawPassword: String): Result<AuthUser>

    suspend fun updateProfile(oldEmail: String, newName: String, newEmail: String): Result<AuthUser>
    suspend fun changePassword(email: String, currentRaw: String, newRaw: String): Result<Unit>
    suspend fun deleteAccount(email: String): Result<Unit>
}