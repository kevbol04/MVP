package com.example.mvp.domain.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle
import java.util.Locale

private val TRAINING_DISPLAY_DATE_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd/MM/uuuu", Locale.getDefault())
        .withResolverStyle(ResolverStyle.STRICT)

data class Training(
    val id: Int = 0,
    val name: String,
    val dateEpochDay: Long,
    val durationMin: Int,
    val type: TrainingType,
    val isDone: Boolean = false
) {
    val date: LocalDate
        get() = LocalDate.ofEpochDay(dateEpochDay)

    val dateText: String
        get() = date.format(TRAINING_DISPLAY_DATE_FORMATTER)

    companion object {
        fun epochDayFromDateText(raw: String): Long? {
            return runCatching {
                LocalDate.parse(raw.trim(), TRAINING_DISPLAY_DATE_FORMATTER).toEpochDay()
            }.getOrNull()
        }
    }
}