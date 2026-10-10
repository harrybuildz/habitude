package com.habitude.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.habitude.ui.addhabit.AddHabitScreen
import com.habitude.ui.settings.SettingsScreen
import com.habitude.ui.today.TodayScreen

object Routes {
    const val TODAY = "today"
    const val ADD_HABIT = "add_habit"
    const val SETTINGS = "settings"
}

@Composable
fun HabitudeApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.TODAY) {
        composable(Routes.TODAY) {
            TodayScreen(
                onAddHabit = { navController.navigate(Routes.ADD_HABIT) },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) },
            )
        }
        composable(Routes.ADD_HABIT) {
            AddHabitScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
