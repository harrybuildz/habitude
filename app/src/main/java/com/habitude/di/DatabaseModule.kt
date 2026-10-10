package com.habitude.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.habitude.data.local.CompletionDao
import com.habitude.data.local.HabitDao
import com.habitude.data.local.HabitudeDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HabitudeDatabase =
        Room.databaseBuilder(context, HabitudeDatabase::class.java, "habitude.db").build()

    @Provides
    fun provideHabitDao(database: HabitudeDatabase): HabitDao = database.habitDao()

    @Provides
    fun provideCompletionDao(database: HabitudeDatabase): CompletionDao = database.completionDao()

    @Provides
    @Singleton
    fun provideSettingsDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.settingsDataStore
}
