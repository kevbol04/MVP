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
        competition = Competition.valueOf(competition),
        goalsFor = goalsFor,
        goalsAgainst = goalsAgainst,
        result = MatchResult.valueOf(result)
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