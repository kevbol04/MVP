package com.example.mvp.domain.model

data class Match(
    val id: Int = 0,
    val rival: String,
    val dateText: String,
    val competition: Competition,
    val goalsFor: Int,
    val goalsAgainst: Int,
    val result: MatchResult
)