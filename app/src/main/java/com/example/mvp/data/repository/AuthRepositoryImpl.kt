package com.example.mvp.data.repository

import com.example.mvp.data.local.dao.AuthUserDao
import com.example.mvp.data.local.entities.AuthUserEntity
import com.example.mvp.data.local.mapper.toDomain
import com.example.mvp.data.security.AuthPasswordHasher
import com.example.mvp.domain.model.AuthUser
import com.example.mvp.domain.repository.AuthRepository
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
        if (nameNorm.length < MIN_NAME_LEN) {
            error("El nombre debe tener al menos $MIN_NAME_LEN caracteres.")
        }

        val normalizedEmail = email.trim().lowercase()
        if (!EMAIL_REGEX.matches(normalizedEmail)) {
            error("Introduce un correo válido.")
        }

        if (rawPassword.length < MIN_PASSWORD_LEN) {
            error("La contraseña debe tener al menos $MIN_PASSWORD_LEN caracteres.")
        }

        val existing = dao.findByEmail(normalizedEmail)
        if (existing != null) {
            error("Ese correo ya está registrado.")
        }

        val credentials = AuthPasswordHasher.hashPassword(rawPassword)

        val entity = AuthUserEntity(
            name = nameNorm,
            email = normalizedEmail,
            passwordHash = credentials.hash,
            passwordSalt = credentials.salt
        )

        val newId = dao.insert(entity)

        entity.copy(id = newId).toDomain()
    }

    override suspend fun login(
        email: String,
        rawPassword: String
    ): Result<AuthUser> = runCatching {
        val normalizedEmail = email.trim().lowercase()

        val user = dao.findByEmail(normalizedEmail)
            ?: error("No existe una cuenta con ese correo.")

        val passwordIsValid = verifyPasswordAndUpgradeIfNeeded(
            user = user,
            rawPassword = rawPassword
        )

        if (!passwordIsValid) {
            error("Contraseña incorrecta.")
        }

        user.toDomain()
    }

    override suspend fun updateProfile(
        oldEmail: String,
        newName: String,
        newEmail: String
    ): Result<AuthUser> = runCatching {
        val oldNorm = oldEmail.trim().lowercase()
        val newNorm = newEmail.trim().lowercase()
        val nameNorm = newName.trim()

        if (nameNorm.length < MIN_NAME_LEN) {
            error("El nombre debe tener al menos $MIN_NAME_LEN caracteres.")
        }

        if (!EMAIL_REGEX.matches(newNorm)) {
            error("Introduce un correo válido.")
        }

        val current = dao.findByEmail(oldNorm)
            ?: error("No se encontró la cuenta.")

        if (newNorm != oldNorm) {
            val exists = dao.findByEmail(newNorm)
            if (exists != null) {
                error("Ese correo ya está registrado.")
            }
        }

        val rows = dao.updateProfileByEmail(
            oldEmail = oldNorm,
            newName = nameNorm,
            newEmail = newNorm
        )

        if (rows <= 0) {
            error("No se pudo actualizar el perfil.")
        }

        current.copy(
            name = nameNorm,
            email = newNorm
        ).toDomain()
    }

    override suspend fun changePassword(
        email: String,
        currentRaw: String,
        newRaw: String
    ): Result<Unit> = runCatching {
        val norm = email.trim().lowercase()

        val user = dao.findByEmail(norm)
            ?: error("No se encontró la cuenta.")

        val currentPasswordIsValid = verifyPasswordAndUpgradeIfNeeded(
            user = user,
            rawPassword = currentRaw
        )

        if (!currentPasswordIsValid) {
            error("La contraseña actual no es correcta.")
        }

        if (newRaw.length < MIN_PASSWORD_LEN) {
            error("La nueva contraseña debe tener al menos $MIN_PASSWORD_LEN caracteres.")
        }

        val newCredentials = AuthPasswordHasher.hashPassword(newRaw)

        val rows = dao.updatePasswordCredentials(
            email = norm,
            newHash = newCredentials.hash,
            newSalt = newCredentials.salt
        )

        if (rows <= 0) {
            error("No se pudo actualizar la contraseña.")
        }
    }

    override suspend fun deleteAccount(email: String): Result<Unit> = runCatching {
        val norm = email.trim().lowercase()

        val existing = dao.findByEmail(norm)
            ?: error("No se encontró la cuenta.")

        val rows = dao.deleteByEmail(existing.email)

        if (rows <= 0) {
            error("No se pudo eliminar la cuenta.")
        }
    }

    private suspend fun verifyPasswordAndUpgradeIfNeeded(
        user: AuthUserEntity,
        rawPassword: String
    ): Boolean {
        val salt = user.passwordSalt

        if (!salt.isNullOrBlank()) {
            return AuthPasswordHasher.verifyPassword(
                rawPassword = rawPassword,
                storedHash = user.passwordHash,
                storedSalt = salt
            )
        }

        val legacyPasswordIsValid = AuthPasswordHasher.verifyLegacySha256(
            rawPassword = rawPassword,
            storedHash = user.passwordHash
        )

        if (!legacyPasswordIsValid) {
            return false
        }

        val newCredentials = AuthPasswordHasher.hashPassword(rawPassword)

        dao.updatePasswordCredentials(
            email = user.email,
            newHash = newCredentials.hash,
            newSalt = newCredentials.salt
        )

        return true
    }
}