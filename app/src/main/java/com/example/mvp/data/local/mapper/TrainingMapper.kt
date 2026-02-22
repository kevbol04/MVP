package com.example.mvp.data.local.mapper

import com.example.mvp.data.local.entities.TrainingEntity
import com.example.mvp.ui.screens.training.Training
import com.example.mvp.ui.screens.training.TrainingType

fun TrainingEntity.toModel(): Training {
    return Training(
        id = id,
        name = name,
        dateText = dateText,
        durationMin = durationMin,
        type = TrainingType.valueOf(type)
    )
}

fun Training.toEntity(userId: Long): TrainingEntity {
    return TrainingEntity(
        id = id,
        userId = userId,
        name = name,
        dateText = dateText,
        durationMin = durationMin,
        type = type.name
    )
}