package com.example.mvp.ui.screens.training

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvp.domain.repository.TrainingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainingsViewModel @Inject constructor(
    private val repo: TrainingRepository
) : ViewModel() {

    private val userId = MutableStateFlow<Long?>(null)

    val trainings: StateFlow<List<Training>> =
        userId.filterNotNull()
            .distinctUntilChanged()
            .flatMapLatest { uid -> repo.observeTrainings(uid) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setUser(id: Long) { userId.value = id }

    fun save(training: Training) {
        val uid = userId.value ?: return
        viewModelScope.launch { repo.upsertTraining(uid, training) }
    }

    fun delete(training: Training) {
        val uid = userId.value ?: return
        viewModelScope.launch { repo.deleteTraining(uid, training) }
    }
}