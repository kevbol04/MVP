package com.example.mvp.data.repository

import com.example.mvp.data.local.dao.AuthUserDao
import com.example.mvp.data.local.entities.AuthUserEntity
import com.example.mvp.domain.model.AuthUser
import com.example.mvp.domain.repository.AuthRepository
import java.security.MessageDigest
import javax.inject.Inject

private const val MIN_PASSWORD_LEN = 4
private const val MIN_NAME_LEN = 3
private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

class AuthRepositoryImpl @Inject constructor(
    private val dao: AuthUserDao
) : AuthRepository {

    override suspend fun register(
        name: String,
        email: String,
        rawPassword: String
    ): Result<AuthUser> = runCatching {
        val nameNorm = name.trim()
        if (nameNorm.length < MIN_NAME_LEN) error("El nombre debe tener al menos $MIN_NAME_LEN caracteres.")

        val normalizedEmail = email.trim().lowercase()
        if (!EMAIL_REGEX.matches(normalizedEmail)) error("Introduce un correo válido.")

        if (rawPassword.length < MIN_PASSWORD_LEN) error("La contraseña debe tener al menos $MIN_PASSWORD_LEN caracteres.")

        val existing = dao.findByEmail(normalizedEmail)
        if (existing != null) error("Ese correo ya está registrado.")

        val entity = AuthUserEntity(
            name = nameNorm,
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

        if (nameNorm.length < MIN_NAME_LEN) error("El nombre debe tener al menos $MIN_NAME_LEN caracteres.")
        if (!EMAIL_REGEX.matches(newNorm)) error("Introduce un correo válido.")

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

    override suspend fun changePassword(
        email: String,
        currentRaw: String,
        newRaw: String
    ): Result<Unit> = runCatching {
        val norm = email.trim().lowercase()
        val user = dao.findByEmail(norm) ?: error("No se encontró la cuenta.")

        val currentHash = currentRaw.sha256()
        if (user.passwordHash != currentHash) error("La contraseña actual no es correcta.")

        if (newRaw.length < MIN_PASSWORD_LEN) error("La nueva contraseña debe tener al menos $MIN_PASSWORD_LEN caracteres.")

        val rows = dao.updatePasswordHash(email = norm, newHash = newRaw.sha256())
        if (rows <= 0) error("No se pudo actualizar la contraseña.")
    }
}

private fun String.sha256(): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(this.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}