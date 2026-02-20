package com.example.mvp.domain.repository

interface AuthRepository {
    suspend fun register(name: String, email: String, rawPassword: String): Result<Unit>
    suspend fun login(email: String, rawPassword: String): Result<Unit>
}