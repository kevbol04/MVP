package com.example.mvp.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "matches",
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
data class MatchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "user_id")
    val userId: Long,

    val rival: String,

    @ColumnInfo(name = "date_text")
    val dateText: String,

    val competition: String,
    val result: String,

    @ColumnInfo(name = "goals_for")
    val goalsFor: Int,

    @ColumnInfo(name = "goals_against")
    val goalsAgainst: Int
)