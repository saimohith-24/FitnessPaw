package com.example.fitnesspaw.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnesspaw.data.CustomHabit
import com.example.fitnesspaw.data.UserPreferences
import com.example.fitnesspaw.firestore.FirestoreManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar
import java.util.UUID

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)
    private val firestoreManager = FirestoreManager()

    // EXPOSE DATASTORE FLOWS AS STATEFLOWS
    val username: StateFlow<String> = userPreferences.usernameFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ""
    )

    val coins: StateFlow<Int> = userPreferences.coinsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val streak: StateFlow<Int> = userPreferences.streakFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val selectedPet: StateFlow<Int> = userPreferences.selectedPetFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0 // 0 = Cat, 1 = Dog, 2 = Panda
    )

    val waterIntake: StateFlow<Int> = userPreferences.waterIntakeFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val dailySteps: StateFlow<Int> = userPreferences.dailyStepsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val stepGoal: StateFlow<Int> = userPreferences.stepGoalFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 7000
    )

    val waterGoal: StateFlow<Int> = userPreferences.waterGoalFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 8
    )

    val petName: StateFlow<String> = userPreferences.petNameFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "Buddy"
    )

    val petHappiness: StateFlow<Int> = userPreferences.petHappinessFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 80
    )

    val weight: StateFlow<String> = userPreferences.weightFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ""
    )

    val height: StateFlow<String> = userPreferences.heightFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ""
    )

    val calorieGoal: StateFlow<Int> = userPreferences.calorieGoalFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 500
    )

    val caloriesBurned: StateFlow<Int> = kotlinx.coroutines.flow.combine(
        dailySteps,
        weight
    ) { steps, weightStr ->
        val weightVal = weightStr.toDoubleOrNull() ?: 70.0
        val baseCalories = steps * 0.04
        val scale = weightVal / 70.0
        (baseCalories * scale).toInt()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    // DYNAMIC HABITS FLOW
    val customHabits: StateFlow<List<CustomHabit>> = userPreferences.habitsJsonFlow.map { json ->
        deserializeHabits(json)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _isSensorSupported = MutableStateFlow(true)
    val isSensorSupported: StateFlow<Boolean> = _isSensorSupported.asStateFlow()

    init {
        checkDailyReset()
    }

    fun setSensorSupported(supported: Boolean) {
        _isSensorSupported.value = supported
    }

    // ===================================
    // DATE HELPER
    // ===================================
    private fun getCurrentDateInt(): Int {
        val cal = Calendar.getInstance()
        return cal.get(Calendar.YEAR) * 10000 +
                (cal.get(Calendar.MONTH) + 1) * 100 +
                cal.get(Calendar.DAY_OF_MONTH)
    }

    // ===================================
    // CHECK DAILY RESET
    // ===================================
    fun checkDailyReset() {
        viewModelScope.launch {
            val lastDate = userPreferences.lastDateFlow.first()
            val currentDate = getCurrentDateInt()

            if (lastDate != currentDate) {
                // Determine yesterday's custom habits success
                val habits = customHabits.value
                if (habits.isNotEmpty()) {
                    val allCompleted = habits.all { it.completed }
                    if (!allCompleted) {
                        // Reset streak if they failed to finish all tasks yesterday
                        userPreferences.saveStreak(0)
                    }
                }

                // Daily Reset Operations
                userPreferences.saveWaterIntake(0)
                userPreferences.saveDailySteps(0)
                userPreferences.saveStepSensorBaseline(-1)

                // Happiness Decays slightly on new day (-15)
                val newHappiness = maxOf(20, petHappiness.value - 15)
                userPreferences.savePetHappiness(newHappiness)

                // Reset custom habits completion status for the new day
                val resetHabits = habits.map { it.copy(completed = false) }
                userPreferences.saveHabitsJson(serializeHabits(resetHabits))

                userPreferences.saveLastDate(currentDate)
                syncToFirestore()
            }
        }
    }

    // ===================================
    // HARDWARE SENSOR BINDING
    // ===================================
    fun onSensorStepCountChanged(totalStepsSinceReboot: Int) {
        viewModelScope.launch {
            val baseline = userPreferences.stepSensorBaselineFlow.first()
            val currentDate = getCurrentDateInt()

            if (baseline == -1 || totalStepsSinceReboot < baseline) {
                userPreferences.saveStepSensorBaseline(totalStepsSinceReboot)
                userPreferences.saveDailySteps(0)
            } else {
                val computedTodaySteps = totalStepsSinceReboot - baseline
                userPreferences.saveDailySteps(computedTodaySteps)
                
                val goal = userPreferences.stepGoalFlow.first()
                val goalCompleted = userPreferences.goalCompletedFlow.first()
                if (computedTodaySteps >= goal && goalCompleted != currentDate) {
                    userPreferences.saveGoalCompleted(currentDate)
                    userPreferences.saveCoins(coins.value + 15)
                }
            }
            syncToFirestore()
        }
    }

    // ===================================
    // MANUAL TRACKING / EMULATOR OVERRIDES
    // ===================================
    fun mockAddSteps(amount: Int) {
        viewModelScope.launch {
            val currentStepsVal = dailySteps.value
            val targetSteps = currentStepsVal + amount
            userPreferences.saveDailySteps(targetSteps)

            val goal = userPreferences.stepGoalFlow.first()
            val goalCompleted = userPreferences.goalCompletedFlow.first()
            val currentDate = getCurrentDateInt()
            if (targetSteps >= goal && goalCompleted != currentDate) {
                userPreferences.saveGoalCompleted(currentDate)
                userPreferences.saveCoins(coins.value + 15)
            }
            syncToFirestore()
        }
    }

    fun incrementWater() {
        viewModelScope.launch {
            val nextWater = waterIntake.value + 1
            userPreferences.saveWaterIntake(nextWater)
            
            // Check if there's any habit named "water" and auto-complete it!
            val list = customHabits.value.toMutableList()
            var changed = false
            val currentWaterGoal = waterGoal.value
            for (i in 0 until list.size) {
                if (list[i].title.contains("water", ignoreCase = true) && !list[i].completed && nextWater >= currentWaterGoal) {
                    list[i] = list[i].copy(completed = true)
                    changed = true
                }
            }
            if (changed) {
                userPreferences.saveHabitsJson(serializeHabits(list))
                checkAndIncrementStreak()
            }
            syncToFirestore()
        }
    }

    fun updateStepGoal(goal: Int) {
        viewModelScope.launch {
            userPreferences.saveStepGoal(goal)
            syncToFirestore()
        }
    }

    fun updateWaterGoal(goal: Int) {
        viewModelScope.launch {
            userPreferences.saveWaterGoal(goal)
            syncToFirestore()
        }
    }

    fun updateWeight(weightVal: String) {
        viewModelScope.launch {
            userPreferences.saveWeight(weightVal.trim())
            syncToFirestore()
        }
    }

    fun updateHeight(heightVal: String) {
        viewModelScope.launch {
            userPreferences.saveHeight(heightVal.trim())
            syncToFirestore()
        }
    }

    fun updateCalorieGoal(goal: Int) {
        viewModelScope.launch {
            userPreferences.saveCalorieGoal(goal)
            syncToFirestore()
        }
    }

    private fun scheduleTaskReminder(taskId: String, taskTitle: String, reminderTime: Long) {
        val context = getApplication<Application>()
        val delay = reminderTime - System.currentTimeMillis()
        if (delay > 0) {
            val workData = androidx.work.workDataOf(
                "task_title" to taskTitle,
                "task_id" to taskId
            )
            val workRequest = androidx.work.OneTimeWorkRequestBuilder<com.example.fitnesspaw.notifications.TaskReminderWorker>()
                .setInitialDelay(delay, java.util.concurrent.TimeUnit.MILLISECONDS)
                .setInputData(workData)
                .build()
            androidx.work.WorkManager.getInstance(context).enqueueUniqueWork(
                "task_reminder_$taskId",
                androidx.work.ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
    }

    private fun cancelTaskReminder(taskId: String) {
        val context = getApplication<Application>()
        androidx.work.WorkManager.getInstance(context).cancelUniqueWork("task_reminder_$taskId")
    }

    // ===================================
    // DYNAMIC HABITS OPERATIONS
    // ===================================
    fun addHabit(title: String, reminderTime: Long? = null) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val id = UUID.randomUUID().toString()
            val currentList = customHabits.value.toMutableList()
            val newHabit = CustomHabit(
                id = id,
                title = title.trim(),
                completed = false,
                reminderTime = reminderTime
            )
            currentList.add(newHabit)
            userPreferences.saveHabitsJson(serializeHabits(currentList))
            if (reminderTime != null) {
                scheduleTaskReminder(id, title.trim(), reminderTime)
            }
            syncToFirestore()
        }
    }

    fun deleteHabit(id: String) {
        viewModelScope.launch {
            val currentList = customHabits.value.toMutableList()
            currentList.removeAll { it.id == id }
            userPreferences.saveHabitsJson(serializeHabits(currentList))
            cancelTaskReminder(id)
            syncToFirestore()
        }
    }

    fun editHabit(id: String, newTitle: String, reminderTime: Long? = null) {
        if (newTitle.isBlank()) return
        viewModelScope.launch {
            val currentList = customHabits.value.toMutableList()
            for (i in 0 until currentList.size) {
                if (currentList[i].id == id) {
                    currentList[i] = currentList[i].copy(title = newTitle.trim(), reminderTime = reminderTime)
                    if (reminderTime != null) {
                        scheduleTaskReminder(id, newTitle.trim(), reminderTime)
                    } else {
                        cancelTaskReminder(id)
                    }
                }
            }
            userPreferences.saveHabitsJson(serializeHabits(currentList))
            syncToFirestore()
        }
    }

    fun toggleCustomHabit(id: String) {
        viewModelScope.launch {
            val currentList = customHabits.value.toMutableList()
            for (i in 0 until currentList.size) {
                if (currentList[i].id == id) {
                    val targetHabit = currentList[i]
                    val nextCompleted = !targetHabit.completed
                    currentList[i] = targetHabit.copy(completed = nextCompleted)

                    if (nextCompleted) {
                        cancelTaskReminder(id)
                    } else {
                        targetHabit.reminderTime?.let { time ->
                            if (time > System.currentTimeMillis()) {
                                scheduleTaskReminder(id, targetHabit.title, time)
                            }
                        }
                    }

                    // If they just completed it and it contains "water", log water to waterGoal cups
                    if (nextCompleted && targetHabit.title.contains("water", ignoreCase = true)) {
                        userPreferences.saveWaterIntake(maxOf(waterIntake.value, waterGoal.value))
                    }
                }
            }
            userPreferences.saveHabitsJson(serializeHabits(currentList))
            checkAndIncrementStreak()
            syncToFirestore()
        }
    }

    // ===================================
    // PET FEEDING AND CUSTOMIZATION
    // ===================================
    fun feedPet(): Boolean {
        if (coins.value < 10) return false
        viewModelScope.launch {
            userPreferences.saveCoins(coins.value - 10)
            val currentHappiness = petHappiness.value
            val targetHappiness = minOf(100, currentHappiness + 15)
            userPreferences.savePetHappiness(targetHappiness)
            syncToFirestore()
        }
        return true
    }

    fun renamePet(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            userPreferences.savePetName(name.trim())
            syncToFirestore()
        }
    }

    fun changeSelectedPet(pet: Int) {
        viewModelScope.launch {
            userPreferences.saveSelectedPet(pet)
            syncToFirestore()
        }
    }

    fun saveProfile(name: String, pet: Int) {
        viewModelScope.launch {
            userPreferences.saveUsername(name)
            userPreferences.saveSelectedPet(pet)
            syncToFirestore()
        }
    }

    // ===================================
    // STREAK TRACKING LOGIC
    // ===================================
    private suspend fun checkAndIncrementStreak() {
        val list = customHabits.value
        if (list.isEmpty()) return

        val allCompleted = list.all { it.completed }
        if (allCompleted) {
            val currentDate = getCurrentDateInt()
            val lastStreakDate = userPreferences.lastStreakIncrementDateFlow.first()

            if (lastStreakDate != currentDate) {
                userPreferences.saveLastStreakIncrementDate(currentDate)
                userPreferences.saveStreak(streak.value + 1)
                userPreferences.saveCoins(coins.value + 20) // +20 coins streak reward!
            }
        }
    }

    // ===================================
    // JSON SERIALIZATION HELPER
    // ===================================
    private fun serializeHabits(habits: List<CustomHabit>): String {
        val array = JSONArray()
        habits.forEach { habit ->
            val obj = JSONObject()
            obj.put("id", habit.id)
            obj.put("title", habit.title)
            obj.put("completed", habit.completed)
            habit.reminderTime?.let { obj.put("reminderTime", it) }
            array.put(obj)
        }
        return array.toString()
    }

    private fun deserializeHabits(json: String): List<CustomHabit> {
        if (json.isBlank()) return emptyList()
        val list = mutableListOf<CustomHabit>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val reminderTime = if (obj.has("reminderTime")) obj.getLong("reminderTime") else null
                list.add(
                    CustomHabit(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        completed = obj.getBoolean("completed"),
                        reminderTime = reminderTime
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    // ===================================
    // FIRESTORE SYNCHRONIZATION
    // ===================================
    fun syncToFirestore() {
        if (FirebaseAuth.getInstance().currentUser == null) return
        viewModelScope.launch {
            val habitsJson = userPreferences.habitsJsonFlow.first()
            firestoreManager.saveUserData(
                username = username.value,
                coins = coins.value,
                streak = streak.value,
                selectedPet = selectedPet.value,
                waterIntake = waterIntake.value,
                dailySteps = dailySteps.value,
                stepGoal = stepGoal.value,
                waterGoal = waterGoal.value,
                habitsJson = habitsJson,
                petName = petName.value,
                petHappiness = petHappiness.value,
                weight = weight.value,
                height = height.value,
                calorieGoal = calorieGoal.value
            )
        }
    }

    fun loadProfileFromFirestore(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val data = firestoreManager.loadUserData()
            if (data != null) {
                val fbUsername = data["username"] as? String ?: ""
                val fbCoins = (data["coins"] as? Long)?.toInt() ?: 0
                val fbStreak = (data["streak"] as? Long)?.toInt() ?: 0
                val fbSelectedPet = (data["selectedPet"] as? Long)?.toInt() ?: 0
                val fbWaterIntake = (data["waterIntake"] as? Long)?.toInt() ?: 0
                val fbDailySteps = (data["dailySteps"] as? Long)?.toInt() ?: 0
                val fbStepGoal = (data["stepGoal"] as? Long)?.toInt() ?: 7000
                val fbWaterGoal = (data["waterGoal"] as? Long)?.toInt() ?: 8
                val fbHabitsJson = data["habitsJson"] as? String ?: ""
                val fbPetName = data["petName"] as? String ?: "Buddy"
                val fbPetHappiness = (data["petHappiness"] as? Long)?.toInt() ?: 80
                val fbWeight = data["weight"] as? String ?: ""
                val fbHeight = data["height"] as? String ?: ""
                val fbCalorieGoal = (data["calorieGoal"] as? Long)?.toInt() ?: 500

                userPreferences.saveUsername(fbUsername)
                userPreferences.saveCoins(fbCoins)
                userPreferences.saveStreak(fbStreak)
                userPreferences.saveSelectedPet(fbSelectedPet)
                userPreferences.saveWaterIntake(fbWaterIntake)
                userPreferences.saveDailySteps(fbDailySteps)
                userPreferences.saveStepGoal(fbStepGoal)
                userPreferences.saveWaterGoal(fbWaterGoal)
                userPreferences.saveHabitsJson(fbHabitsJson)
                userPreferences.savePetName(fbPetName)
                userPreferences.savePetHappiness(fbPetHappiness)
                userPreferences.saveWeight(fbWeight)
                userPreferences.saveHeight(fbHeight)
                userPreferences.saveCalorieGoal(fbCalorieGoal)
            }
            onComplete()
        }
    }

    fun clearLocalData() {
        viewModelScope.launch {
            userPreferences.saveUsername("")
            userPreferences.saveCoins(0)
            userPreferences.saveStreak(0)
            userPreferences.saveSelectedPet(0)
            userPreferences.saveWaterIntake(0)
            userPreferences.saveDailySteps(0)
            userPreferences.saveStepGoal(7000)
            userPreferences.saveWaterGoal(8)
            userPreferences.saveStepSensorBaseline(-1)
            userPreferences.saveHabitsJson("")
            userPreferences.savePetName("Buddy")
            userPreferences.savePetHappiness(80)
            userPreferences.saveThemeMode(2) // Reset to System theme
            userPreferences.saveWeight("")
            userPreferences.saveHeight("")
            userPreferences.saveCalorieGoal(500)
        }
    }
}
