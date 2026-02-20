package com.example.mvp.data.repository

import com.example.mvp.data.local.dao.AuthUserDao
import com.example.mvp.data.local.entities.AuthUserEntity
import com.example.mvp.domain.repository.AuthRepository
import java.security.MessageDigest
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val dao: AuthUserDao
) : AuthRepository {

    override suspend fun register(
        name: String,
        email: String,
        rawPassword: String
    ): Result<Unit> = runCatching {
        val normalizedEmail = email.trim().lowercase()
        val existing = dao.findByEmail(normalizedEmail)
        if (existing != null) error("Ese correo ya está registrado.")

        val entity = AuthUserEntity(
            name = name.trim(),
            email = normalizedEmail,
            passwordHash = rawPassword.sha256()
        )

        dao.insert(entity)
    }

    override suspend fun login(email: String, rawPassword: String): Result<Unit> = runCatching {
        val normalizedEmail = email.trim().lowercase()
        val user = dao.findByEmail(normalizedEmail) ?: error("No existe una cuenta con ese correo.")

        val providedHash = rawPassword.sha256()
        if (user.passwordHash != providedHash) error("Contraseña incorrecta.")
    }
}

private fun String.sha256(): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(this.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}