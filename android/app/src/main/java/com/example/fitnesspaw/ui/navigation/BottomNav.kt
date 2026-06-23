package com.example.fitnesspaw.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.*
import com.example.fitnesspaw.ui.screens.AnalyticsScreen
import com.example.fitnesspaw.ui.screens.DashboardScreen
import com.example.fitnesspaw.ui.screens.PetsScreen
import com.example.fitnesspaw.ui.screens.ProfileScreen
import com.example.fitnesspaw.ui.theme.ThemeViewModel
import com.example.fitnesspaw.ui.viewmodel.MainViewModel

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    object Home : BottomNavItem(
        "dashboard",
        "Home",
        Icons.Default.Home
    )

    object Analytics : BottomNavItem(
        "analytics",
        "Analytics",
        Icons.Default.Analytics
    )

    object Pets : BottomNavItem(
        "pets",
        "Pets",
        Icons.Default.Pets
    )

    object Profile : BottomNavItem(
        "profile",
        "Profile",
        Icons.Default.Person
    )
}

@Composable
fun BottomNavScreen(
    themeViewModel: ThemeViewModel,
    mainViewModel: MainViewModel,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = NavigationBarDefaults.Elevation
            ) {
                val items = listOf(
                    BottomNavItem.Home,
                    BottomNavItem.Analytics,
                    BottomNavItem.Pets,
                    BottomNavItem.Profile
                )

                val currentRoute = navController
                    .currentBackStackEntryAsState()
                    .value
                    ?.destination
                    ?.route

                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                item.icon,
                                contentDescription = null
                            )
                        },
                        label = {
                            Text(item.title)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.padding(padding)
        ) {
            composable("dashboard") {
                DashboardScreen(mainViewModel)
            }

            composable("analytics") {
                AnalyticsScreen(mainViewModel)
            }

            composable("pets") {
                PetsScreen(mainViewModel)
            }

            composable("profile") {
                ProfileScreen(themeViewModel, mainViewModel, onLogout)
            }
        }
    }
}