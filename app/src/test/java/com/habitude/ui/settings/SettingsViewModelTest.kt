package com.habitude.ui.settings

import app.cash.turbine.test
import com.habitude.data.UserSettings
import com.habitude.testutil.FakeReminderScheduler
import com.habitude.testutil.FakeSettingsRepository
import com.habitude.testutil.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val scheduler = FakeReminderScheduler()

    private fun createViewModel(
        repository: FakeSettingsRepository = FakeSettingsRepository(),
    ) = SettingsViewModel(
        settingsRepository = repository,
        reminderScheduler = scheduler,
    ) to repository

    @Test
    fun `default state follows system theme with reminders off`() = runTest {
        val (viewModel, _) = createViewModel()

        viewModel.uiState.test {
            val state = awaitItem()
            assertNull(state.darkTheme)
            assertFalse(state.reminderEnabled)
            assertEquals(20, state.reminderHour)
        }
    }

    @Test
    fun `stored settings are reflected in ui state`() = runTest {
        val (viewModel, _) = createViewModel(
            FakeSettingsRepository(
                UserSettings(darkTheme = true, reminderEnabled = true, reminderHour = 7),
            ),
        )

        viewModel.uiState.test {
            var state = awaitItem()
            if (state.darkTheme == null) state = awaitItem()

            assertEquals(true, state.darkTheme)
            assertTrue(state.reminderEnabled)
            assertEquals(7, state.reminderHour)
        }
    }

    @Test
    fun `setting dark theme persists the choice`() = runTest {
        val (viewModel, repository) = createViewModel()

        viewModel.setDarkTheme(true)

        assertEquals(true, repository.current.darkTheme)
    }

    @Test
    fun `enabling reminders schedules at the stored hour`() = runTest {
        val (viewModel, repository) = createViewModel(
            FakeSettingsRepository(UserSettings(reminderHour = 9)),
        )

        viewModel.setReminderEnabled(true)

        assertTrue(repository.current.reminderEnabled)
        assertEquals(listOf(9), scheduler.scheduledHours)
    }

    @Test
    fun `disabling reminders cancels scheduled work`() = runTest {
        val (viewModel, repository) = createViewModel(
            FakeSettingsRepository(UserSettings(reminderEnabled = true)),
        )

        viewModel.setReminderEnabled(false)

        assertFalse(repository.current.reminderEnabled)
        assertEquals(1, scheduler.cancelCount)
        assertTrue(scheduler.scheduledHours.isEmpty())
    }

    @Test
    fun `changing hour while enabled reschedules`() = runTest {
        val (viewModel, repository) = createViewModel(
            FakeSettingsRepository(UserSettings(reminderEnabled = true, reminderHour = 20)),
        )

        viewModel.setReminderHour(6)

        assertEquals(6, repository.current.reminderHour)
        assertEquals(listOf(6), scheduler.scheduledHours)
    }

    @Test
    fun `changing hour while disabled only persists`() = runTest {
        val (viewModel, repository) = createViewModel()

        viewModel.setReminderHour(6)

        assertEquals(6, repository.current.reminderHour)
        assertTrue(scheduler.scheduledHours.isEmpty())
        assertEquals(0, scheduler.cancelCount)
    }
}
