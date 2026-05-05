package com.example.mvp.data.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.userSessionDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_session"
)

@Singleton
class UserSessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    val session: Flow<UserSession?> = context.userSessionDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val userId = preferences[USER_ID] ?: 0L
            val name = preferences[USER_NAME].orEmpty()
            val email = preferences[USER_EMAIL].orEmpty()

            if (userId > 0L && email.isNotBlank()) {
                UserSession(
                    userId = userId,
                    name = name.ifBlank { "Usuario" },
                    email = email
                )
            } else {
                null
            }
        }

    suspend fun saveSession(userId: Long, name: String, email: String) {
        context.userSessionDataStore.edit { preferences ->
            preferences[USER_ID] = userId
            preferences[USER_NAME] = name
            preferences[USER_EMAIL] = email
        }
    }

    suspend fun clearSession() {
        context.userSessionDataStore.edit { preferences ->
            preferences.clear()
        }
    }

    private companion object {
        val USER_ID = longPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
    }
}