package com.example.mvp.data.local.mapper

import com.example.mvp.data.local.entities.MatchEntity
import com.example.mvp.domain.model.Competition
import com.example.mvp.domain.model.Match

fun MatchEntity.toModel(): Match {
    return Match(
        id = id,
        rival = rival,
        dateEpochDay = dateEpochDay,
        competition = competition.toCompetition(),
        goalsFor = goalsFor,
        goalsAgainst = goalsAgainst,
        isFinished = isFinished
    )
}

fun Match.toEntity(userId: Long): MatchEntity {
    return MatchEntity(
        id = id,
        userId = userId,
        rival = rival,
        dateEpochDay = dateEpochDay,
        competition = competition.name,
        goalsFor = if (isFinished) goalsFor else 0,
        goalsAgainst = if (isFinished) goalsAgainst else 0,
        isFinished = isFinished
    )
}

private fun String.toCompetition(): Competition {
    return Competition.entries.firstOrNull { competition ->
        competition.name == this || competition.label == this
    } ?: Competition.entries.first()
}