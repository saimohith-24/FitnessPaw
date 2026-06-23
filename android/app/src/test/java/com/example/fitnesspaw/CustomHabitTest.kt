package com.example.fitnesspaw

import com.example.fitnesspaw.data.CustomHabit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CustomHabitTest {

    @Test
    fun testCustomHabit_initializationWithDefaults() {
        val habit = CustomHabit(
            id = "test-id-123",
            title = "Drink Water"
        )

        assertEquals("test-id-123", habit.id)
        assertEquals("Drink Water", habit.title)
        assertFalse(habit.completed)
        assertNull(habit.reminderTime)
    }

    @Test
    fun testCustomHabit_initializationWithCustomValues() {
        val habit = CustomHabit(
            id = "test-id-456",
            title = "Morning Walk",
            completed = true,
            reminderTime = 1719114000000L
        )

        assertEquals("test-id-456", habit.id)
        assertEquals("Morning Walk", habit.title)
        assertTrue(habit.completed)
        assertEquals(1719114000000L, habit.reminderTime)
    }

    @Test
    fun testCustomHabit_copyHelperMethod() {
        val original = CustomHabit(
            id = "id-789",
            title = "Sleep early"
        )

        val updated = original.copy(completed = true)

        assertEquals(original.id, updated.id)
        assertEquals(original.title, updated.title)
        assertFalse(original.completed)
        assertTrue(updated.completed)
    }
}
