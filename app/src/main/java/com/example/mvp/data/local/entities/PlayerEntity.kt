package com.example.mvp.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "players",
    foreignKeys = [
        ForeignKey(
            entity = AuthUserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["user_id", "number"], unique = true),
        Index(value = ["user_id", "lineup_slot"], unique = true)
    ]
)
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "user_id")
    val userId: Long,

    val name: String,
    val position: String,
    val age: Int,
    val number: Int,
    val status: String,

    @ColumnInfo(name = "level")
    val level: String = "BUENO",

    @ColumnInfo(name = "style")
    val style: String = "COMPLETO",

    @ColumnInfo(name = "lineup_slot")
    val lineupSlot: String? = null
)