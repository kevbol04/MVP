package com.example.mvp.domain.model

data class AuthUser(
    val id: Long = 0L,
    val name: String,
    val email: String,
    val createdAt: Long = System.currentTimeMillis()
)