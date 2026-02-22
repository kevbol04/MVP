package com.example.mvp.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    @ColumnInfo(name = "user_id", index = true)
    val userId: Long,

    val name: String,
    val position: String,
    val age: Int,
    val number: Int,
    val rating: Int,
    val status: String
)