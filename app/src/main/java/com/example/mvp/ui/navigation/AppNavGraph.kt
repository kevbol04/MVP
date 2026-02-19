package com.example.mvp.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mvp.ui.navigation.Route

import com.example.mvp.ui.screens.login.AuthScreen
import com.example.mvp.ui.screens.dashboard.DashboardScreen

import com.example.mvp.ui.screens.training.TrainingsScreen
import com.example.mvp.ui.screens.training.TrainingFormScreen
import com.example.mvp.ui.screens.training.Training

import com.example.mvp.ui.screens.matches.MatchesScreen
import com.example.mvp.ui.screens.matches.MatchFormScreen
import com.example.mvp.ui.screens.matches.Match

@Composable
fun AppNavGraph(
    startDestination: String = Route.Auth.route
) {
    val navController = rememberNavController()

    var trainings by remember { mutableStateOf(emptyList<Training>()) }
    var matches by remember { mutableStateOf(emptyList<Match>()) }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable(Route.Auth.route) {
            AuthScreen(
                onLogin = { _, _ ->
                    navController.navigate(Route.Dashboard.route) {
                        popUpTo(Route.Auth.route) { inclusive = true }
                    }
                },
                onRegister = { _, _, _ ->
                    navController.navigate(Route.Dashboard.route) {
                        popUpTo(Route.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Route.Dashboard.route) {
            DashboardScreen(
                username = "Kev",
                onGoTraining = { navController.navigate(Route.Trainings.route) },
                onGoMatches = { navController.navigate(Route.Matches.route) },
                onGoPlayers = { /* luego */ },
                onGoStats = { /* luego */ }
            )
        }

        composable(Route.Trainings.route) {
            TrainingsScreen(
                trainings = trainings,
                onBack = { navController.popBackStack() },
                onCreateTraining = { navController.navigate(Route.TrainingForm.route) },
                onEditTraining = { t -> navController.navigate(Route.TrainingFormWithId.createRoute(t.id)) }
            )
        }

        composable(Route.TrainingForm.route) {
            TrainingFormScreen(
                initial = null,
                onBack = { navController.popBackStack() },
                onSave = { newTraining ->
                    val nextId = (trainings.maxOfOrNull { it.id } ?: 0) + 1
                    trainings = listOf(newTraining.copy(id = nextId, isRecent = true)) + trainings
                    navController.popBackStack()
                }
            )
        }

        composable(Route.TrainingFormWithId.route) { backStackEntry ->
            val id = backStackEntry.arguments?.getString(Route.TrainingFormWithId.ARG_ID)?.toIntOrNull()
            val current = trainings.firstOrNull { it.id == id }

            TrainingFormScreen(
                initial = current,
                onBack = { navController.popBackStack() },
                onSave = { edited ->
                    trainings = trainings.map { if (it.id == edited.id) edited.copy(isRecent = true) else it }
                    navController.popBackStack()
                }
            )
        }

        composable(Route.Matches.route) {
            MatchesScreen(
                matches = matches,
                onBack = { navController.popBackStack() },
                onCreateMatch = { navController.navigate(Route.MatchForm.route) },
                onEditMatch = { m -> navController.navigate(Route.MatchFormWithId.createRoute(m.id)) }
            )
        }

        composable(Route.MatchForm.route) {
            MatchFormScreen(
                initial = null,
                onBack = { navController.popBackStack() },
                onSave = { newMatch ->
                    val nextId = (matches.maxOfOrNull { it.id } ?: 0) + 1
                    matches = listOf(newMatch.copy(id = nextId, isRecent = true)) + matches
                    navController.popBackStack()
                }
            )
        }

        composable(Route.MatchFormWithId.route) { backStackEntry ->
            val id = backStackEntry.arguments?.getString(Route.MatchFormWithId.ARG_ID)?.toIntOrNull()
            val current = matches.firstOrNull { it.id == id }

            MatchFormScreen(
                initial = current,
                onBack = { navController.popBackStack() },
                onSave = { edited ->
                    matches = matches.map { if (it.id == edited.id) edited.copy(isRecent = true) else it }
                    navController.popBackStack()
                }
            )
        }
    }
}