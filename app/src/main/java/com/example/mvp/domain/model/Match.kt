package com.example.mvp.domain.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle
import java.util.Locale

private val MATCH_DISPLAY_DATE_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd/MM/uuuu", Locale.getDefault())
        .withResolverStyle(ResolverStyle.STRICT)

data class Match(
    val id: Int = 0,
    val rival: String,
    val dateEpochDay: Long,
    val competition: Competition,
    val goalsFor: Int = 0,
    val goalsAgainst: Int = 0,
    val isFinished: Boolean = true
) {
    val date: LocalDate
        get() = LocalDate.ofEpochDay(dateEpochDay)

    val dateText: String
        get() = date.format(MATCH_DISPLAY_DATE_FORMATTER)

    val result: MatchResult
        get() = when {
            goalsFor > goalsAgainst -> MatchResult.VICTORIA
            goalsFor == goalsAgainst -> MatchResult.EMPATE
            else -> MatchResult.DERROTA
        }

    val needsResult: Boolean
        get() = !isFinished && date.isBefore(LocalDate.now())

    val statusLabel: String
        get() = when {
            isFinished -> result.label
            needsResult -> "Pendiente de resultado"
            else -> "Programado"
        }

    val scoreText: String
        get() = if (isFinished) "$goalsFor - $goalsAgainst" else "Por jugar"

    val isFutureScheduled: Boolean
        get() = !isFinished && date.isAfter(LocalDate.now())

    companion object {
        fun epochDayFromDateText(raw: String): Long? {
            return runCatching {
                LocalDate.parse(raw.trim(), MATCH_DISPLAY_DATE_FORMATTER).toEpochDay()
            }.getOrNull()
        }
    }
}