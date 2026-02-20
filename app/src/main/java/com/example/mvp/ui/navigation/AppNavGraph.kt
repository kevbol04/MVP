package com.example.mvp.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.mvp.ui.screens.login.AuthScreen
import com.example.mvp.ui.screens.dashboard.DashboardScreen

import com.example.mvp.ui.screens.training.TrainingsScreen
import com.example.mvp.ui.screens.training.TrainingFormScreen
import com.example.mvp.ui.screens.training.Training

import com.example.mvp.ui.screens.matches.MatchesScreen
import com.example.mvp.ui.screens.matches.MatchFormScreen
import com.example.mvp.ui.screens.matches.Match

import com.example.mvp.ui.screens.players.PlayersScreen
import com.example.mvp.ui.screens.players.PlayerFormScreen
import com.example.mvp.ui.screens.players.PlayerDetailScreen
import com.example.mvp.ui.screens.players.Player
import com.example.mvp.ui.screens.players.PlayerPosition
import com.example.mvp.ui.screens.players.PlayerStatus

import com.example.mvp.ui.screens.stats.StatsScreen

@Composable
fun AppNavGraph(
    startDestination: String = Route.Auth.route
) {
    val navController = rememberNavController()

    var trainings by remember { mutableStateOf(emptyList<Training>()) }
    var matches by remember { mutableStateOf(emptyList<Match>()) }
    var players by remember { mutableStateOf(emptyList<Player>()) }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // ---------------- LOGIN ----------------

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

        // ---------------- DASHBOARD ----------------

        composable(Route.Dashboard.route) {
            DashboardScreen(
                username = "Kev",
                onGoTraining = { navController.navigate(Route.Trainings.route) },
                onGoMatches = { navController.navigate(Route.Matches.route) },
                onGoPlayers = { navController.navigate(Route.Players.route) },
                onGoStats = { navController.navigate(Route.Stats.route) }
            )
        }

        // ---------------- TRAININGS ----------------

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
                    trainings = listOf(newTraining.copy(id = nextId)) + trainings
                    navController.popBackStack()
                }
            )
        }

        composable(Route.TrainingFormWithId.route) { backStackEntry ->
            val id = backStackEntry.arguments
                ?.getString(Route.TrainingFormWithId.ARG_ID)
                ?.toIntOrNull()
            val current = trainings.firstOrNull { it.id == id }

            TrainingFormScreen(
                initial = current,
                onBack = { navController.popBackStack() },
                onSave = { edited ->
                    trainings = trainings.map { if (it.id == edited.id) edited else it }
                    navController.popBackStack()
                }
            )
        }

        // ---------------- MATCHES ----------------

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
                    matches = listOf(newMatch.copy(id = nextId)) + matches
                    navController.popBackStack()
                }
            )
        }

        composable(Route.MatchFormWithId.route) { backStackEntry ->
            val id = backStackEntry.arguments
                ?.getString(Route.MatchFormWithId.ARG_ID)
                ?.toIntOrNull()
            val current = matches.firstOrNull { it.id == id }

            MatchFormScreen(
                initial = current,
                onBack = { navController.popBackStack() },
                onSave = { edited ->
                    matches = matches.map { if (it.id == edited.id) edited else it }
                    navController.popBackStack()
                }
            )
        }

        // ---------------- PLAYERS ----------------

        composable(Route.Players.route) {
            PlayersScreen(
                players = players,
                onBack = { navController.popBackStack() },
                onCreatePlayer = { navController.navigate(Route.PlayerForm.route) },
                onEditPlayer = { p -> navController.navigate(Route.PlayerFormWithId.createRoute(p.id)) },
                onOpenPlayer = { p -> navController.navigate(Route.PlayerDetail.createRoute(p.id)) }
            )
        }

        composable(Route.PlayerForm.route) {
            PlayerFormScreen(
                initial = null,
                onBack = { navController.popBackStack() },
                onSave = { newPlayer ->
                    val nextId = (players.maxOfOrNull { it.id } ?: 0) + 1
                    players = listOf(newPlayer.copy(id = nextId)) + players
                    navController.popBackStack()
                }
            )
        }

        composable(Route.PlayerFormWithId.route) { backStackEntry ->
            val id = backStackEntry.arguments
                ?.getString(Route.PlayerFormWithId.ARG_ID)
                ?.toIntOrNull()
            val current = players.firstOrNull { it.id == id }

            PlayerFormScreen(
                initial = current,
                onBack = { navController.popBackStack() },
                onSave = { edited ->
                    players = players.map { if (it.id == edited.id) edited else it }
                    navController.popBackStack()
                }
            )
        }

        composable(Route.PlayerDetail.route) { backStackEntry ->
            val id = backStackEntry.arguments
                ?.getString(Route.PlayerDetail.ARG_ID)
                ?.toIntOrNull()
            val current = players.firstOrNull { it.id == id }

            if (current == null) {
                LaunchedEffect(Unit) { navController.popBackStack() }
            } else {
                PlayerDetailScreen(
                    player = current,
                    onBack = { navController.popBackStack() },
                    onEdit = { p -> navController.navigate(Route.PlayerFormWithId.createRoute(p.id)) }
                )
            }
        }

        // ---------------- STATS ----------------

        composable(Route.Stats.route) {
            StatsScreen(
                players = players,
                matches = matches,
                trainings = trainings,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

private fun defaultPlayersForNavInit(): List<Player> = listOf(
    Player(1, "Álex Romero", PlayerPosition.POR, 23, 1, 78, PlayerStatus.TITULAR),
    Player(2, "Sergio Vidal", PlayerPosition.DEF, 27, 2, 80, PlayerStatus.TITULAR),
    Player(3, "Mario Costa", PlayerPosition.DEF, 21, 4, 74, PlayerStatus.SUPLENTE),
    Player(4, "Hugo Navarro", PlayerPosition.DEF, 29, 5, 82, PlayerStatus.TITULAR),
    Player(5, "Iván Paredes", PlayerPosition.MED, 24, 6, 79, PlayerStatus.TITULAR),
    Player(6, "Dani Serrano", PlayerPosition.MED, 22, 8, 77, PlayerStatus.SUPLENTE),
    Player(7, "Lucas Prieto", PlayerPosition.MED, 26, 10, 83, PlayerStatus.TITULAR),
    Player(8, "Adrián Molina", PlayerPosition.DEL, 25, 9, 84, PlayerStatus.TITULAR),
    Player(9, "Eric Salas", PlayerPosition.DEL, 20, 11, 73, PlayerStatus.LESIONADO),
    Player(10, "Bruno Sanz", PlayerPosition.DEL, 28, 7, 81, PlayerStatus.SUPLENTE),
)