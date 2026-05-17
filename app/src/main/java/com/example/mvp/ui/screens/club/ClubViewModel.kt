package com.example.mvp.ui.screens.club

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvp.domain.model.Club
import com.example.mvp.domain.repository.ClubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClubViewModel @Inject constructor(
    private val repo: ClubRepository
) : ViewModel() {

    private val userId = MutableStateFlow<Long?>(null)
    private val _loaded = MutableStateFlow(false)

    val loaded: StateFlow<Boolean> = _loaded

    val club: StateFlow<Club?> =
        userId.filterNotNull()
            .distinctUntilChanged()
            .flatMapLatest { uid ->
                repo.observeClub(uid)
                    .onStart { _loaded.value = false }
                    .onEach { _loaded.value = true }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun setUser(id: Long) {
        if (userId.value != id) {
            _loaded.value = false
            userId.value = id
        }
    }

    fun save(club: Club, onSaved: () -> Unit = {}) {
        val uid = userId.value ?: return
        viewModelScope.launch {
            repo.save(uid, club)
            onSaved()
        }
    }
}