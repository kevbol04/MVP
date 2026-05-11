package com.example.mvp.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE auth_users ADD COLUMN passwordSalt TEXT")
        }
    }

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE trainings ADD COLUMN is_done INTEGER NOT NULL DEFAULT 0")
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        callback: AppDatabaseCallback
    ): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
            .addCallback(callback)
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
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