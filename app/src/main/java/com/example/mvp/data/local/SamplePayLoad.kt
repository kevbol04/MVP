package com.example.mvp.data.local

import com.example.mvp.data.local.dao.AuthUserDao
import com.example.mvp.data.local.dao.ClubDao
import com.example.mvp.data.local.dao.MatchDao
import com.example.mvp.data.local.dao.PlayerDao
import com.example.mvp.data.local.dao.TrainingDao
import com.example.mvp.data.local.entities.AuthUserEntity
import com.example.mvp.data.local.entities.ClubEntity
import com.example.mvp.data.local.entities.MatchEntity
import com.example.mvp.data.local.entities.PlayerEntity
import com.example.mvp.data.local.entities.TrainingEntity
import com.example.mvp.data.security.AuthPasswordHasher
import com.example.mvp.domain.model.Competition
import com.example.mvp.domain.model.MatchResult
import com.example.mvp.domain.model.PlayerPosition
import com.example.mvp.domain.model.PlayerStatus
import com.example.mvp.domain.model.TrainingType

object SamplePayLoad {

    private const val DEMO_NAME = "User"
    private const val DEMO_EMAIL = "user@gmail.com"
    private const val DEMO_PASSWORD = "1234"

    suspend fun seed(
        authUserDao: AuthUserDao,
        playerDao: PlayerDao,
        matchDao: MatchDao,
        trainingDao: TrainingDao,
        clubDao: ClubDao
    ) {
        if (authUserDao.findByEmail(DEMO_EMAIL) != null) return

        val credentials = AuthPasswordHasher.hashPassword(DEMO_PASSWORD)

        val userId = authUserDao.insert(
            AuthUserEntity(
                name = DEMO_NAME,
                email = DEMO_EMAIL,
                passwordHash = credentials.hash,
                passwordSalt = credentials.salt
            )
        )

        clubDao.insert(buildClub(userId))
        buildPlayers(userId).forEach { playerDao.insert(it) }
        buildMatches(userId).forEach { matchDao.insert(it) }
        buildTrainings(userId).forEach { trainingDao.insert(it) }
    }

    private fun buildClub(userId: Long): ClubEntity = ClubEntity(
        userId = userId,
        name = "Real Madrid CF",
        stadium = "Santiago Bernabéu",
        city = "Madrid",
        coachName = DEMO_NAME,
        badgeId = "royal_blue"
    )

    private fun buildPlayers(userId: Long): List<PlayerEntity> = listOf(
        // Porteros
        player(userId, "Thibaut Courtois", PlayerPosition.POR, 31, 1, 89),
        player(userId, "Andriy Lunin", PlayerPosition.POR, 25, 13, 78),
        player(userId, "Fran González", PlayerPosition.POR, 19, 26, 69),

        // Defensas
        player(userId, "Dani Carvajal", PlayerPosition.DEF, 34, 2, 84),
        player(userId, "Éder Militão", PlayerPosition.DEF, 28, 3, 86, PlayerStatus.LESIONADO),
        player(userId, "David Alaba", PlayerPosition.DEF, 33, 4, 85, PlayerStatus.LESIONADO),
        player(userId, "Trent Alexander-Arnold", PlayerPosition.DEF, 27, 12, 87),
        player(userId, "Raúl Asencio", PlayerPosition.DEF, 23, 17, 81),
        player(userId, "Álvaro Carreras", PlayerPosition.DEF, 23, 18, 80),
        player(userId, "Fran García", PlayerPosition.DEF, 26, 20, 79),
        player(userId, "Antonio Rüdiger", PlayerPosition.DEF, 33, 22, 86),
        player(userId, "Ferland Mendy", PlayerPosition.DEF, 30, 23, 83, PlayerStatus.LESIONADO),
        player(userId, "Dean Huijsen", PlayerPosition.DEF, 21, 24, 84),
        player(userId, "David Jiménez", PlayerPosition.DEF, 22, 35, 72),
        player(userId, "Jacobo Ramón", PlayerPosition.DEF, 21, 31, 73),

        // Mediocentros
        player(userId, "Jude Bellingham", PlayerPosition.MED, 22, 5, 88),
        player(userId, "Eduardo Camavinga", PlayerPosition.MED, 23, 6, 84),
        player(userId, "Federico Valverde", PlayerPosition.MED, 27, 8, 86),
        player(userId, "Aurélien Tchouaméni", PlayerPosition.MED, 26, 14, 84),
        player(userId, "Arda Güler", PlayerPosition.MED, 21, 15, 82),
        player(userId, "Dani Ceballos", PlayerPosition.MED, 29, 19, 80, PlayerStatus.LESIONADO),
        player(userId, "Brahim Díaz", PlayerPosition.MED, 26, 21, 82),
        player(userId, "Thiago Pitarch", PlayerPosition.MED, 19, 28, 70),
        player(userId, "Jaime Cestero", PlayerPosition.MED, 18, 27, 68, PlayerStatus.LESIONADO),

        // Delanteros
        player(userId, "Vinícius Jr.", PlayerPosition.DEL, 25, 7, 90),
        player(userId, "Kylian Mbappé", PlayerPosition.DEL, 27, 10, 91),
        player(userId, "Rodrygo Goes", PlayerPosition.DEL, 25, 11, 85),
        player(userId, "Gonzalo García", PlayerPosition.DEL, 22, 16, 76),
        player(userId, "Endrick", PlayerPosition.DEL, 19, 9, 79),
        player(userId, "Franco Mastantuono", PlayerPosition.DEL, 18, 30, 78, PlayerStatus.LESIONADO)
    )

