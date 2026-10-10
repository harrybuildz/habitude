package com.habitude.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CompletionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(completion: CompletionEntity)

    @Query("DELETE FROM completions WHERE habitId = :habitId AND dateEpochDay = :dateEpochDay")
    suspend fun delete(habitId: Long, dateEpochDay: Long)
}
