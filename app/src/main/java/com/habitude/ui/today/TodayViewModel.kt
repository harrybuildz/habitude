package com.habitude.ui.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitude.data.HabitRepository
import com.habitude.domain.DateProvider
import com.habitude.domain.StreakCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TodayHabit(
    val id: Long,
    val name: String,
    val emoji: String,
    val currentStreak: Int,
    val bestStreak: Int,
    val completedToday: Boolean,
)

data class TodayUiState(
    val habits: List<TodayHabit> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class TodayViewModel @Inject constructor(
    private val repository: HabitRepository,
    private val streakCalculator: StreakCalculator,
    private val dateProvider: DateProvider,
) : ViewModel() {

    val uiState: StateFlow<TodayUiState> = repository.observeHabitsWithCompletions()
        .map { habits ->
            val today = dateProvider.todayEpochDay()
            TodayUiState(
                habits = habits.map { item ->
                    val completedDays = item.completions.map { it.dateEpochDay }.toSet()
                    val streaks = streakCalculator.calculate(completedDays, today)
                    TodayHabit(
                        id = item.habit.id,
                        name = item.habit.name,
                        emoji = item.habit.emoji,
                        currentStreak = streaks.current,
                        bestStreak = streaks.best,
                        completedToday = completedDays.contains(today),
                    )
                },
                isLoading = false,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TodayUiState(),
        )

    fun toggleCompletion(habitId: Long) {
        viewModelScope.launch {
            val today = dateProvider.todayEpochDay()
            val habit = repository.observeHabitsWithCompletions().first()
                .firstOrNull { it.habit.id == habitId } ?: return@launch
            val completedToday = habit.completions.any { it.dateEpochDay == today }
            repository.setCompleted(habitId, today, !completedToday)
        }
    }

    fun deleteHabit(habitId: Long) {
        viewModelScope.launch {
            repository.deleteHabit(habitId)
        }
    }
}
