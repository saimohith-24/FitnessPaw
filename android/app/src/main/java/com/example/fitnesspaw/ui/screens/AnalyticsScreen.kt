package com.example.fitnesspaw.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fitnesspaw.ui.viewmodel.MainViewModel
import java.util.Calendar

@Composable
fun AnalyticsScreen(viewModel: MainViewModel) {
    // Observe state
    val streak by viewModel.streak.collectAsStateWithLifecycle()
    val dailySteps by viewModel.dailySteps.collectAsStateWithLifecycle()
    val waterIntake by viewModel.waterIntake.collectAsStateWithLifecycle()
    val waterGoal by viewModel.waterGoal.collectAsStateWithLifecycle()
    val coins by viewModel.coins.collectAsStateWithLifecycle()

    val customHabits by viewModel.customHabits.collectAsStateWithLifecycle()

    // Calculate today's completion percentage
    val totalHabitsCount = customHabits.size
    val completedHabitsCount = customHabits.count { it.completed }
    val todayProgress = if (totalHabitsCount == 0) 0f else completedHabitsCount.toFloat() / totalHabitsCount

    // Current Day of Week (1 = Sunday, 2 = Monday, ..., 7 = Saturday)
    val dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

    val todayWeekPos = when (dayOfWeek) {
        Calendar.MONDAY -> 0
        Calendar.TUESDAY -> 1
        Calendar.WEDNESDAY -> 2
        Calendar.THURSDAY -> 3
        Calendar.FRIDAY -> 4
        Calendar.SATURDAY -> 5
        Calendar.SUNDAY -> 6
        else -> 0
    }

    val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    // Dynamically map weekly progress based on active streak and current day's progress
    val resolvedDays = remember(todayProgress, streak) {
        dayNames.mapIndexed { i, name ->
            val isToday = i == todayWeekPos
            val progress = when {
                isToday -> todayProgress
                i < todayWeekPos -> {
                    val daysAgo = todayWeekPos - i
                    if (streak > 0 && daysAgo <= streak) 1f else 0f
                }
                else -> 0f
            }
            WeeklyDayData(name, progress, isToday)
        }
    }

    // Average weekly completion
    val avgCompletion = resolvedDays.map { it.progress }.average().toFloat()

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

                Text(
                    text = "Analytics 📊",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // WEEKLY PROGRESS CUSTOM CHART
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "Weekly Habit Success",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(30.dp))

                        // Custom Weekly Bar Chart
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            resolvedDays.forEach { data ->
                                WeeklyBarItem(data)
                            }
                        }
                    }
                }
            }

            // SUMMARY DETAILS GRID
            item {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Performance Metrics",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        MetricDetailsCard(
                            title = "Weekly Success",
                            value = "${(avgCompletion * 100).toInt()}%",
                            description = "Avg habit completion",
                            modifier = Modifier.weight(1f)
                        )

                        MetricDetailsCard(
                            title = "Streak Heat",
                            value = "$streak Days",
                            description = "Consecutive active days",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        MetricDetailsCard(
                            title = "Hydration Stats",
                            value = "$waterIntake / $waterGoal",
                            description = "Cups logged today",
                            modifier = Modifier.weight(1f)
                        )

                        MetricDetailsCard(
                            title = "Steps Counter",
                            value = dailySteps.toString(),
                            description = "Total steps walked today",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val caloriesBurned by viewModel.caloriesBurned.collectAsStateWithLifecycle()
                        MetricDetailsCard(
                            title = "Calories Burned",
                            value = "$caloriesBurned kcal",
                            description = "Based on steps/weight",
                            modifier = Modifier.weight(1f)
                        )

                        MetricDetailsCard(
                            title = "Earned Tokens",
                            value = "🪙 $coins",
                            description = "Total paw tokens collected",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(48.dp))
                }
            }
        }
    }
}

// Data class to coordinate weekly day representation
data class WeeklyDayData(
    val dayName: String,
    val progress: Float,
    val isToday: Boolean
)

private fun WeeklyDayClass(isToday: Boolean, todayProgress: Float): WeeklyDayData {
    val cal = Calendar.getInstance()
    val dayName = if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) "Fri" else "Sun"
    return WeeklyDayData(dayName, todayProgress, isToday)
}

@Composable
fun WeeklyBarItem(data: WeeklyDayData) {
    // Height Animation for bars
    var startAnim by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (startAnim) data.progress else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "BarHeightAnim"
    )

    LaunchedEffect(Unit) {
        startAnim = true
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(36.dp)
    ) {
        // Percentage tooltip inside bar or above
        Text(
            text = "${(data.progress * 100).toInt()}%",
            color = if (data.isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            fontSize = 11.sp,
            fontWeight = if (data.isToday) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // Bar container
        Box(
            modifier = Modifier
                .width(14.dp)
                .height(100.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(animatedProgress)
                    .background(
                        if (data.isToday) {
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                )
                            )
                        } else {
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
                                )
                            )
                        }
                    )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Day Label
        Text(
            text = data.dayName,
            color = if (data.isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            fontSize = 13.sp,
            fontWeight = if (data.isToday) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun MetricDetailsCard(
    title: String,
    value: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = description,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
        }
    }
}