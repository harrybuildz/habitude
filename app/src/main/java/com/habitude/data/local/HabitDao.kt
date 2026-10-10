package com.habitude.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Transaction
    @Query("SELECT * FROM habits ORDER BY createdAtEpochDay ASC, id ASC")
    fun observeHabitsWithCompletions(): Flow<List<HabitWithCompletions>>

    @Insert
    suspend fun insert(habit: HabitEntity): Long

    @Query("DELETE FROM habits WHERE id = :habitId")
    suspend fun delete(habitId: Long)
}
