package com.habitude.ui.today

import app.cash.turbine.test
import com.habitude.domain.StreakCalculator
import com.habitude.testutil.FakeHabitRepository
import com.habitude.testutil.FixedDateProvider
import com.habitude.testutil.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class TodayViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val today = 1000L
    private val repository = FakeHabitRepository()
    private val dateProvider = FixedDateProvider(today)

    private fun createViewModel() = TodayViewModel(
        repository = repository,
        streakCalculator = StreakCalculator(),
        dateProvider = dateProvider,
    )

    @Test
    fun `empty repository produces empty loaded state`() = runTest {
        val viewModel = createViewModel()

        viewModel.uiState.test {
            var state = awaitItem()
            if (state.isLoading) state = awaitItem()

            assertFalse(state.isLoading)
            assertTrue(state.habits.isEmpty())
        }
    }

    @Test
    fun `habits are mapped with streaks and completion flags`() = runTest {
        repository.seed(
            FakeHabitRepository.habit(
                id = 1,
                name = "Read",
                emoji = "📚",
                completedDays = listOf(today - 2, today - 1, today),
            ),
            FakeHabitRepository.habit(
                id = 2,
                name = "Run",
                emoji = "🏃",
                completedDays = listOf(today - 10, today - 9),
            ),
        )
        val viewModel = createViewModel()

        viewModel.uiState.test {
            var state = awaitItem()
            if (state.isLoading) state = awaitItem()

            assertEquals(2, state.habits.size)

            val read = state.habits.first { it.id == 1L }
            assertEquals("Read", read.name)
            assertEquals("📚", read.emoji)
            assertEquals(3, read.currentStreak)
            assertEquals(3, read.bestStreak)
            assertTrue(read.completedToday)

            val run = state.habits.first { it.id == 2L }
            assertEquals(0, run.currentStreak)
            assertEquals(2, run.bestStreak)
            assertFalse(run.completedToday)
        }
    }

    @Test
    fun `toggling an unchecked habit marks today completed`() = runTest {
        repository.seed(FakeHabitRepository.habit(id = 1, completedDays = emptyList()))
        val viewModel = createViewModel()

        viewModel.toggleCompletion(1)

        assertEquals(
            listOf(FakeHabitRepository.SetCompletedCall(habitId = 1, dateEpochDay = today, completed = true)),
            repository.setCompletedCalls,
        )
    }

    @Test
    fun `toggling a checked habit unmarks today`() = runTest {
        repository.seed(FakeHabitRepository.habit(id = 1, completedDays = listOf(today)))
        val viewModel = createViewModel()

        viewModel.toggleCompletion(1)

        assertEquals(
            listOf(FakeHabitRepository.SetCompletedCall(habitId = 1, dateEpochDay = today, completed = false)),
            repository.setCompletedCalls,
        )
    }

    @Test
    fun `toggling an unknown habit does nothing`() = runTest {
        val viewModel = createViewModel()

        viewModel.toggleCompletion(42)

        assertTrue(repository.setCompletedCalls.isEmpty())
    }

    @Test
    fun `deleting a habit delegates to the repository`() = runTest {
        repository.seed(FakeHabitRepository.habit(id = 7))
        val viewModel = createViewModel()

        viewModel.deleteHabit(7)

        assertEquals(listOf(7L), repository.deletedHabitIds)
    }

    @Test
    fun `state updates when repository emits a new list`() = runTest {
        val viewModel = createViewModel()

        viewModel.uiState.test {
            var state = awaitItem()
            if (state.isLoading) state = awaitItem()
            assertTrue(state.habits.isEmpty())

            repository.addHabit(name = "Stretch", emoji = "🧘", createdAtEpochDay = today)

            val updated = awaitItem()
            assertEquals(1, updated.habits.size)
            assertEquals("Stretch", updated.habits.first().name)
            assertFalse(updated.habits.first().completedToday)
        }
    }
}
