package com.example.flats.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.onboardingDataStore: DataStore<Preferences> by preferencesDataStore(name = "onboarding")

object OnboardingPreferences {
    private val COMPLETED = booleanPreferencesKey("completed")

    fun isCompleted(context: Context): Flow<Boolean> =
        context.onboardingDataStore.data.map { prefs -> prefs[COMPLETED] ?: false }

    suspend fun setCompleted(context: Context) {
        context.onboardingDataStore.edit { prefs -> prefs[COMPLETED] = true }
    }
}