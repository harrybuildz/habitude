package com.habitude.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class StreakCalculatorTest {

    private val calculator = StreakCalculator()

    private val today = 1000L

    @Test
    fun `empty set has no streaks`() {
        val streaks = calculator.calculate(emptySet(), today)
        assertEquals(StreakCalculator.Streaks(0, 0), streaks)
    }

    @Test
    fun `single completion today is a one day streak`() {
        val streaks = calculator.calculate(setOf(today), today)
        assertEquals(StreakCalculator.Streaks(1, 1), streaks)
    }

    @Test
    fun `single completion yesterday still anchors current streak`() {
        val streaks = calculator.calculate(setOf(today - 1), today)
        assertEquals(StreakCalculator.Streaks(1, 1), streaks)
    }

    @Test
    fun `run ending yesterday counts as current streak`() {
        val days = setOf(today - 3, today - 2, today - 1)
        val streaks = calculator.calculate(days, today)
        assertEquals(StreakCalculator.Streaks(3, 3), streaks)
    }

    @Test
    fun `run ending today counts as current streak`() {
        val days = setOf(today - 2, today - 1, today)
        val streaks = calculator.calculate(days, today)
        assertEquals(StreakCalculator.Streaks(3, 3), streaks)
    }

    @Test
    fun `completion two days ago does not anchor current streak`() {
        val days = setOf(today - 4, today - 3, today - 2)
        val streaks = calculator.calculate(days, today)
        assertEquals(StreakCalculator.Streaks(0, 3), streaks)
    }

    @Test
    fun `gap breaks current streak but not best`() {
        // 5-day historical run, then a gap, then today checked.
        val days = setOf(today - 10, today - 9, today - 8, today - 7, today - 6, today)
        val streaks = calculator.calculate(days, today)
        assertEquals(StreakCalculator.Streaks(1, 5), streaks)
    }

    @Test
    fun `best streak equals current when current run is longest`() {
        val days = setOf(today - 20, today - 2, today - 1, today)
        val streaks = calculator.calculate(days, today)
        assertEquals(StreakCalculator.Streaks(3, 3), streaks)
    }

    @Test
    fun `current streak skips unchecked today and counts back from yesterday`() {
        val days = setOf(today - 5, today - 4, today - 3, today - 2, today - 1)
        val streaks = calculator.calculate(days, today)
        assertEquals(StreakCalculator.Streaks(5, 5), streaks)
    }

    @Test
    fun `future completions are ignored`() {
        val days = setOf(today, today + 1, today + 2)
        val streaks = calculator.calculate(days, today)
        assertEquals(StreakCalculator.Streaks(1, 1), streaks)
    }

    @Test
    fun `non consecutive days each count as runs of one`() {
        val days = setOf(today - 8, today - 6, today - 4, today - 2, today)
        val streaks = calculator.calculate(days, today)
        assertEquals(StreakCalculator.Streaks(1, 1), streaks)
    }
}
