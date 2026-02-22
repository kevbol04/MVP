package com.example.mvp.data.local

import com.example.mvp.data.local.dao.AuthUserDao
import com.example.mvp.data.local.dao.MatchDao
import com.example.mvp.data.local.dao.PlayerDao
import com.example.mvp.data.local.dao.TrainingDao
import com.example.mvp.data.local.entities.AuthUserEntity
import com.example.mvp.data.local.entities.MatchEntity
import com.example.mvp.data.local.entities.PlayerEntity
import com.example.mvp.data.local.entities.TrainingEntity
import com.example.mvp.ui.screens.matches.Competition
import com.example.mvp.ui.screens.players.PlayerPosition
import com.example.mvp.ui.screens.players.PlayerStatus
import com.example.mvp.ui.screens.training.TrainingType
import java.security.MessageDigest

object SamplePayLoad {

    private const val DEMO_NAME = "User"
    private const val DEMO_EMAIL = "user@gmail.com"
    private const val DEMO_PASSWORD = "1234"

    suspend fun seed(
        authUserDao: AuthUserDao,
        playerDao: PlayerDao,
        matchDao: MatchDao,
        trainingDao: TrainingDao
    ) {
        if (authUserDao.findByEmail(DEMO_EMAIL) != null) return

        val userId = authUserDao.insert(
            AuthUserEntity(
                name = DEMO_NAME,
                email = DEMO_EMAIL,
                passwordHash = DEMO_PASSWORD.sha256()
            )
        )

        buildPlayers(userId).forEach { playerDao.insert(it) }

        buildMatches(userId).forEach { matchDao.insert(it) }

        buildTrainings(userId).forEach { trainingDao.insert(it) }
    }

    private fun buildPlayers(userId: Long): List<PlayerEntity> {
        var dorsal = 1

        return listOf(

            PlayerEntity(
                userId = userId,
                name = "Thibaut Courtois",
                position = PlayerPosition.POR.name,
                age = 31,
                number = dorsal++,
                rating = 89,
                status = PlayerStatus.TITULAR.name
            ),
            PlayerEntity(
                userId = userId,
                name = "Dani Carvajal",
                position = PlayerPosition.DEF.name,
                age = 32,
                number = dorsal++,
                rating = 84,
                status = PlayerStatus.TITULAR.name
            ),
            PlayerEntity(
                userId = userId,
                name = "Antonio Rüdiger",
                position = PlayerPosition.DEF.name,
                age = 31,
                number = dorsal++,
                rating = 86,
                status = PlayerStatus.TITULAR.name
            ),
            PlayerEntity(
                userId = userId,
                name = "David Alaba",
                position = PlayerPosition.DEF.name,
                age = 32,
                number = dorsal++,
                rating = 85,
                status = PlayerStatus.TITULAR.name
            ),
            PlayerEntity(
                userId = userId,
                name = "Ferland Mendy",
                position = PlayerPosition.DEF.name,
                age = 28,
                number = dorsal++,
                rating = 83,
                status = PlayerStatus.TITULAR.name
            ),
            PlayerEntity(
                userId = userId,
                name = "Luka Modrić",
                position = PlayerPosition.MED.name,
                age = 38,
                number = dorsal++,
                rating = 87,
                status = PlayerStatus.TITULAR.name
            ),
            PlayerEntity(
                userId = userId,
                name = "Jude Bellingham",
                position = PlayerPosition.MED.name,
                age = 21,
                number = dorsal++,
                rating = 88,
                status = PlayerStatus.TITULAR.name
            ),
            PlayerEntity(
                userId = userId,
                name = "Vinícius Jr.",
                position = PlayerPosition.DEL.name,
                age = 24,
                number = dorsal++,
                rating = 90,
                status = PlayerStatus.TITULAR.name
            ),

            PlayerEntity(
                userId = userId,
                name = "Andriy Lunin",
                position = PlayerPosition.POR.name,
                age = 25,
                number = dorsal++,
                rating = 78,
                status = PlayerStatus.SUPLENTE.name
            ),
            PlayerEntity(
                userId = userId,
                name = "Nacho Fernández",
                position = PlayerPosition.DEF.name,
                age = 34,
                number = dorsal++,
                rating = 80,
                status = PlayerStatus.SUPLENTE.name
            ),
            PlayerEntity(
                userId = userId,
                name = "Federico Valverde",
                position = PlayerPosition.MED.name,
                age = 26,
                number = dorsal++,
                rating = 86,
                status = PlayerStatus.SUPLENTE.name
            ),
            PlayerEntity(
                userId = userId,
                name = "Aurélien Tchouaméni",
                position = PlayerPosition.MED.name,
                age = 24,
                number = dorsal++,
                rating = 84,
                status = PlayerStatus.SUPLENTE.name
            ),
            PlayerEntity(
                userId = userId,
                name = "Rodrygo Goes",
                position = PlayerPosition.DEL.name,
                age = 23,
                number = dorsal++,
                rating = 85,
                status = PlayerStatus.SUPLENTE.name
            ),

            PlayerEntity(
                userId = userId,
                name = "Éder Militão",
                position = PlayerPosition.DEF.name,
                age = 26,
                number = dorsal++,
                rating = 86,
                status = PlayerStatus.LESIONADO.name
            ),
            PlayerEntity(
                userId = userId,
                name = "Brahim Díaz",
                position = PlayerPosition.DEL.name,
                age = 25,
                number = dorsal++,
                rating = 82,
                status = PlayerStatus.LESIONADO.name
            )
        )
    }

    private fun buildMatches(userId: Long): List<MatchEntity> =
        listOf(
            MatchEntity(
                userId = userId,
                rival = "Real Madrid",
                dateText = "22/02/2026",
                competition = com.example.mvp.ui.screens.matches.Competition.LIGA.name,
                result = com.example.mvp.ui.screens.matches.MatchResult.VICTORIA.name,
                goalsFor = 2,
                goalsAgainst = 1
            ),
            MatchEntity(
                userId = userId,
                rival = "FC Barcelona",
                dateText = "2026-02-10/02/2026",
                competition = com.example.mvp.ui.screens.matches.Competition.COPA.name,
                result = com.example.mvp.ui.screens.matches.MatchResult.EMPATE.name,
                goalsFor = 1,
                goalsAgainst = 1
            ),
            MatchEntity(
                userId = userId,
                rival = "Valencia CF",
                dateText = "01/02/2026",
                competition = com.example.mvp.ui.screens.matches.Competition.AMISTOSO.name,
                result = com.example.mvp.ui.screens.matches.MatchResult.DERROTA.name,
                goalsFor = 0,
                goalsAgainst = 2
            )
        )

    private fun buildTrainings(userId: Long): List<TrainingEntity> =
        listOf(
            TrainingEntity(
                userId = userId,
                name = "Entrenamiento físico",
                dateText = "23/03/2026",
                durationMin = 60,
                type = TrainingType.VELOCIDAD.name
            ),
            TrainingEntity(
                userId = userId,
                name = "Entrenamiento táctico",
                dateText = "24/03/2026",
                durationMin = 90,
                type = TrainingType.RESISTENCIA.name
            )
        )
}

private fun String.sha256(): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(this.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}