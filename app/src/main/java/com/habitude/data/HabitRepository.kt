package com.habitude.data

import com.habitude.data.local.HabitWithCompletions
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun observeHabitsWithCompletions(): Flow<List<HabitWithCompletions>>
    suspend fun addHabit(name: String, emoji: String, createdAtEpochDay: Long)
    suspend fun deleteHabit(habitId: Long)
    suspend fun setCompleted(habitId: Long, dateEpochDay: Long, completed: Boolean)
}
