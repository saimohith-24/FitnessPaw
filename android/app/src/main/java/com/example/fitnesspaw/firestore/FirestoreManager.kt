package com.example.fitnesspaw.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreManager {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // =========================
    // SAVE USER DATA
    // =========================
    fun saveUserData(
        username: String,
        coins: Int,
        streak: Int,
        selectedPet: Int,
        waterIntake: Int,
        dailySteps: Int,
        stepGoal: Int,
        waterGoal: Int,
        habitsJson: String,
        petName: String,
        petHappiness: Int,
        weight: String,
        height: String,
        calorieGoal: Int
    ) {
        val userId = auth.currentUser?.uid ?: return

        val data = hashMapOf(
            "username" to username,
            "coins" to coins,
            "streak" to streak,
            "selectedPet" to selectedPet,
            "waterIntake" to waterIntake,
            "dailySteps" to dailySteps,
            "stepGoal" to stepGoal,
            "waterGoal" to waterGoal,
            "habitsJson" to habitsJson,
            "petName" to petName,
            "petHappiness" to petHappiness,
            "weight" to weight,
            "height" to height,
            "calorieGoal" to calorieGoal,
            "lastUpdated" to com.google.firebase.Timestamp.now()
        )

        firestore
            .collection("users")
            .document(userId)
            .set(data)
    }

    // =========================
    // LOAD USER DATA
    // =========================
    suspend fun loadUserData(): Map<String, Any>? {
        val userId = auth.currentUser?.uid ?: return null

        return try {
            val snapshot = firestore
                .collection("users")
                .document(userId)
                .get()
                .await()

            snapshot.data
        } catch (e: Exception) {
            null
        }
    }
}