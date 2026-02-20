package com.example.mvp.data.local.mapper

import com.example.mvp.data.local.entities.AuthUserEntity
import com.example.mvp.domain.model.AuthUser

fun AuthUserEntity.toDomain(): AuthUser = AuthUser(
    id = id,
    name = name,
    email = email,
    passwordHash = passwordHash,
    createdAt = createdAt
)

fun AuthUser.toEntity(): AuthUserEntity = AuthUserEntity(
    id = id,
    name = name,
    email = email,
    passwordHash = passwordHash,
    createdAt = createdAt
)