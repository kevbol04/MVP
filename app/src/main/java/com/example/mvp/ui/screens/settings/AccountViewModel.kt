package com.example.mvp.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvp.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AccountUiState(
    val loading: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null,
    val passwordChanged: Boolean = false,
    val accountDeleted: Boolean = false
)

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState: StateFlow<AccountUiState> = _uiState.asStateFlow()

    fun saveProfile(oldEmail: String, newName: String, newEmail: String) {
        viewModelScope.launch {
            _uiState.value = AccountUiState(loading = true)
            val result = repo.updateProfile(oldEmail, newName, newEmail)
            _uiState.value = AccountUiState(
                loading = false,
                saved = result.isSuccess,
                error = result.exceptionOrNull()?.message
            )
        }
    }

    fun changePassword(email: String, current: String, newPass: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null)

            val result = repo.changePassword(email = email, currentRaw = current, newRaw = newPass)

            _uiState.value = _uiState.value.copy(
                loading = false,
                passwordChanged = result.isSuccess,
                error = result.exceptionOrNull()?.message
            )
        }
    }

    fun deleteAccount(email: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null)
            val result = repo.deleteAccount(email)
            _uiState.value = _uiState.value.copy(
                loading = false,
                accountDeleted = result.isSuccess,
                error = result.exceptionOrNull()?.message
            )
        }
    }

    fun clearPasswordChangedFlag() {
        _uiState.value = _uiState.value.copy(passwordChanged = false)
    }

    fun clearSavedFlag() {
        _uiState.value = _uiState.value.copy(saved = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearAccountDeletedFlag() {
        _uiState.value = _uiState.value.copy(accountDeleted = false)
    }
}