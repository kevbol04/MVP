package com.example.mvp.ui.screens.players

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvp.domain.repository.PlayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayersViewModel @Inject constructor(
    private val repo: PlayerRepository
) : ViewModel() {

    private val userId = MutableStateFlow<Long?>(null)

    val players: StateFlow<List<Player>> =
        userId.filterNotNull()
            .distinctUntilChanged()
            .flatMapLatest { uid -> repo.observePlayers(uid) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setUser(id: Long) {
        userId.value = id
    }

    fun playerById(playerId: Int): Flow<Player?> {
        return userId.filterNotNull()
            .distinctUntilChanged()
            .flatMapLatest { uid -> repo.observePlayer(uid, playerId) }
    }

    fun save(player: Player) {
        val uid = userId.value ?: return
        viewModelScope.launch { repo.save(uid, player) }
    }

    fun delete(player: Player) {
        val uid = userId.value ?: return
        viewModelScope.launch { repo.delete(uid, player) }
    }
}