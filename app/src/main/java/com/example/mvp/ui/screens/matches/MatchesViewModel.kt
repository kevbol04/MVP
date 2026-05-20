package com.example.mvp.ui.screens.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvp.domain.model.Match
import com.example.mvp.domain.repository.MatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchesViewModel @Inject constructor(
    private val repo: MatchRepository
) : ViewModel() {

    private val userId = MutableStateFlow<Long?>(null)
    private val _messages = MutableSharedFlow<String>()
    val messages: SharedFlow<String> = _messages

    val matches: StateFlow<List<Match>> =
        userId.filterNotNull()
            .distinctUntilChanged()
            .flatMapLatest { uid -> repo.observeMatches(uid) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setUser(id: Long) {
        userId.value = id
    }

    fun matchById(matchId: Int) =
        userId.filterNotNull()
            .distinctUntilChanged()
            .flatMapLatest { uid -> repo.observeMatch(uid, matchId) }

    fun save(match: Match, onSuccess: () -> Unit = {}) {
        val uid = userId.value
        if (uid == null || uid <= 0L) {
            viewModelScope.launch { _messages.emit("La sesión no es válida.") }
            return
        }

        viewModelScope.launch {
            runCatching { repo.upsertMatch(uid, match) }
                .onSuccess { onSuccess() }
                .onFailure { error -> _messages.emit(error.message ?: "No se pudo guardar el partido.") }
        }
    }

    fun delete(match: Match, onSuccess: () -> Unit = {}) {
        val uid = userId.value
        if (uid == null || uid <= 0L) {
            viewModelScope.launch { _messages.emit("La sesión no es válida.") }
            return
        }

        viewModelScope.launch {
            runCatching { repo.deleteMatch(uid, match) }
                .onSuccess { onSuccess() }
                .onFailure { error -> _messages.emit(error.message ?: "No se pudo eliminar el partido.") }
        }
    }
}