package com.example.fitnesspaw

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fitnesspaw.notifications.NotificationScheduler
import com.example.fitnesspaw.sensor.StepSensorManager
import com.example.fitnesspaw.ui.navigation.BottomNavScreen
import com.example.fitnesspaw.ui.screens.LoginScreen
import com.example.fitnesspaw.ui.screens.SignupScreen
import com.example.fitnesspaw.ui.screens.SplashScreen
import com.example.fitnesspaw.ui.theme.FitnessPawTheme
import com.example.fitnesspaw.ui.theme.ThemeViewModel
import com.example.fitnesspaw.ui.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private val themeViewModel: ThemeViewModel by viewModels()
    private lateinit var stepSensorManager: StepSensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Step Sensor Manager with ViewModel Callback
        stepSensorManager = StepSensorManager(this) { totalSteps ->
            mainViewModel.onSensorStepCountChanged(totalSteps)
        }
        mainViewModel.setSensorSupported(stepSensorManager.isSensorAvailable)

        setContent {
            // Resolve active theme mode: 0 = Light, 1 = Dark, 2 = System
            val themeMode = themeViewModel.themeMode.intValue
            val darkThemeEnabled = when (themeMode) {
                0 -> false
                1 -> true
                else -> isSystemInDarkTheme()
            }

            FitnessPawTheme(
                darkTheme = darkThemeEnabled
            ) {
                val rootNavController = rememberNavController()

                NavHost(
                    navController = rootNavController,
                    startDestination = "splash"
                ) {
                    // SPLASH SCREEN
                    composable("splash") {
                        SplashScreen(
                            onSplashFinished = {
                                val currentUser = FirebaseAuth.getInstance().currentUser
                                if (currentUser != null) {
                                    mainViewModel.loadProfileFromFirestore {
                                        NotificationScheduler(this@MainActivity).scheduleReminders()
                                        rootNavController.navigate("main") {
                                            popUpTo("splash") { inclusive = true }
                                        }
                                    }
                                } else {
                                    rootNavController.navigate("login") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }
                            }
                        )
                    }

                    // LOGIN SCREEN
                    composable("login") {
                        LoginScreen(
                            onLoginClick = {
                                mainViewModel.loadProfileFromFirestore {
                                    NotificationScheduler(this@MainActivity).scheduleReminders()
                                    rootNavController.navigate("main") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            },
                            onSignupClick = {
                                rootNavController.navigate("signup")
                            }
                        )
                    }

                    // SIGNUP SCREEN
                    composable("signup") {
                        SignupScreen(
                            onSignupSuccess = { username, petIndex ->
                                mainViewModel.saveProfile(username, petIndex)
                                NotificationScheduler(this@MainActivity).scheduleReminders()
                                rootNavController.navigate("main") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onLoginClick = {
                                rootNavController.navigate("login") {
                                    popUpTo("signup") { inclusive = true }
                                }
                            }
                        )
                    }

                    // MAIN BOTTOM NAV CONTAINER SCREEN
                    composable("main") {
                        BottomNavScreen(
                            themeViewModel = themeViewModel,
                            mainViewModel = mainViewModel,
                            onLogout = {
                                mainViewModel.clearLocalData()
                                NotificationScheduler(this@MainActivity).cancelAllReminders()
                                FirebaseAuth.getInstance().signOut()
                                rootNavController.navigate("login") {
                                    popUpTo("main") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        stepSensorManager.startListening()
        mainViewModel.checkDailyReset()
    }

    override fun onPause() {
        super.onPause()
        stepSensorManager.stopListening()
    }
}