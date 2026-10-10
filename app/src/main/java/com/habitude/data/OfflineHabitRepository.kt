package com.habitude.data

import com.habitude.data.local.CompletionDao
import com.habitude.data.local.CompletionEntity
import com.habitude.data.local.HabitDao
import com.habitude.data.local.HabitEntity
import com.habitude.data.local.HabitWithCompletions
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

/** Room-backed implementation; the database is the single source of truth. */
@Singleton
class OfflineHabitRepository @Inject constructor(
    private val habitDao: HabitDao,
    private val completionDao: CompletionDao,
) : HabitRepository {

    override fun observeHabitsWithCompletions(): Flow<List<HabitWithCompletions>> =
        habitDao.observeHabitsWithCompletions()

    override suspend fun addHabit(name: String, emoji: String, createdAtEpochDay: Long) {
        habitDao.insert(HabitEntity(name = name, emoji = emoji, createdAtEpochDay = createdAtEpochDay))
    }

    override suspend fun deleteHabit(habitId: Long) {
        habitDao.delete(habitId)
    }

    override suspend fun setCompleted(habitId: Long, dateEpochDay: Long, completed: Boolean) {
        if (completed) {
            completionDao.insert(CompletionEntity(habitId = habitId, dateEpochDay = dateEpochDay))
        } else {
            completionDao.delete(habitId = habitId, dateEpochDay = dateEpochDay)
        }
    }
}
