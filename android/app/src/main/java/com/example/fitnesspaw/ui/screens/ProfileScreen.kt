package com.example.fitnesspaw.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fitnesspaw.ui.theme.ThemeViewModel
import com.example.fitnesspaw.ui.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(
    themeViewModel: ThemeViewModel,
    mainViewModel: MainViewModel,
    onLogout: () -> Unit
) {
    val context = LocalContext.current

    // Observe values
    val username by mainViewModel.username.collectAsStateWithLifecycle()
    val selectedPet by mainViewModel.selectedPet.collectAsStateWithLifecycle()
    val streak by mainViewModel.streak.collectAsStateWithLifecycle()
    val waterIntake by mainViewModel.waterIntake.collectAsStateWithLifecycle()
    val waterGoal by mainViewModel.waterGoal.collectAsStateWithLifecycle()
    val dailySteps by mainViewModel.dailySteps.collectAsStateWithLifecycle()
    val stepGoal by mainViewModel.stepGoal.collectAsStateWithLifecycle()
    val petName by mainViewModel.petName.collectAsStateWithLifecycle()

    val weight by mainViewModel.weight.collectAsStateWithLifecycle()
    val height by mainViewModel.height.collectAsStateWithLifecycle()
    val calorieGoal by mainViewModel.calorieGoal.collectAsStateWithLifecycle()

    // Dialog state
    var showEditDetailsDialog by remember { mutableStateOf(false) }
    var editUsernameText by remember { mutableStateOf("") }
    var editWeightText by remember { mutableStateOf("") }
    var editHeightText by remember { mutableStateOf("") }

    val customHabits by mainViewModel.customHabits.collectAsStateWithLifecycle()
    val completedHabitsCount = customHabits.count { it.completed }
    val totalHabitsCount = customHabits.size

    val currentUser = FirebaseAuth.getInstance().currentUser
    val userEmail = currentUser?.email ?: "user@fitnesspaw.com"
    val userUid = currentUser?.uid ?: "Unknown UID"
    val isVerified = currentUser?.isEmailVerified == true

    val currentThemeMode = themeViewModel.themeMode.intValue

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // PROFILE AVATAR Frame
            Surface(
                modifier = Modifier.size(110.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = when (selectedPet) {
                            0 -> "🐱"
                            1 -> "🐶"
                            else -> "🐼"
                        },
                        fontSize = 52.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // USERNAME & SUBTITLE
            Text(
                text = if (username.isNotBlank()) username else "Sai Mohith",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = userEmail,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // STATS WIDGET SUMMARY CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    ProfileStatItem("🔥 Current Streak", "$streak Days")
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 12.dp))
                    ProfileStatItem("💧 Water Logged Today", "$waterIntake / $waterGoal Cups")
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 12.dp))
                    ProfileStatItem("👣 Steps Walked Today", dailySteps.toString())
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 12.dp))
                    ProfileStatItem("🏆 Daily Habits Completed", "$completedHabitsCount / $totalHabitsCount")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // USER DETAILS CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "User Details 👤",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                        )
                        IconButton(
                            onClick = {
                                editUsernameText = username
                                editWeightText = weight
                                editHeightText = height
                                showEditDetailsDialog = true
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Details",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    ProfileStatItem("Height", if (height.isNotBlank()) "$height cm" else "Not set")
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 12.dp))
                    ProfileStatItem("Weight", if (weight.isNotBlank()) "$weight kg" else "Not set")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // DAILY TARGET GOALS CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Daily Target Goals 🎯",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Step Goal Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Steps Goal", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                            Text("Recommended: 5,000 - 15,000", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = { if (stepGoal > 1000) mainViewModel.updateStepGoal(stepGoal - 1000) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            Text(
                                text = String.format("%,d", stepGoal),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.widthIn(min = 60.dp),
                                textAlign = TextAlign.Center
                            )
                            IconButton(
                                onClick = { if (stepGoal < 50000) mainViewModel.updateStepGoal(stepGoal + 1000) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 12.dp))

                    // Water Goal Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Water Goal", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                            Text("Recommended: 4 - 16 Cups", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = { if (waterGoal > 2) mainViewModel.updateWaterGoal(waterGoal - 1) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            Text(
                                text = "$waterGoal Cups",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.widthIn(min = 60.dp),
                                textAlign = TextAlign.Center
                            )
                            IconButton(
                                onClick = { if (waterGoal < 24) mainViewModel.updateWaterGoal(waterGoal + 1) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 12.dp))

                    // Calorie Goal Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Calorie Burn Goal", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                            Text("Recommended: 200 - 2,000 kcal", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = { if (calorieGoal > 100) mainViewModel.updateCalorieGoal(calorieGoal - 50) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            Text(
                                text = "$calorieGoal kcal",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.widthIn(min = 60.dp),
                                textAlign = TextAlign.Center
                            )
                            IconButton(
                                onClick = { if (calorieGoal < 10000) mainViewModel.updateCalorieGoal(calorieGoal + 50) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // FIREBASE CONSOLE INTEGRATION DETAILS CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Firebase Console Link",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Status:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                        Text("Connected ✅", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("User UID:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                        Text(
                            text = userUid,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.width(180.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Email Status:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                        Text(
                            text = if (isVerified) "Verified Email" else "Pending Verification",
                            fontSize = 13.sp,
                            color = if (isVerified) MaterialTheme.colorScheme.primary else Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // PERSISTENT THEME SWITCHER (Light, System, Dark)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "App Theme Settings",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Light Mode
                        ThemeModeItem(
                            title = "Light",
                            isSelected = currentThemeMode == 0,
                            onClick = {
                                themeViewModel.setThemeMode(0)
                                Toast.makeText(context, "Theme set to Light", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f)
                        )

                        // System Default Mode
                        ThemeModeItem(
                            title = "System",
                            isSelected = currentThemeMode == 2,
                            onClick = {
                                themeViewModel.setThemeMode(2)
                                Toast.makeText(context, "Theme set to System Default", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f)
                        )

                        // Dark Mode
                        ThemeModeItem(
                            title = "Dark",
                            isSelected = currentThemeMode == 1,
                            onClick = {
                                themeViewModel.setThemeMode(1)
                                Toast.makeText(context, "Theme set to Dark", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // SIGN OUT BUTTON
            Button(
                onClick = { onLogout() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Logout",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sign Out",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Edit details dialog
            if (showEditDetailsDialog) {
                AlertDialog(
                    onDismissRequest = { showEditDetailsDialog = false },
                    title = { Text("Edit User Details 👤", fontWeight = FontWeight.Bold) },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = editUsernameText,
                                onValueChange = { editUsernameText = it },
                                label = { Text("Username") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = editHeightText,
                                onValueChange = { editHeightText = it },
                                label = { Text("Height (cm)") },
                                singleLine = true,
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = editWeightText,
                                onValueChange = { editWeightText = it },
                                label = { Text("Weight (kg)") },
                                singleLine = true,
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                mainViewModel.saveProfile(editUsernameText.trim(), selectedPet)
                                mainViewModel.updateHeight(editHeightText.trim())
                                mainViewModel.updateWeight(editWeightText.trim())
                                showEditDetailsDialog = false
                                Toast.makeText(context, "Profile details updated!", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Text("Save")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEditDetailsDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@Composable
fun ThemeModeItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
        ),
        border = BorderStroke(
            width = 1.5.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun ProfileStatItem(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}