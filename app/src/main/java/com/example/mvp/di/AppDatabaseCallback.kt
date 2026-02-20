package com.example.mvp.di

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mvp.data.local.dao.AuthUserDao
import com.example.mvp.data.local.entities.AuthUserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class AppDatabaseCallback @Inject constructor(
    private val authUserDaoProvider: Provider<AuthUserDao>
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        CoroutineScope(Dispatchers.IO).launch {
            val dao = authUserDaoProvider.get()

            val demo = AuthUserEntity(
                name = "User",
                email = "user@gmail.com",
                passwordHash = "1234".sha256()
            )

            runCatching { dao.insert(demo) }
        }
    }
}

private fun String.sha256(): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(this.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}