package com.example.fitnesspaw.data

data class CustomHabit(
    val id: String,
    val title: String,
    val completed: Boolean = false,
    val reminderTime: Long? = null
)
