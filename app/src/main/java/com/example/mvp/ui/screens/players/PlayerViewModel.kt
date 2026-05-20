package com.example.mvp.ui.screens.players

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvp.domain.model.Club
import com.example.mvp.domain.model.Player
import com.example.mvp.domain.repository.ClubRepository
import com.example.mvp.domain.repository.PlayerRepository
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayersViewModel @Inject constructor(
    private val repo: PlayerRepository,
    private val clubRepository: ClubRepository
) : ViewModel() {

    private val userId = MutableStateFlow<Long?>(null)

    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val messages: SharedFlow<String> = _messages.asSharedFlow()

    val players: StateFlow<List<Player>> =
        userId.filterNotNull()
            .distinctUntilChanged()
            .flatMapLatest { uid -> repo.observePlayers(uid) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val selectedFormationId: StateFlow<String> =
        userId.filterNotNull()
            .distinctUntilChanged()
            .flatMapLatest { uid -> clubRepository.observeClub(uid) }
            .map { club -> club?.selectedFormationId ?: Club.DEFAULT_FORMATION_ID }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Club.DEFAULT_FORMATION_ID)

    fun setUser(id: Long) {
        userId.value = id
    }

    fun playerById(playerId: Int) =
        userId.filterNotNull()
            .distinctUntilChanged()
            .flatMapLatest { uid -> repo.observePlayer(uid, playerId) }

    fun save(
        player: Player,
        onSuccess: () -> Unit = {}
    ) {
        val uid = userId.value
        if (uid == null) {
            emitMessage("No se pudo guardar el jugador porque no hay una sesión activa.")
            return
        }

        viewModelScope.launch {
            runCatching {
                repo.save(uid, player.normalized())
            }.onSuccess {
                onSuccess()
            }.onFailure { throwable ->
                _messages.emit(throwable.toUserMessage("No se pudo guardar el jugador."))
            }
        }
    }

    fun delete(
        player: Player,
        onSuccess: () -> Unit = {}
    ) {
        val uid = userId.value
        if (uid == null) {
            emitMessage("No se pudo eliminar el jugador porque no hay una sesión activa.")
            return
        }

        viewModelScope.launch {
            runCatching {
                repo.delete(uid, player)
            }.onSuccess {
                onSuccess()
            }.onFailure { throwable ->
                _messages.emit(throwable.toUserMessage("No se pudo eliminar el jugador."))
            }
        }
    }

    fun saveSelectedFormation(
        formationId: String,
        onSuccess: () -> Unit = {}
    ) {
        val uid = userId.value
        if (uid == null) {
            emitMessage("No se pudo guardar la formación porque no hay una sesión activa.")
            return
        }

        viewModelScope.launch {
            runCatching {
                clubRepository.updateSelectedFormation(uid, formationId)
            }.onSuccess {
                onSuccess()
            }.onFailure { throwable ->
                _messages.emit(throwable.toUserMessage("No se pudo guardar la formación."))
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