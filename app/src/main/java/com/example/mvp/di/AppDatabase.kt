package com.example.mvp.di

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mvp.data.local.dao.AuthUserDao
import com.example.mvp.data.local.entities.AuthUserEntity

@Database(
    entities = [
        AuthUserEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun authUserDao(): AuthUserDao
}