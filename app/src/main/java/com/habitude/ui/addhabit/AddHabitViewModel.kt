package com.habitude.ui.addhabit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitude.data.HabitRepository
import com.habitude.domain.DateProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddHabitUiState(
    val name: String = "",
    val emoji: String = AddHabitViewModel.EMOJI_OPTIONS.first(),
) {
    val canSave: Boolean get() = name.isNotBlank()
}

@HiltViewModel
class AddHabitViewModel @Inject constructor(
    private val repository: HabitRepository,
    private val dateProvider: DateProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddHabitUiState())
    val uiState: StateFlow<AddHabitUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onEmojiSelected(emoji: String) {
        _uiState.update { it.copy(emoji = emoji) }
    }

    fun save(onSaved: () -> Unit) {
        val state = _uiState.value
        if (!state.canSave) return
        viewModelScope.launch {
            repository.addHabit(
                name = state.name.trim(),
                emoji = state.emoji,
                createdAtEpochDay = dateProvider.todayEpochDay(),
            )
            onSaved()
        }
    }

    companion object {
        val EMOJI_OPTIONS = listOf(
            "💪", "🏃", "📚", "🧘", "💧", "😴",
            "🥗", "✍️", "🎸", "🧹", "🧠", "☀️",
            "🚭", "💊", "🦷", "🚶", "🎨", "💻",
        )
    }
}
