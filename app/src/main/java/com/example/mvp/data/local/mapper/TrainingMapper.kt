package com.example.mvp.data.local.mapper

import com.example.mvp.data.local.entities.TrainingEntity
import com.example.mvp.domain.model.Training
import com.example.mvp.domain.model.TrainingType

fun TrainingEntity.toModel(): Training {
    return Training(
        id = id,
        name = name,
        dateText = dateText,
        durationMin = durationMin,
        type = type.toTrainingType(),
        isDone = isDone
    )
}

fun Training.toEntity(userId: Long): TrainingEntity {
    return TrainingEntity(
        id = id,
        userId = userId,
        name = name,
        dateText = dateText,
        durationMin = durationMin,
        type = type.name,
        isDone = isDone
    )
}

private fun String.toTrainingType(): TrainingType {
    return TrainingType.entries.firstOrNull { trainingType ->
        trainingType.name == this || trainingType.label == this
    } ?: TrainingType.entries.first()
}