package com.example.mvp.ui.screens.players

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvp.domain.model.Club
import com.example.mvp.domain.repository.ClubRepository
import com.example.mvp.domain.repository.PlayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.example.mvp.domain.model.Player
import javax.inject.Inject

@HiltViewModel
class PlayersViewModel @Inject constructor(
    private val repo: PlayerRepository,
    private val clubRepository: ClubRepository
) : ViewModel() {

    private val userId = MutableStateFlow<Long?>(null)

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

    fun saveSelectedFormation(formationId: String) {
        val uid = userId.value ?: return
        viewModelScope.launch {
            clubRepository.updateSelectedFormation(uid, formationId)
        }
    }
}