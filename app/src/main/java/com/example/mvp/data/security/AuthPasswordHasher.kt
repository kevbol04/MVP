package com.example.mvp.data.security

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

data class PasswordCredentials(
    val hash: String,
    val salt: String
)

object AuthPasswordHasher {

    private const val ITERATIONS = 120_000
    private const val KEY_LENGTH_BITS = 256
    private const val SALT_LENGTH_BYTES = 16

    private val secureRandom = SecureRandom()

    fun hashPassword(rawPassword: String): PasswordCredentials {
        val saltBytes = ByteArray(SALT_LENGTH_BYTES)
        secureRandom.nextBytes(saltBytes)

        val hashBytes = pbkdf2(
            password = rawPassword.toCharArray(),
            salt = saltBytes
        )

        return PasswordCredentials(
            hash = hashBytes.toBase64(),
            salt = saltBytes.toBase64()
        )
    }

    fun verifyPassword(
        rawPassword: String,
        storedHash: String,
        storedSalt: String
    ): Boolean {
        val saltBytes = storedSalt.fromBase64()

        val calculatedHash = pbkdf2(
            password = rawPassword.toCharArray(),
            salt = saltBytes
        )

        val storedHashBytes = storedHash.fromBase64()

        return MessageDigest.isEqual(calculatedHash, storedHashBytes)
    }

    fun verifyLegacySha256(
        rawPassword: String,
        storedHash: String
    ): Boolean {
        return rawPassword.sha256() == storedHash
    }

    private fun pbkdf2(
        password: CharArray,
        salt: ByteArray
    ): ByteArray {
        val spec = PBEKeySpec(
            password,
            salt,
            ITERATIONS,
            KEY_LENGTH_BITS
        )

        return SecretKeyFactory
            .getInstance("PBKDF2WithHmacSHA256")
            .generateSecret(spec)
            .encoded
    }

    private fun ByteArray.toBase64(): String {
        return Base64.encodeToString(this, Base64.NO_WRAP)
    }

    private fun String.fromBase64(): ByteArray {
        return Base64.decode(this, Base64.NO_WRAP)
    }

    private fun String.sha256(): String {
        val bytes = MessageDigest
            .getInstance("SHA-256")
            .digest(this.toByteArray())

        return bytes.joinToString("") { "%02x".format(it) }
    }
}