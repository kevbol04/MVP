package com.example.mvp.di

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mvp.data.local.dao.AuthUserDao
import com.example.mvp.data.local.dao.MatchDao
import com.example.mvp.data.local.dao.PlayerDao
import com.example.mvp.data.local.dao.TrainingDao
import com.example.mvp.data.local.entities.AuthUserEntity
import com.example.mvp.data.local.entities.MatchEntity
import com.example.mvp.data.local.entities.PlayerEntity
import com.example.mvp.data.local.entities.TrainingEntity

@Database(
    entities = [
        AuthUserEntity::class,
        TrainingEntity::class,
        MatchEntity::class,
        PlayerEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun authUserDao(): AuthUserDao
    abstract fun trainingDao(): TrainingDao
    abstract fun matchDao(): MatchDao
    abstract fun playerDao(): PlayerDao
}