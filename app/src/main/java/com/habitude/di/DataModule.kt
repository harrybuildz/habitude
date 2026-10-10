package com.habitude.di

import com.habitude.data.DataStoreSettingsRepository
import com.habitude.data.HabitRepository
import com.habitude.data.OfflineHabitRepository
import com.habitude.data.SettingsRepository
import com.habitude.domain.DateProvider
import com.habitude.domain.SystemDateProvider
import com.habitude.work.ReminderScheduler
import com.habitude.work.WorkManagerReminderScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindHabitRepository(impl: OfflineHabitRepository): HabitRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: DataStoreSettingsRepository): SettingsRepository

    @Binds
    abstract fun bindDateProvider(impl: SystemDateProvider): DateProvider

    @Binds
    abstract fun bindReminderScheduler(impl: WorkManagerReminderScheduler): ReminderScheduler
}
