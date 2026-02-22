package com.example.mvp.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "trainings",
    foreignKeys = [
        ForeignKey(
            entity = AuthUserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["user_id"])]
)
data class TrainingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "user_id")
    val userId: Long,

    val name: String,

    @ColumnInfo(name = "date_text")
    val dateText: String,

    @ColumnInfo(name = "duration_min")
    val durationMin: Int,

    val type: String
)