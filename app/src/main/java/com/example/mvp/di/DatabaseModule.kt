package com.example.mvp.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mvp.data.local.dao.AuthUserDao
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
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS trainings (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    user_id INTEGER NOT NULL,
                    name TEXT NOT NULL,
                    date_text TEXT NOT NULL,
                    duration_min INTEGER NOT NULL,
                    type TEXT NOT NULL,
                    FOREIGN KEY(user_id) REFERENCES auth_users(id) ON DELETE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS index_trainings_user_id ON trainings(user_id)")
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
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    fun provideAuthUserDao(db: AppDatabase): AuthUserDao = db.authUserDao()

    @Provides
    fun provideTrainingDao(db: AppDatabase): TrainingDao = db.trainingDao()
}