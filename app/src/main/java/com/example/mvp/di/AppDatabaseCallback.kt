package com.example.mvp.di

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mvp.data.local.SamplePayLoad
import com.example.mvp.data.local.dao.AuthUserDao
import com.example.mvp.data.local.dao.MatchDao
import com.example.mvp.data.local.dao.PlayerDao
import com.example.mvp.data.local.dao.TrainingDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class AppDatabaseCallback @Inject constructor(
    private val authUserDaoProvider: Provider<AuthUserDao>,
    private val playerDaoProvider: Provider<PlayerDao>,
    private val matchDaoProvider: Provider<MatchDao>,
    private val trainingDaoProvider: Provider<TrainingDao>
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        CoroutineScope(Dispatchers.IO).launch {
            val authUserDao = authUserDaoProvider.get()
            val playerDao = playerDaoProvider.get()
            val matchDao = matchDaoProvider.get()
            val trainingDao = trainingDaoProvider.get()

            SamplePayLoad.seed(
                authUserDao = authUserDao,
                playerDao = playerDao,
                matchDao = matchDao,
                trainingDao = trainingDao
            )
        }
    }
}

private fun String.sha256(): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(this.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}