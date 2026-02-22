package com.example.mvp.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun AccountRoute(
    name: String,
    email: String,
    onBack: () -> Unit,
    onProfileUpdated: (newName: String, newEmail: String) -> Unit,
    onDeleteAccount: () -> Unit,
    vm: AccountViewModel = hiltViewModel()
) {
    val state by vm.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var pendingProfileUpdate by remember { mutableStateOf<Pair<String, String>?>(null) }

    LaunchedEffect(state.saved) {
        if (state.saved) {
            pendingProfileUpdate?.let { (n, e) ->
                onProfileUpdated(n, e)
            }
            pendingProfileUpdate = null
            vm.clearSavedFlag()
        }
    }

    LaunchedEffect(state.passwordChanged) {
        if (state.passwordChanged) {
            snackbarHostState.showSnackbar("Contraseña actualizada correctamente.")
            vm.clearPasswordChangedFlag()
        }
    }

    LaunchedEffect(state.accountDeleted) {
        if (state.accountDeleted) {
            vm.clearAccountDeletedFlag()
            onDeleteAccount()
        }
    }

    LaunchedEffect(state.error) {
        val msg = state.error ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AccountScreen(
                modifier = Modifier.fillMaxSize(),
                name = name,
                email = email,
                onBack = onBack,
                onSave = { newName, newEmail ->
                    pendingProfileUpdate = newName to newEmail
                    vm.saveProfile(oldEmail = email, newName = newName, newEmail = newEmail)
                },
                onChangePassword = { current, new ->
                    vm.changePassword(email = email, current = current, newPass = new)
                },
                onDeleteAccount = {
                    vm.deleteAccount(email)
                },
                passwordLoading = state.loading,
                passwordError = state.error,
                passwordChanged = state.passwordChanged,
                onPasswordChangedConsumed = { vm.clearPasswordChangedFlag() },
                onPasswordErrorConsumed = { vm.clearError() },
                deleteLoading = state.loading,
                deleteError = state.error,
                accountDeleted = state.accountDeleted,
                onDeleteErrorConsumed = { vm.clearError() }
            )

            if (state.loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}