package com.example.mvp.ui.screens.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvp.data.session.UserSession
import com.example.mvp.data.session.UserSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SessionUiState(
    val loading: Boolean = true,
    val userSession: UserSession? = null
)

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val sessionManager: UserSessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            sessionManager.session.collect { session ->
                _uiState.value = SessionUiState(
                    loading = false,
                    userSession = session
                )
            }
        }
    }

    fun saveSession(userId: Long, name: String, email: String) {
        viewModelScope.launch {
            sessionManager.saveSession(userId, name, email)
        }
    }

    fun clearSession() {
        viewModelScope.launch {
            sessionManager.clearSession()
        }
    }
}