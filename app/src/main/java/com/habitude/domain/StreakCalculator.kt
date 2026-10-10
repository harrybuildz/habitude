package com.habitude.domain

import javax.inject.Inject

/**
 * Pure streak math over epoch days (no clocks, no Android types), so it is
 * trivially unit-testable and reusable from UI and background work alike.
 */
class StreakCalculator @Inject constructor() {

    data class Streaks(val current: Int, val best: Int) {
        companion object {
            val NONE = Streaks(current = 0, best = 0)
        }
    }

    /**
     * @param completedEpochDays days on which the habit was completed.
     * @param todayEpochDay the caller's notion of "today"; future days are ignored.
     *
     * The current streak is anchored at today, or at yesterday when today has
     * not been checked yet — an unchecked today does not break the streak
     * until the day is actually over.
     */
    fun calculate(completedEpochDays: Set<Long>, todayEpochDay: Long): Streaks {
        val days = completedEpochDays.filter { it <= todayEpochDay }.sorted()
        if (days.isEmpty()) return Streaks.NONE

        var best = 1
        var run = 1
        for (i in 1 until days.size) {
            run = if (days[i] == days[i - 1] + 1) run + 1 else 1
            if (run > best) best = run
        }

        val anchor = when {
            days.contains(todayEpochDay) -> todayEpochDay
            days.contains(todayEpochDay - 1) -> todayEpochDay - 1
            else -> return Streaks(current = 0, best = best)
        }

        var current = 0
        var day = anchor
        while (days.contains(day)) {
            current++
            day--
        }
        return Streaks(current = current, best = best)
    }
}
