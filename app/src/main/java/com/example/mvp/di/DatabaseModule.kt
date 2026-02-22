package com.example.mvp.di

import android.content.Context
import androidx.room.Room
import com.example.mvp.data.local.dao.AuthUserDao
import com.example.mvp.data.local.dao.MatchDao
import com.example.mvp.data.local.dao.PlayerDao
import com.example.mvp.data.local.dao.TrainingDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DB_NAME = "mvp_database"

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        callback: AppDatabaseCallback
    ): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
            .addCallback(callback)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideAuthUserDao(db: AppDatabase): AuthUserDao = db.authUserDao()

    @Provides
    fun provideTrainingDao(db: AppDatabase): TrainingDao = db.trainingDao()

    @Provides
    fun provideMatchDao(db: AppDatabase): MatchDao = db.matchDao()

    @Provides
    fun providePlayerDao(db: AppDatabase): PlayerDao = db.playerDao()
}