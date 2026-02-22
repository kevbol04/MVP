package com.example.mvp.ui.screens.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvp.domain.repository.MatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchesViewModel @Inject constructor(
    private val repo: MatchRepository
) : ViewModel() {

    private val userId = MutableStateFlow<Long?>(null)

    val matches: StateFlow<List<Match>> =
        userId.filterNotNull()
            .distinctUntilChanged()
            .flatMapLatest { uid -> repo.observeMatches(uid) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setUser(id: Long) { userId.value = id }

    fun matchById(matchId: Int): Flow<Match?> {
        return userId.filterNotNull()
            .distinctUntilChanged()
            .flatMapLatest { uid -> repo.observeMatch(uid, matchId) }
    }

    fun save(match: Match) {
        val uid = userId.value ?: return
        viewModelScope.launch { repo.upsertMatch(uid, match) }
    }

    fun delete(match: Match) {
        val uid = userId.value ?: return
        viewModelScope.launch { repo.deleteMatch(uid, match) }
    }
}