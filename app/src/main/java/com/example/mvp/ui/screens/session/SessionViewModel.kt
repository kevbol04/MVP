package com.example.mvp.ui.screens.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvp.data.local.InitialDataSeeder
import com.example.mvp.data.session.UserSession
import com.example.mvp.data.session.UserSessionManager
import com.example.mvp.domain.repository.AuthRepository
import com.example.mvp.domain.repository.ClubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SessionUiState(
    val loading: Boolean = true,
    val userSession: UserSession? = null,
    val hasCompletedClubSetup: Boolean = false
)

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val sessionManager: UserSessionManager,
    private val authRepository: AuthRepository,
    private val clubRepository: ClubRepository,

    // SOLO DESARROLLO:
    // Este seeder crea user@gmail.com / 1234 y datos de prueba antes de mostrar el login.
    // Antes de publicar la app, eliminar este parámetro y la llamada seedDataIfNeeded() del init.
    private val initialDataSeeder: InitialDataSeeder
) : ViewModel() {

    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // SOLO DESARROLLO:
            // Carga usuario demo y datos de prueba antes de mostrar Login/Dashboard.
            //
            // ANTES DE PUBLICAR:
            // Eliminar este bloque y eliminar InitialDataSeeder.kt / SamplePayLoad.kt.
            runCatching {
                initialDataSeeder.seedDataIfNeeded()
            }

            sessionManager.session.collect { session ->
                validateSavedSession(session)
            }
        }
    }

    private suspend fun validateSavedSession(session: UserSession?) {
        if (session == null) {
            _uiState.value = SessionUiState(
                loading = false,
                userSession = null,
                hasCompletedClubSetup = false
            )
            return
        }

        val databaseUser = runCatching {
            authRepository.findById(session.userId)
        }.getOrNull()

        val savedEmail = session.email.trim().lowercase()
        val databaseEmail = databaseUser?.email?.trim()?.lowercase()

        val sessionIsInvalid = databaseUser == null || databaseEmail != savedEmail

        if (sessionIsInvalid) {
            sessionManager.clearSession()

            _uiState.value = SessionUiState(
                loading = false,
                userSession = null,
                hasCompletedClubSetup = false
            )
            return
        }

        val verifiedSession = UserSession(
            userId = databaseUser.id,
            name = databaseUser.name,
            email = databaseUser.email
        )

        if (verifiedSession != session) {
            sessionManager.saveSession(
                userId = verifiedSession.userId,
                name = verifiedSession.name,
                email = verifiedSession.email
            )
        }

        val hasClub = runCatching {
            clubRepository.hasClub(verifiedSession.userId)
        }.getOrDefault(false)

        _uiState.value = SessionUiState(
            loading = false,
            userSession = verifiedSession,
            hasCompletedClubSetup = hasClub
        )
    }

    fun saveSession(userId: Long, name: String, email: String) {
        viewModelScope.launch {
            sessionManager.saveSession(userId, name, email)
        }
    }

    fun markClubSetupCompleted() {
        _uiState.value = _uiState.value.copy(
            hasCompletedClubSetup = true
        )
    }

    fun clearSession() {
        viewModelScope.launch {
            sessionManager.clearSession()
        }
    }
}