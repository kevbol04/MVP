package com.example.mvp.ui.screens.training

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvp.domain.model.Training
import com.example.mvp.domain.repository.TrainingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainingsViewModel @Inject constructor(
    private val repo: TrainingRepository
) : ViewModel() {

    private val userId = MutableStateFlow<Long?>(null)

    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val messages: SharedFlow<String> = _messages.asSharedFlow()

    val trainings: StateFlow<List<Training>> =
        userId.filterNotNull()
            .distinctUntilChanged()
            .flatMapLatest { uid -> repo.observeTrainings(uid) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setUser(id: Long) {
        userId.value = id
    }

    fun save(
        training: Training,
        onSuccess: () -> Unit = {}
    ) {
        val uid = userId.value

        if (uid == null || uid <= 0L) {
            emitMessage("No se pudo guardar el entrenamiento porque no hay una sesión activa.")
            return
        }

        viewModelScope.launch {
            runCatching {
                repo.upsertTraining(uid, training)
            }.onSuccess {
                onSuccess()
            }.onFailure { throwable ->
                _messages.emit(
                    throwable.toUserMessage("No se pudo guardar el entrenamiento.")
                )
            }
        }
    }

    fun delete(
        training: Training,
        onSuccess: () -> Unit = {}
    ) {
        val uid = userId.value

        if (uid == null || uid <= 0L) {
            emitMessage("No se pudo eliminar el entrenamiento porque no hay una sesión activa.")
            return
        }

        viewModelScope.launch {
            runCatching {
                repo.deleteTraining(uid, training)
            }.onSuccess {
                onSuccess()
            }.onFailure { throwable ->
                _messages.emit(
                    throwable.toUserMessage("No se pudo eliminar el entrenamiento.")
                )
            }
        }
    }

    fun toggleDone(
        training: Training,
        onSuccess: () -> Unit = {}
    ) {
        val uid = userId.value

        if (uid == null || uid <= 0L) {
            emitMessage("No se pudo actualizar el entrenamiento porque no hay una sesión activa.")
            return
        }

        viewModelScope.launch {
            runCatching {
                repo.toggleTrainingDone(uid, training)
            }.onSuccess {
                onSuccess()
            }.onFailure { throwable ->
                _messages.emit(
                    throwable.toUserMessage("No se pudo actualizar el estado del entrenamiento.")
                )
            }
        }
    }

    private fun emitMessage(message: String) {
        _messages.tryEmit(message)
    }

    private fun Throwable.toUserMessage(defaultMessage: String): String {
        val cleanMessage = message?.trim().orEmpty()
        return cleanMessage.ifBlank { defaultMessage }
    }
}