package com.example.fitnesspaw.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "habit_prefs"
)

class HabitPreferences(
    private val context: Context
) {

    companion object {

        val WORKOUT =
            booleanPreferencesKey("workout")

        val WATER =
            booleanPreferencesKey("water")

        val WALK =
            booleanPreferencesKey("walk")

        val SLEEP =
            booleanPreferencesKey("sleep")
    }

    suspend fun saveHabit(

        key: androidx.datastore.preferences.core.Preferences.Key<Boolean>,

        value: Boolean
    ) {

        context.dataStore.edit {

            it[key] = value
        }
    }

    fun getHabit(

        key: androidx.datastore.preferences.core.Preferences.Key<Boolean>
    ): Flow<Boolean> {

        return context.dataStore.data.map {

            it[key] ?: false
        }
    }
}