package com.example.mvp.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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

    LaunchedEffect(state.saved) {
        if (state.saved) {
            onProfileUpdated(name /* (se sobreescribe en AccountScreen) */, email /* idem */)
            vm.clearSavedFlag()
        }
    }

    LaunchedEffect(state.error) {
        val msg = state.error ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        vm.clearError()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.Transparent
    ) { padding ->
        Box(Modifier.fillMaxSize()) {

            AccountScreen(
                modifier = Modifier.fillMaxSize(),
                name = name,
                email = email,
                onBack = onBack,
                onSave = { newName, newEmail ->
                    vm.saveProfile(oldEmail = email, newName = newName, newEmail = newEmail)
                    onProfileUpdated(newName, newEmail)
                },
                onDeleteAccount = onDeleteAccount
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