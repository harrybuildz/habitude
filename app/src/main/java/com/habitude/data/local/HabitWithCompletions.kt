package com.habitude.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class HabitWithCompletions(
    @Embedded val habit: HabitEntity,
    @Relation(parentColumn = "id", entityColumn = "habitId")
    val completions: List<CompletionEntity>,
)
