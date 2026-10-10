package com.habitude.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * One row per habit per completed day. The composite primary key makes
 * completion idempotent: checking an already-checked day cannot duplicate.
 */
@Entity(
    tableName = "completions",
    primaryKeys = ["habitId", "dateEpochDay"],
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("habitId")],
)
data class CompletionEntity(
    val habitId: Long,
    val dateEpochDay: Long,
)
