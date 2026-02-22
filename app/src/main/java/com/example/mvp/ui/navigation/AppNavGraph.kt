package com.example.mvp.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mvp.ui.screens.dashboard.DashboardScreen
import com.example.mvp.ui.screens.login.AuthRoute
import com.example.mvp.ui.screens.matches.Match
import com.example.mvp.ui.screens.matches.MatchFormScreen
import com.example.mvp.ui.screens.matches.MatchesScreen
import com.example.mvp.ui.screens.matches.MatchesViewModel
import com.example.mvp.ui.screens.players.Player
import com.example.mvp.ui.screens.players.PlayerDetailScreen
import com.example.mvp.ui.screens.players.PlayerFormScreen
import com.example.mvp.ui.screens.players.PlayerPosition
import com.example.mvp.ui.screens.players.PlayerStatus
import com.example.mvp.ui.screens.players.PlayersScreen
import com.example.mvp.ui.screens.players.PlayersViewModel
import com.example.mvp.ui.screens.settings.AccountRoute
import com.example.mvp.ui.screens.settings.SettingsScreen
import com.example.mvp.ui.screens.stats.StatsScreen
import com.example.mvp.ui.screens.training.Training
import com.example.mvp.ui.screens.training.TrainingFormScreen
import com.example.mvp.ui.screens.training.TrainingsScreen
import com.example.mvp.ui.screens.training.TrainingsViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(
    startDestination: String = Route.Auth.route
) {
    val navController = rememberNavController()

    var currentUsername by rememberSaveable { mutableStateOf("Usuario") }
    var currentEmail by rememberSaveable { mutableStateOf("") }
    var currentUserId by rememberSaveable { mutableStateOf(0L) }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // ---------------- LOGIN ----------------
        composable(Route.Auth.route) {
            AuthRoute(
                onSuccess = { id, name, email ->
                    currentUserId = id
                    currentUsername = name
                    currentEmail = email
                    navController.navigate(Route.Dashboard.route) {
                        popUpTo(Route.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        // ---------------- DASHBOARD ----------------
        composable(Route.Dashboard.route) {
            DashboardScreen(
                username = currentUsername,
                onGoTraining = { navController.navigateToTab(Route.Trainings.route) },
                onGoMatches = { navController.navigateToTab(Route.Matches.route) },
                onGoPlayers = { navController.navigateToTab(Route.Players.route) },
                onGoStats = { navController.navigateToTab(Route.Stats.route) },
                onGoSettings = { navController.navigate(Route.Settings.route) }
            )
        }

        // ---------------- SETTINGS ----------------
        composable(Route.Settings.route) {
            SettingsScreen(
                username = currentUsername,
                onBack = { navController.popBackStack() },
                onOpenAccount = { navController.navigate(Route.Account.route) },
                onOpenPrivacy = { },
                onOpenAbout = { },
                onLogout = {
                    currentUserId = 0L
                    currentUsername = "Usuario"
                    currentEmail = ""
                    navController.navigate(Route.Auth.route) {
                        popUpTo(Route.Dashboard.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Route.Account.route) {
            AccountRoute(
                name = currentUsername,
                email = currentEmail,
                onBack = { navController.popBackStack() },
                onProfileUpdated = { newName, newEmail ->
                    currentUsername = newName
                    currentEmail = newEmail
                },
                onDeleteAccount = {
                    currentUsername = "Usuario"
                    currentEmail = ""
                    navController.navigate(Route.Auth.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ---------------- TRAININGS ----------------
        composable(Route.Trainings.route) {
            val vm: TrainingsViewModel = hiltViewModel()
            LaunchedEffect(currentUserId) { vm.setUser(currentUserId) }

            val trainings by vm.trainings.collectAsState()

            TrainingsScreen(
                trainings = trainings,
                onBack = { navController.popBackStack() },
                onCreateTraining = { navController.navigate(Route.TrainingForm.route) },
                onEditTraining = { t -> navController.navigate(Route.TrainingFormWithId.createRoute(t.id)) },

                onDeleteTraining = { t -> vm.delete(t) },

                onGoMatches = { navController.navigateToTab(Route.Matches.route) },
                onGoPlayers = { navController.navigateToTab(Route.Players.route) },
                onGoStats = { navController.navigateToTab(Route.Stats.route) }
            )
        }

        composable(Route.TrainingForm.route) {
            val vm: TrainingsViewModel = hiltViewModel()
            LaunchedEffect(currentUserId) { vm.setUser(currentUserId) }

            TrainingFormScreen(
                initial = null,
                onBack = { navController.popBackStack() },
                onSave = { t ->
                    vm.save(t.copy(id = 0))
                    navController.popBackStack()
                }
            )
        }

        composable(Route.TrainingFormWithId.route) { backStackEntry ->
            val vm: TrainingsViewModel = hiltViewModel()
            LaunchedEffect(currentUserId) { vm.setUser(currentUserId) }

            val trainings by vm.trainings.collectAsState()

            val id = backStackEntry.arguments
                ?.getString(Route.TrainingFormWithId.ARG_ID)
                ?.toIntOrNull()

            val current = trainings.firstOrNull { it.id == id }

            TrainingFormScreen(
                initial = current,
                onBack = { navController.popBackStack() },
                onSave = { edited ->
                    vm.save(edited)
                    navController.popBackStack()
                }

            )
        }

        // ---------------- MATCHES ----------------
        composable(Route.Matches.route) {
            val vm: MatchesViewModel = hiltViewModel()
            LaunchedEffect(currentUserId) { vm.setUser(currentUserId) }

            val matches by vm.matches.collectAsState()

            MatchesScreen(
                matches = matches,
                onBack = { navController.popBackStack() },
                onCreateMatch = { navController.navigate(Route.MatchForm.route) },
                onEditMatch = { m -> navController.navigate(Route.MatchFormWithId.createRoute(m.id)) },
                onDeleteMatch = { m -> vm.delete(m) },

                onGoTraining = { navController.navigateToTab(Route.Trainings.route) },
                onGoPlayers = { navController.navigateToTab(Route.Players.route) },
                onGoStats = { navController.navigateToTab(Route.Stats.route) }
            )
        }

        composable(Route.MatchForm.route) {
            val vm: MatchesViewModel = hiltViewModel()
            LaunchedEffect(currentUserId) { vm.setUser(currentUserId) }

            MatchFormScreen(
                initial = null,
                onBack = { navController.popBackStack() },
                onSave = { m ->
                    vm.save(m.copy(id = 0))
                    navController.popBackStack()
                }
            )
        }

        composable(Route.MatchFormWithId.route) { backStackEntry ->
            val vm: MatchesViewModel = hiltViewModel()
            LaunchedEffect(currentUserId) { vm.setUser(currentUserId) }

            val id = backStackEntry.arguments
                ?.getString(Route.MatchFormWithId.ARG_ID)
                ?.toIntOrNull()

            if (id == null) {
                LaunchedEffect(Unit) { navController.popBackStack() }
                return@composable
            }

            val current by vm.matchById(id).collectAsState(initial = null)

            if (current == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return@composable
            }

            MatchFormScreen(
                initial = current,
                onBack = { navController.popBackStack() },
                onSave = { edited ->
                    vm.save(edited)
                    navController.popBackStack()
                }
            )
        }

        // ---------------- PLAYERS ----------------
        composable(Route.Players.route) {
            val vm: PlayersViewModel = hiltViewModel()
            LaunchedEffect(currentUserId) { vm.setUser(currentUserId) }

            val players by vm.players.collectAsState()

            PlayersScreen(
                players = players,
                onBack = { navController.popBackStack() },
                onCreatePlayer = { navController.navigate(Route.PlayerForm.route) },
                onEditPlayer = { p -> navController.navigate(Route.PlayerFormWithId.createRoute(p.id)) },
                onOpenPlayer = { p -> navController.navigate(Route.PlayerDetail.createRoute(p.id)) },
                onDeletePlayer = { p -> vm.delete(p) },

                onGoTraining = { navController.navigateToTab(Route.Trainings.route) },
                onGoMatches = { navController.navigateToTab(Route.Matches.route) },
                onGoStats = { navController.navigateToTab(Route.Stats.route) }
            )
        }

        composable(Route.PlayerForm.route) {
            val vm: PlayersViewModel = hiltViewModel()
            LaunchedEffect(currentUserId) { vm.setUser(currentUserId) }

            PlayerFormScreen(
                initial = null,
                onBack = { navController.popBackStack() },
                onSave = { p ->
                    vm.save(p.copy(id = 0))
                    navController.popBackStack()
                }
            )
        }

        composable(Route.PlayerFormWithId.route) { backStackEntry ->
            val vm: PlayersViewModel = hiltViewModel()
            LaunchedEffect(currentUserId) { vm.setUser(currentUserId) }

            val players by vm.players.collectAsState()

            val id = backStackEntry.arguments
                ?.getString(Route.PlayerFormWithId.ARG_ID)
                ?.toIntOrNull()

            val current = players.firstOrNull { it.id == id }

            PlayerFormScreen(
                initial = current,
                existingPlayers = players,
                onBack = { navController.popBackStack() },
                onSave = { edited ->
                    vm.save(edited)
                    navController.popBackStack()
                }
            )
        }

        composable(Route.PlayerDetail.route) { backStackEntry ->
            val vm: PlayersViewModel = hiltViewModel()
            LaunchedEffect(currentUserId) { vm.setUser(currentUserId) }

            val id = backStackEntry.arguments
                ?.getString(Route.PlayerDetail.ARG_ID)
                ?.toIntOrNull()

            if (id == null) {
                LaunchedEffect(Unit) { navController.popBackStack() }
                return@composable
            }

            val current by vm.playerById(id).collectAsState(initial = null)

            if (current == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return@composable
            }

            PlayerDetailScreen(
                player = current!!,
                onBack = { navController.popBackStack() },
                onEdit = { p -> navController.navigate(Route.PlayerFormWithId.createRoute(p.id)) }
            )
        }

        composable(Route.PlayerForm.route) {
            val vm: PlayersViewModel = hiltViewModel()
            LaunchedEffect(currentUserId) { vm.setUser(currentUserId) }

            val players by vm.players.collectAsState()

            PlayerFormScreen(
                initial = null,
                existingPlayers = players,
                onBack = { navController.popBackStack() },
                onSave = { newPlayer ->
                    vm.save(newPlayer.copy(id = 0))
                    navController.popBackStack()
                }
            )
        }

        composable(Route.PlayerFormWithId.route) { backStackEntry ->
            val vm: PlayersViewModel = hiltViewModel()
            LaunchedEffect(currentUserId) { vm.setUser(currentUserId) }

            val id = backStackEntry.arguments
                ?.getString(Route.PlayerFormWithId.ARG_ID)
                ?.toIntOrNull()

            if (id == null) {
                LaunchedEffect(Unit) { navController.popBackStack() }
                return@composable
            }

            val current by vm.playerById(id).collectAsState(initial = null)

            if (current == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return@composable
            }

            PlayerFormScreen(
                initial = current,
                onBack = { navController.popBackStack() },
                onSave = { edited ->
                    vm.save(edited)
                    navController.popBackStack()
                }
            )
        }

        composable(Route.PlayerDetail.route) { backStackEntry ->
            val vm: PlayersViewModel = hiltViewModel()
            LaunchedEffect(currentUserId) { vm.setUser(currentUserId) }

            val id = backStackEntry.arguments
                ?.getString(Route.PlayerDetail.ARG_ID)
                ?.toIntOrNull()

            if (id == null) {
                LaunchedEffect(Unit) { navController.popBackStack() }
                return@composable
            }

            val current by vm.playerById(id).collectAsState(initial = null)

            if (current == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return@composable
            }

            PlayerDetailScreen(
                player = current!!,
                onBack = { navController.popBackStack() },
                onEdit = { p -> navController.navigate(Route.PlayerFormWithId.createRoute(p.id)) }
            )
        }

        // ---------------- STATS ----------------
        composable(Route.Stats.route) {
            val playersVm: PlayersViewModel = hiltViewModel()
            val matchesVm: MatchesViewModel = hiltViewModel()
            val trainingsVm: TrainingsViewModel = hiltViewModel()

            LaunchedEffect(currentUserId) {
                playersVm.setUser(currentUserId)
                matchesVm.setUser(currentUserId)
                trainingsVm.setUser(currentUserId)
            }

            val players by playersVm.players.collectAsState()
            val matches by matchesVm.matches.collectAsState()
            val trainings by trainingsVm.trainings.collectAsState()

            StatsScreen(
                players = players,
                matches = matches,
                trainings = trainings,
                onBack = { navController.popBackStack() },

                onGoTraining = { navController.navigateToTab(Route.Trainings.route) },
                onGoMatches = { navController.navigateToTab(Route.Matches.route) },
                onGoPlayers = { navController.navigateToTab(Route.Players.route) }
            )
        }
    }
}

private fun NavHostController.navigateToTab(route: String) {
    navigate(route) {
        launchSingleTop = true
        restoreState = true
        popUpTo(Route.Dashboard.route) { saveState = true }
    }
}