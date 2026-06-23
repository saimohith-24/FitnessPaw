package com.example.fitnesspaw.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "fitnesspaw"
)

class UserPreferences(
    private val context: Context
) {

    companion object {
        val SELECTED_PET =
            intPreferencesKey("selected_pet")
        val STEP_GOAL = intPreferencesKey("step_goal")
        val WATER_GOAL = intPreferencesKey("water_goal")

        // COINS
        val COINS = intPreferencesKey("coins")

        // STREAK
        val STREAK = intPreferencesKey("streak")

        // LAST DATE
        val LAST_DATE = intPreferencesKey("last_date")

        // GOAL COMPLETED
        val GOAL_COMPLETED =
            intPreferencesKey("goal_completed")

        val USERNAME = stringPreferencesKey("username")
        val WATER_INTAKE = intPreferencesKey("water_intake")
        val DAILY_STEPS = intPreferencesKey("daily_steps")
        val STEP_SENSOR_BASELINE = intPreferencesKey("step_sensor_baseline")
        val LAST_STREAK_INCREMENT_DATE = intPreferencesKey("last_streak_increment_date")
        val THEME_MODE = intPreferencesKey("theme_mode")
        val HABITS_JSON = stringPreferencesKey("habits_json")
        val PET_NAME = stringPreferencesKey("pet_name")
        val PET_HAPPINESS = intPreferencesKey("pet_happiness")
        val WEIGHT = stringPreferencesKey("weight")
        val HEIGHT = stringPreferencesKey("height")
        val CALORIE_GOAL = intPreferencesKey("calorie_goal")
    }

    // =========================
    // SAVE COINS
    // =========================

    suspend fun saveCoins(coins: Int) {

        context.dataStore.edit {

            it[COINS] = coins
        }
    }

    // =========================
    // GET COINS
    // =========================

    val coinsFlow = context.dataStore.data.map {

        it[COINS] ?: 0
    }

    // =========================
    // SAVE STREAK
    // =========================

    suspend fun saveStreak(streak: Int) {

        context.dataStore.edit {

            it[STREAK] = streak
        }
    }

    // =========================
    // GET STREAK
    // =========================

    val streakFlow = context.dataStore.data.map {

        it[STREAK] ?: 0
    }

    // =========================
    // SAVE LAST DATE
    // =========================

    suspend fun saveLastDate(date: Int) {

        context.dataStore.edit {

            it[LAST_DATE] = date
        }
    }

    // =========================
    // GET LAST DATE
    // =========================

    val lastDateFlow = context.dataStore.data.map {

        it[LAST_DATE] ?: 0
    }

    // =========================
    // SAVE GOAL COMPLETED
    // =========================

    suspend fun saveGoalCompleted(value: Int) {

        context.dataStore.edit {

            it[GOAL_COMPLETED] = value
        }
    }

    // =========================
    // GET GOAL COMPLETED
    // =========================

    val goalCompletedFlow =
        context.dataStore.data.map {

            it[GOAL_COMPLETED] ?: 0
        }

    // =========================
    // SAVE STEP GOAL
    // =========================

    suspend fun saveStepGoal(goal: Int) {

        context.dataStore.edit {

            it[STEP_GOAL] = goal
        }
    }

    // =========================
    // GET STEP GOAL
    // =========================

    val stepGoalFlow = context.dataStore.data.map {

        it[STEP_GOAL] ?: 7000
    }

    // =========================
    // SAVE WATER GOAL
    // =========================

    suspend fun saveWaterGoal(goal: Int) {
        context.dataStore.edit {
            it[WATER_GOAL] = goal
        }
    }

    // =========================
    // GET WATER GOAL
    // =========================

    val waterGoalFlow = context.dataStore.data.map {
        it[WATER_GOAL] ?: 8
    }

    suspend fun saveSelectedPet(pet: Int) {

        context.dataStore.edit {

            it[SELECTED_PET] = pet
        }
    }

    val selectedPetFlow = context.dataStore.data.map {

        it[SELECTED_PET] ?: 0
    }

    // =========================
    // SAVE USERNAME
    // =========================
    suspend fun saveUsername(name: String) {
        context.dataStore.edit {
            it[USERNAME] = name
        }
    }

    val usernameFlow = context.dataStore.data.map {
        it[USERNAME] ?: ""
    }

    // =========================
    // SAVE WATER INTAKE
    // =========================
    suspend fun saveWaterIntake(water: Int) {
        context.dataStore.edit {
            it[WATER_INTAKE] = water
        }
    }

    val waterIntakeFlow = context.dataStore.data.map {
        it[WATER_INTAKE] ?: 0
    }

    // =========================
    // SAVE DAILY STEPS
    // =========================
    suspend fun saveDailySteps(steps: Int) {
        context.dataStore.edit {
            it[DAILY_STEPS] = steps
        }
    }

    val dailyStepsFlow = context.dataStore.data.map {
        it[DAILY_STEPS] ?: 0
    }

    // =========================
    // SAVE STEP SENSOR BASELINE
    // =========================
    suspend fun saveStepSensorBaseline(baseline: Int) {
        context.dataStore.edit {
            it[STEP_SENSOR_BASELINE] = baseline
        }
    }

    val stepSensorBaselineFlow = context.dataStore.data.map {
        it[STEP_SENSOR_BASELINE] ?: -1
    }

    // =========================
    // SAVE LAST STREAK INCREMENT DATE
    // =========================
    suspend fun saveLastStreakIncrementDate(date: Int) {
        context.dataStore.edit {
            it[LAST_STREAK_INCREMENT_DATE] = date
        }
    }

    val lastStreakIncrementDateFlow = context.dataStore.data.map {
        it[LAST_STREAK_INCREMENT_DATE] ?: 0
    }

    // =========================
    // SAVE THEME MODE
    // =========================
    suspend fun saveThemeMode(mode: Int) {
        context.dataStore.edit {
            it[THEME_MODE] = mode
        }
    }

    val themeModeFlow = context.dataStore.data.map {
        it[THEME_MODE] ?: 2 // Default to 2 (System)
    }

    // =========================
    // SAVE HABITS JSON
    // =========================
    suspend fun saveHabitsJson(json: String) {
        context.dataStore.edit {
            it[HABITS_JSON] = json
        }
    }

    val habitsJsonFlow = context.dataStore.data.map {
        it[HABITS_JSON] ?: ""
    }

    // =========================
    // SAVE PET NAME
    // =========================
    suspend fun savePetName(name: String) {
        context.dataStore.edit {
            it[PET_NAME] = name
        }
    }

    val petNameFlow = context.dataStore.data.map {
        it[PET_NAME] ?: "Buddy"
    }

    // =========================
    // SAVE PET HAPPINESS
    // =========================
    suspend fun savePetHappiness(happiness: Int) {
        context.dataStore.edit {
            it[PET_HAPPINESS] = happiness
        }
    }

    val petHappinessFlow = context.dataStore.data.map {
        it[PET_HAPPINESS] ?: 0
    }

    suspend fun saveWeight(weight: String) {
        context.dataStore.edit {
            it[WEIGHT] = weight
        }
    }

    val weightFlow = context.dataStore.data.map {
        it[WEIGHT] ?: ""
    }

    suspend fun saveHeight(height: String) {
        context.dataStore.edit {
            it[HEIGHT] = height
        }
    }

    val heightFlow = context.dataStore.data.map {
        it[HEIGHT] ?: ""
    }

    suspend fun saveCalorieGoal(goal: Int) {
        context.dataStore.edit {
            it[CALORIE_GOAL] = goal
        }
    }

    val calorieGoalFlow = context.dataStore.data.map {
        it[CALORIE_GOAL] ?: 500
    }
}
