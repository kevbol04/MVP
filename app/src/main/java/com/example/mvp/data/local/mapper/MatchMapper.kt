package com.example.mvp.data.local.mapper

import com.example.mvp.data.local.entities.MatchEntity
import com.example.mvp.ui.screens.matches.Competition
import com.example.mvp.ui.screens.matches.Match
import com.example.mvp.ui.screens.matches.MatchResult

fun MatchEntity.toModel(): Match {
    return Match(
        id = id,
        rival = rival,
        dateText = dateText,
        competition = competition.toCompetition(),
        goalsFor = goalsFor,
        goalsAgainst = goalsAgainst,
        result = result.toMatchResult()
    )
}

fun Match.toEntity(userId: Long): MatchEntity {
    return MatchEntity(
        id = id,
        userId = userId,
        rival = rival,
        dateText = dateText,
        competition = competition.name,
        goalsFor = goalsFor,
        goalsAgainst = goalsAgainst,
        result = result.name
    )
}

private fun String.toCompetition(): Competition {
    return Competition.entries.firstOrNull { competition ->
        competition.name == this || competition.label == this
    } ?: Competition.entries.first()
}

private fun String.toMatchResult(): MatchResult {
    return MatchResult.entries.firstOrNull { result ->
        result.name == this || result.label == this
    } ?: MatchResult.entries.first()
}