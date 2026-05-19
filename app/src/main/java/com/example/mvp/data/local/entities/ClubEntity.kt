package com.example.mvp.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "clubs",
    foreignKeys = [
        ForeignKey(
            entity = AuthUserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["user_id"], unique = true)
    ]
)
data class ClubEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "user_id")
    val userId: Long,

    val name: String,
    val stadium: String,
    val city: String,

    @ColumnInfo(name = "coach_name")
    val coachName: String,

    @ColumnInfo(name = "badge_id", defaultValue = "royal_blue")
    val badgeId: String = "royal_blue",

    @ColumnInfo(name = "selected_formation_id", defaultValue = "442")
    val selectedFormationId: String = "442"
)