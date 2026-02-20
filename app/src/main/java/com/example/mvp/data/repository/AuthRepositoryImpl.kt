package com.example.mvp.data.repository

import com.example.mvp.data.local.dao.AuthUserDao
import com.example.mvp.data.local.entities.AuthUserEntity
import com.example.mvp.domain.model.AuthUser
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
    ): Result<AuthUser> = runCatching {
        val normalizedEmail = email.trim().lowercase()
        val existing = dao.findByEmail(normalizedEmail)
        if (existing != null) error("Ese correo ya está registrado.")

        val entity = AuthUserEntity(
            name = name.trim(),
            email = normalizedEmail,
            passwordHash = rawPassword.sha256()
        )

        val newId = dao.insert(entity)

        AuthUser(
            id = newId,
            name = entity.name,
            email = entity.email,
            passwordHash = entity.passwordHash,
            createdAt = entity.createdAt
        )
    }

    override suspend fun login(
        email: String,
        rawPassword: String
    ): Result<AuthUser> = runCatching {
        val normalizedEmail = email.trim().lowercase()
        val user = dao.findByEmail(normalizedEmail)
            ?: error("No existe una cuenta con ese correo.")

        val providedHash = rawPassword.sha256()
        if (user.passwordHash != providedHash) error("Contraseña incorrecta.")

        AuthUser(
            id = user.id,
            name = user.name,
            email = user.email,
            passwordHash = user.passwordHash,
            createdAt = user.createdAt
        )
    }

    override suspend fun updateProfile(
        oldEmail: String,
        newName: String,
        newEmail: String
    ): Result<AuthUser> = runCatching {
        val oldNorm = oldEmail.trim().lowercase()
        val newNorm = newEmail.trim().lowercase()
        val nameNorm = newName.trim()

        val current = dao.findByEmail(oldNorm) ?: error("No se encontró la cuenta.")

        if (newNorm != oldNorm) {
            val exists = dao.findByEmail(newNorm)
            if (exists != null) error("Ese correo ya está registrado.")
        }

        val rows = dao.updateProfileByEmail(
            oldEmail = oldNorm,
            newName = nameNorm,
            newEmail = newNorm
        )
        if (rows <= 0) error("No se pudo actualizar el perfil.")

        AuthUser(
            id = current.id,
            name = nameNorm,
            email = newNorm,
            passwordHash = current.passwordHash,
            createdAt = current.createdAt
        )
    }
}

private fun String.sha256(): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(this.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}