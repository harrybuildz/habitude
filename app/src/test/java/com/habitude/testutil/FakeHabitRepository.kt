package com.habitude.testutil

import com.habitude.data.HabitRepository
import com.habitude.data.local.CompletionEntity
import com.habitude.data.local.HabitEntity
import com.habitude.data.local.HabitWithCompletions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeHabitRepository : HabitRepository {

    private val habitsFlow = MutableStateFlow<List<HabitWithCompletions>>(emptyList())
    private var nextId = 1L

    data class SetCompletedCall(val habitId: Long, val dateEpochDay: Long, val completed: Boolean)

    val setCompletedCalls = mutableListOf<SetCompletedCall>()
    val deletedHabitIds = mutableListOf<Long>()

    fun seed(vararg habits: HabitWithCompletions) {
        habitsFlow.value = habits.toList()
    }

    override fun observeHabitsWithCompletions(): Flow<List<HabitWithCompletions>> =
        habitsFlow.asStateFlow()

    override suspend fun addHabit(name: String, emoji: String, createdAtEpochDay: Long) {
        val habit = HabitEntity(
            id = nextId++,
            name = name,
            emoji = emoji,
            createdAtEpochDay = createdAtEpochDay,
        )
        habitsFlow.update { it + HabitWithCompletions(habit, emptyList()) }
    }

    override suspend fun deleteHabit(habitId: Long) {
        deletedHabitIds += habitId
        habitsFlow.update { list -> list.filterNot { it.habit.id == habitId } }
    }

    override suspend fun setCompleted(habitId: Long, dateEpochDay: Long, completed: Boolean) {
        setCompletedCalls += SetCompletedCall(habitId, dateEpochDay, completed)
        habitsFlow.update { list ->
            list.map { item ->
                if (item.habit.id != habitId) return@map item
                val completions = if (completed) {
                    (item.completions + CompletionEntity(habitId, dateEpochDay)).distinct()
                } else {
                    item.completions.filterNot { it.dateEpochDay == dateEpochDay }
                }
                item.copy(completions = completions)
            }
        }
    }

    companion object {
        fun habit(
            id: Long,
            name: String = "Habit $id",
            emoji: String = "💪",
            createdAtEpochDay: Long = 0,
            completedDays: List<Long> = emptyList(),
        ): HabitWithCompletions = HabitWithCompletions(
            habit = HabitEntity(id = id, name = name, emoji = emoji, createdAtEpochDay = createdAtEpochDay),
            completions = completedDays.map { CompletionEntity(habitId = id, dateEpochDay = it) },
        )
    }
}