    private fun player(
        userId: Long,
        name: String,
        position: PlayerPosition,
        age: Int,
        number: Int,
        rating: Int,
        status: PlayerStatus = PlayerStatus.DISPONIBLE
    ): PlayerEntity = PlayerEntity(
        userId = userId,
        name = name,
        position = position.name,
        age = age,
        number = number,
        rating = rating,
        status = status.name,
        lineupSlot = null
    )

    private fun buildMatches(userId: Long): List<MatchEntity> = listOf(
        match(userId, "Atlético de Madrid", "05/04/2026", Competition.LIGA, MatchResult.VICTORIA, 3, 1),
        match(userId, "FC Barcelona", "29/03/2026", Competition.COPA, MatchResult.EMPATE, 2, 2),
        match(userId, "Valencia CF", "22/03/2026", Competition.LIGA, MatchResult.VICTORIA, 2, 0),
        match(userId, "Sevilla FC", "15/03/2026", Competition.LIGA, MatchResult.DERROTA, 1, 2),
        match(userId, "Real Sociedad", "08/03/2026", Competition.COPA, MatchResult.VICTORIA, 4, 2),
        match(userId, "Villarreal CF", "01/03/2026", Competition.AMISTOSO, MatchResult.EMPATE, 1, 1),
        match(userId, "Real Betis", "22/02/2026", Competition.LIGA, MatchResult.VICTORIA, 3, 0),
        match(userId, "Athletic Club", "15/02/2026", Competition.LIGA, MatchResult.DERROTA, 0, 1),
        match(userId, "Getafe CF", "08/02/2026", Competition.AMISTOSO, MatchResult.VICTORIA, 2, 1),
        match(userId, "Girona FC", "01/02/2026", Competition.LIGA, MatchResult.VICTORIA, 5, 2)
    )

    private fun match(
        userId: Long,
        rival: String,
        dateText: String,
        competition: Competition,
        result: MatchResult,
        goalsFor: Int,
        goalsAgainst: Int
    ): MatchEntity = MatchEntity(
        userId = userId,
        rival = rival,
        dateText = dateText,
        competition = competition.name,
        result = result.name,
        goalsFor = goalsFor,
        goalsAgainst = goalsAgainst
    )

    private fun buildTrainings(userId: Long): List<TrainingEntity> = listOf(
        training(userId, "Recuperación post partido", "13/04/2026", 40, TrainingType.RECUPERACION, true),
        training(userId, "Resistencia base", "16/04/2026", 75, TrainingType.RESISTENCIA, true),
        training(userId, "Fuerza tren inferior", "20/04/2026", 70, TrainingType.FUERZA, false),
        training(userId, "Trabajo táctico defensivo", "24/04/2026", 90, TrainingType.TECNICA, true),
        training(userId, "Velocidad y reacción", "28/04/2026", 60, TrainingType.VELOCIDAD, false),
        training(userId, "Salida de balón", "02/05/2026", 85, TrainingType.TECNICA, true),
        training(userId, "Presión tras pérdida", "06/05/2026", 90, TrainingType.RESISTENCIA, false),
        training(userId, "Finalización y centros", "10/05/2026", 80, TrainingType.TECNICA, true),
        training(userId, "Bloque medio", "12/05/2026", 75, TrainingType.TECNICA, false),
        training(userId, "Activación pre partido", "13/05/2026", 45, TrainingType.RECUPERACION, false),
        training(userId, "Recuperación activa", "15/05/2026", 45, TrainingType.RECUPERACION, false),
        training(userId, "Fuerza preventiva", "18/05/2026", 65, TrainingType.FUERZA, false),
        training(userId, "Transiciones ofensivas", "22/05/2026", 90, TrainingType.TECNICA, false),
        training(userId, "Resistencia con balón", "26/05/2026", 80, TrainingType.RESISTENCIA, false),
        training(userId, "Técnica individual", "30/05/2026", 70, TrainingType.TECNICA, false),
        training(userId, "Cambios de ritmo", "03/06/2026", 60, TrainingType.VELOCIDAD, false),
        training(userId, "Partido reducido", "07/06/2026", 90, TrainingType.RESISTENCIA, false),
        training(userId, "Estrategia balón parado", "11/06/2026", 75, TrainingType.TECNICA, false),
        training(userId, "Sesión regenerativa", "12/06/2026", 40, TrainingType.RECUPERACION, false),
        training(userId, "Potencia y cambios de ritmo", "14/06/2026", 70, TrainingType.VELOCIDAD, false)
    )

    private fun training(
        userId: Long,
        name: String,
        dateText: String,
        durationMin: Int,
        type: TrainingType,
        isDone: Boolean
    ): TrainingEntity = TrainingEntity(
        userId = userId,
        name = name,
        dateText = dateText,
        durationMin = durationMin,
        type = type.name,
        isDone = isDone
    )
}