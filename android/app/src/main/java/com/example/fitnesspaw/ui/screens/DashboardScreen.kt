package com.example.fitnesspaw.ui.screens

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.fitnesspaw.data.CustomHabit
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fitnesspaw.R
import com.example.fitnesspaw.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private fun formatDateTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())
    return sdf.format(timestamp)
}

private fun showDateTimePicker(context: android.content.Context, onDateTimeSelected: (Long) -> Unit) {
    val calendar = Calendar.getInstance()
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val timeCalendar = Calendar.getInstance()
            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    val selectedCalendar = Calendar.getInstance().apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month)
                        set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        set(Calendar.HOUR_OF_DAY, hourOfDay)
                        set(Calendar.MINUTE, minute)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    onDateTimeSelected(selectedCalendar.timeInMillis)
                },
                timeCalendar.get(Calendar.HOUR_OF_DAY),
                timeCalendar.get(Calendar.MINUTE),
                false
            ).show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val context = LocalContext.current

    // Observe state from ViewModel
    val username by viewModel.username.collectAsStateWithLifecycle()
    val coins by viewModel.coins.collectAsStateWithLifecycle()
    val streak by viewModel.streak.collectAsStateWithLifecycle()
    val selectedPet by viewModel.selectedPet.collectAsStateWithLifecycle()
    val waterIntake by viewModel.waterIntake.collectAsStateWithLifecycle()
    val waterGoal by viewModel.waterGoal.collectAsStateWithLifecycle()
    val dailySteps by viewModel.dailySteps.collectAsStateWithLifecycle()
    val stepGoal by viewModel.stepGoal.collectAsStateWithLifecycle()
    val petName by viewModel.petName.collectAsStateWithLifecycle()
    val petHappiness by viewModel.petHappiness.collectAsStateWithLifecycle()
    val isSensorSupported by viewModel.isSensorSupported.collectAsStateWithLifecycle()

    // Observe dynamic custom habits
    val customHabits by viewModel.customHabits.collectAsStateWithLifecycle()

    // Local input state for adding dynamic habits
    var habitInputText by remember { mutableStateOf("") }
    var habitToEdit by remember { mutableStateOf<CustomHabit?>(null) }
    var editHabitText by remember { mutableStateOf("") }
    var reminderTimestamp by remember { mutableStateOf<Long?>(null) }

    // Bottom Sheet visibility states
    var showStepsSheet by remember { mutableStateOf(false) }
    var showWaterSheet by remember { mutableStateOf(false) }
    var showStreakSheet by remember { mutableStateOf(false) }
    var showCalorieSheet by remember { mutableStateOf(false) }

    val caloriesBurned by viewModel.caloriesBurned.collectAsStateWithLifecycle()
    val calorieGoal by viewModel.calorieGoal.collectAsStateWithLifecycle()

    // Permission checks
    var hasActivityPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            hasActivityPermission = permissions[Manifest.permission.ACTIVITY_RECOGNITION] ?: hasActivityPermission
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            hasNotificationPermission = permissions[Manifest.permission.POST_NOTIFICATIONS] ?: hasNotificationPermission
        }
    }

    // Dynamic Pet Mood Logic from dynamic habits completed count
    val completedHabitsCount = customHabits.count { it.completed }
    val totalHabitsCount = customHabits.size
    val progressTarget = if (totalHabitsCount == 0) 1.0f else completedHabitsCount.toFloat() / totalHabitsCount
    val animatedProgress by animateFloatAsState(
        targetValue = progressTarget,
        animationSpec = tween(durationMillis = 800),
        label = "PetProgressRing"
    )

    val (petMoodText, petImageRes) = when (selectedPet) {
        0 -> { // Cat
            when {
                totalHabitsCount > 0 && completedHabitsCount == 0 -> "Sad 😢" to R.drawable.cat_sad
                totalHabitsCount > 0 && completedHabitsCount < totalHabitsCount -> "Resting 😴" to R.drawable.cat_sleepy
                else -> "Happy! ✨" to R.drawable.cat_happy
            }
        }
        1 -> { // Dog
            when {
                totalHabitsCount > 0 && completedHabitsCount == 0 -> "Sad 😢" to R.drawable.dog_sad
                totalHabitsCount > 0 && completedHabitsCount < totalHabitsCount -> "Resting 😴" to R.drawable.dog_sleepy
                else -> "Happy! ✨" to R.drawable.dog_happy
            }
        }
        else -> { // Panda
            when {
                totalHabitsCount > 0 && completedHabitsCount == 0 -> "Sad 😢" to R.drawable.panda_sad
                totalHabitsCount > 0 && completedHabitsCount < totalHabitsCount -> "Resting 😴" to R.drawable.panda_sleepy
                else -> "Happy! ✨" to R.drawable.panda_happy
            }
        }
    }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
            MaterialTheme.colorScheme.surface
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(48.dp))

                // TOP GREETING
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Good Morning 👋",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (username.isNotBlank()) username else "Sai Mohith",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // COINS DISPLAY
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🪙", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = coins.toString(),
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            // PERMISSIONS ERROR CARD
            if (!hasActivityPermission || !hasNotificationPermission) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Permission Access Required ⚠️",
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Enable steps and notifications tracking to unlock full capabilities.",
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    val reqs = mutableListOf<String>()
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                        reqs.add(Manifest.permission.ACTIVITY_RECOGNITION)
                                    }
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        reqs.add(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                    permissionLauncher.launch(reqs.toTypedArray())
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text("Grant Access", color = Color.White, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            // HERO CARD (Virtual Pet display with Circular Progress)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp, horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(200.dp)
                        ) {
                            // Circular Canvas Ring
                            val accentColor = MaterialTheme.colorScheme.primary
                            val trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCircle(
                                    color = trackColor,
                                    radius = size.minDimension / 2 - 12.dp.toPx(),
                                    style = Stroke(width = 10.dp.toPx())
                                )
                                drawArc(
                                    color = accentColor,
                                    startAngle = -90f,
                                    sweepAngle = animatedProgress * 360f,
                                    useCenter = false,
                                    style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }

                            // Pet Image Frame
                            Image(
                                painter = painterResource(id = petImageRes),
                                contentDescription = "Virtual Pet Companion",
                                modifier = Modifier
                                    .size(140.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Pet Mood State text
                        Text(
                            text = "$petName is $petMoodText",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Completion text description
                        Text(
                            text = if (totalHabitsCount == 0) {
                                "No active habits. Create one below! 🐾"
                            } else {
                                "${completedHabitsCount} of ${totalHabitsCount} habits completed today"
                            },
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            fontSize = 15.sp
                        )
                    }
                }
            }

            // STATS TILES ROW
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DashboardStatCard(
                            title = "Steps",
                            value = "${dailySteps}/${stepGoal}",
                            icon = Icons.Default.DirectionsWalk,
                            modifier = Modifier.weight(1f),
                            onClick = { showStepsSheet = true }
                        )

                        DashboardStatCard(
                            title = "Water",
                            value = "${waterIntake}/${waterGoal} Cups",
                            icon = Icons.Default.WaterDrop,
                            modifier = Modifier.weight(1f),
                            onClick = { showWaterSheet = true }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DashboardStatCard(
                            title = "Streak",
                            value = "$streak Days",
                            icon = Icons.Default.LocalFireDepartment,
                            modifier = Modifier.weight(1f),
                            onClick = { showStreakSheet = true }
                        )

                        DashboardStatCard(
                            title = "Calories",
                            value = "${caloriesBurned}/${calorieGoal} kcal",
                            icon = Icons.Default.LocalFireDepartment,
                            modifier = Modifier.weight(1f),
                            onClick = { showCalorieSheet = true }
                        )
                    }
                }
            }

            // HABITS CHECKLIST ROW
            item {
                Text(
                    text = "Habit Tasks 🐾",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            // DYNAMIC DOCKED HABIT ITEMS LIST
            if (customHabits.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                    ) {
                        Text(
                            text = "No custom habits logged. Create your daily tasks using the input fields below!",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                }
            } else {
                items(customHabits, key = { it.id }) { habit ->
                    DynamicHabitItemCard(
                        title = habit.title,
                        completed = habit.completed,
                        reminderTime = habit.reminderTime,
                        onToggle = { viewModel.toggleCustomHabit(habit.id) },
                        onEdit = {
                            habitToEdit = habit
                            editHabitText = habit.title
                        },
                        onDelete = {
                            viewModel.deleteHabit(habit.id)
                            Toast.makeText(context, "Habit deleted!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            // ADD HABIT INPUT TEXTFIELD DOCK
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    OutlinedTextField(
                        value = habitInputText,
                        onValueChange = { habitInputText = it },
                        label = { Text("Log custom daily habit...") },
                        placeholder = { Text("e.g. Read 10 Pages") },
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    if (habitInputText.isNotBlank()) {
                                        viewModel.addHabit(habitInputText, reminderTimestamp)
                                        habitInputText = ""
                                        reminderTimestamp = null
                                        Toast.makeText(context, "Custom habit logged!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add custom task",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (reminderTimestamp == null) {
                            TextButton(
                                onClick = {
                                    showDateTimePicker(context) { selectedTime ->
                                        reminderTimestamp = selectedTime
                                    }
                                }
                            ) {
                                Text("⏰ Set Reminder Notification", fontSize = 13.sp)
                            }
                        } else {
                            InputChip(
                                selected = true,
                                onClick = {
                                    showDateTimePicker(context) { selectedTime ->
                                        reminderTimestamp = selectedTime
                                    }
                                },
                                label = { Text("⏰ " + formatDateTime(reminderTimestamp!!)) },
                                trailingIcon = {
                                    IconButton(
                                        onClick = { reminderTimestamp = null },
                                        modifier = Modifier.size(16.dp)
                                    ) {
                                        Text("×", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(48.dp))
            }
        }

        // ==========================================
        // STEPS BOTTOM SHEET
        // ==========================================
        if (showStepsSheet) {
            ModalBottomSheet(
                onDismissRequest = { showStepsSheet = false },
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Daily Steps Tracker 👣",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Total Steps Today: $dailySteps",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { dailySteps.toFloat() / stepGoal.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${((dailySteps.toFloat() / stepGoal.toFloat()) * 100).toInt()}% towards goal",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Adjust Daily Step Goal 🎯",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        IconButton(
                            onClick = { if (stepGoal > 1000) viewModel.updateStepGoal(stepGoal - 1000) }
                        ) {
                            Text("-", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        
                        Text(
                            text = String.format("%,d steps", stepGoal),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        IconButton(
                            onClick = { if (stepGoal < 50000) viewModel.updateStepGoal(stepGoal + 1000) }
                        ) {
                            Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (isSensorSupported) "Sensor Status: Active ✅" else "Sensor Status: Emulator / Fallback ⚠️",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSensorSupported) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Use the simulation triggers below to mock walk steps for evaluation.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = { viewModel.mockAddSteps(500) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("+500 Steps")
                        }

                        Button(
                            onClick = { viewModel.mockAddSteps(2000) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("+2000 Steps")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // ==========================================
        // WATER BOTTOM SHEET
        // ==========================================
        if (showWaterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showWaterSheet = false },
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Water Intake 💧",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your pet needs water to stay happy and healthy! Goal is $waterGoal cups.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    androidx.compose.foundation.layout.FlowRow(
                        horizontalArrangement = Arrangement.Center,
                        maxItemsInEachRow = 8,
                        modifier = Modifier.padding(bottom = 20.dp)
                    ) {
                        for (i in 1..waterGoal) {
                            val isLogged = waterIntake >= i
                            Card(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(36.dp)
                                    .clip(CircleShape),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isLogged) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                                )
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "💧",
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = "Logged: $waterIntake / $waterGoal cups",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Adjust Daily Water Goal 🎯",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        IconButton(
                            onClick = { if (waterGoal > 2) viewModel.updateWaterGoal(waterGoal - 1) }
                        ) {
                            Text("-", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        
                        Text(
                            text = "$waterGoal cups",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        IconButton(
                            onClick = { if (waterGoal < 24) viewModel.updateWaterGoal(waterGoal + 1) }
                        ) {
                            Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.incrementWater() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("💧 Log 1 Cup", fontSize = 16.sp, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // ==========================================
        // STREAK BOTTOM SHEET
        // ==========================================
        if (showStreakSheet) {
            ModalBottomSheet(
                onDismissRequest = { showStreakSheet = false },
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Activity Streak 🔥",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Card(
                        modifier = Modifier.size(100.dp),
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🔥", fontSize = 48.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "$streak Day Streak",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Check off all dynamic habits daily to keep your streak burning! $petName depends on you.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        // ==========================================
        // CALORIES BOTTOM SHEET
        // ==========================================
        if (showCalorieSheet) {
            ModalBottomSheet(
                onDismissRequest = { showCalorieSheet = false },
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Calories Burned Tracker 🏆",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Total Calories Burned Today: $caloriesBurned kcal",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { minOf(1f, caloriesBurned.toFloat() / maxOf(1f, calorieGoal.toFloat())) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val percent = if (calorieGoal > 0) ((caloriesBurned.toFloat() / calorieGoal.toFloat()) * 100).toInt() else 0
                    Text(
                        text = "$percent% towards calorie goal",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Adjust Daily Calorie Goal 🎯",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        IconButton(
                            onClick = { if (calorieGoal > 100) viewModel.updateCalorieGoal(calorieGoal - 50) }
                        ) {
                            Text("-", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }

                        Text(
                            text = "$calorieGoal kcal",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        IconButton(
                            onClick = { if (calorieGoal < 10000) viewModel.updateCalorieGoal(calorieGoal + 50) }
                        ) {
                            Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // Edit Habit Dialog
        if (habitToEdit != null) {
            var editReminderTime by remember(habitToEdit) { mutableStateOf(habitToEdit?.reminderTime) }
            AlertDialog(
                onDismissRequest = { habitToEdit = null },
                title = { Text("Edit Habit Title & Reminder 🐾") },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = editHabitText,
                            onValueChange = { editHabitText = it },
                            label = { Text("Habit name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (editReminderTime == null) {
                                TextButton(
                                    onClick = {
                                        showDateTimePicker(context) { selectedTime ->
                                            editReminderTime = selectedTime
                                        }
                                    }
                                ) {
                                    Text("⏰ Set Reminder Notification", fontSize = 13.sp)
                                }
                            } else {
                                InputChip(
                                    selected = true,
                                    onClick = {
                                        showDateTimePicker(context) { selectedTime ->
                                            editReminderTime = selectedTime
                                        }
                                    },
                                    label = { Text("⏰ " + formatDateTime(editReminderTime!!)) },
                                    trailingIcon = {
                                        IconButton(
                                            onClick = { editReminderTime = null },
                                            modifier = Modifier.size(16.dp)
                                        ) {
                                            Text("×", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val habitId = habitToEdit?.id
                            if (habitId != null && editHabitText.isNotBlank()) {
                                viewModel.editHabit(habitId, editHabitText, editReminderTime)
                                habitToEdit = null
                                Toast.makeText(context, "Habit updated!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { habitToEdit = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun DynamicHabitItemCard(
    title: String,
    completed: Boolean,
    reminderTime: Long? = null,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onToggle,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (completed) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (completed) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.08f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Surface(
                modifier = Modifier.size(28.dp),
                shape = CircleShape,
                color = if (completed) MaterialTheme.colorScheme.primary else Color.Transparent,
                border = BorderStroke(
                    width = 2.dp,
                    color = if (completed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (completed) {
                        Text("✓", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.width(18.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                if (reminderTime != null) {
                    Text(
                        text = "⏰ " + formatDateTime(reminderTime),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            // Inline Edit Button
            IconButton(
                onClick = onEdit
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Habit",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            // Inline Delete Button
            IconButton(
                onClick = onDelete
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete Habit",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun DashboardStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(105.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = title,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
        }
    }
}