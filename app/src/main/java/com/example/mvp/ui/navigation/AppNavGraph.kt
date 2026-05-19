package com.example.mvp.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mvp.ui.components.AppLoadingScreen
import com.example.mvp.ui.components.AppSnackbarHost
import com.example.mvp.ui.screens.club.ClubScreen
import com.example.mvp.ui.screens.club.ClubViewModel
import com.example.mvp.ui.screens.dashboard.DashboardScreen
import com.example.mvp.ui.screens.login.AuthRoute
import com.example.mvp.ui.screens.matches.MatchDetailScreen
import com.example.mvp.ui.screens.matches.MatchFormScreen
import com.example.mvp.ui.screens.matches.MatchesScreen
import com.example.mvp.ui.screens.matches.MatchesViewModel
import com.example.mvp.ui.screens.players.PlayerDetailScreen
import com.example.mvp.ui.screens.players.PlayerFormScreen
import com.example.mvp.ui.screens.players.PlayersScreen
import com.example.mvp.ui.screens.players.PlayersViewModel
import com.example.mvp.ui.screens.session.SessionViewModel
import com.example.mvp.ui.screens.settings.AboutScreen
import com.example.mvp.ui.screens.settings.AccountRoute
import com.example.mvp.ui.screens.settings.PrivacyScreen
import com.example.mvp.ui.screens.settings.SettingsScreen
import com.example.mvp.ui.screens.stats.StatsScreen
import com.example.mvp.ui.screens.training.TrainingFormScreen
import com.example.mvp.ui.screens.training.TrainingsScreen
import com.example.mvp.ui.screens.training.TrainingsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AppNavGraph(
    startDestination: String = Route.Auth.route
) {
    val navController = rememberNavController()

    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarScope = rememberCoroutineScope()

    fun showSnackbar(message: String) {
        snackbarScope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }

    val sessionViewModel: SessionViewModel = hiltViewModel()
    val sessionState by sessionViewModel.uiState.collectAsState()
    val savedSession = sessionState.userSession
    val hasCompletedClubSetup = sessionState.hasCompletedClubSetup

    var minimumSplashFinished by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        delay(900)
        minimumSplashFinished = true
    }

    if (sessionState.loading || !minimumSplashFinished) {
        AppLoadingScreen()
        return
    }

    var currentUsername by rememberSaveable {
        mutableStateOf(savedSession?.name ?: "Usuario")
    }

    var currentEmail by rememberSaveable {
        mutableStateOf(savedSession?.email ?: "")
    }

    var currentUserId by rememberSaveable {
        mutableStateOf(savedSession?.userId ?: 0L)
    }

    LaunchedEffect(savedSession) {
        currentUserId = savedSession?.userId ?: 0L
        currentUsername = savedSession?.name ?: "Usuario"
        currentEmail = savedSession?.email ?: ""
    }

    LaunchedEffect(savedSession, hasCompletedClubSetup) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route

        if (savedSession == null) {
            if (currentRoute != Route.Auth.route) {
                navController.navigate(Route.Auth.route) {
                    popUpTo(0) {
                        inclusive = true
                    }
                }
            }
            return@LaunchedEffect
        }

        if (!hasCompletedClubSetup) {
            if (currentRoute != Route.ClubSetup.route) {
                navController.navigate(Route.ClubSetup.route) {
                    popUpTo(0) {
                        inclusive = true
                    }
                }
            }
            return@LaunchedEffect
        }

        if (currentRoute == Route.Auth.route || currentRoute == Route.ClubSetup.route) {
            navController.navigate(Route.Dashboard.route) {
                popUpTo(0) {
                    inclusive = true
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        NavHost(
            navController = navController,
            startDestination = when {
                savedSession == null -> startDestination
                hasCompletedClubSetup -> Route.Dashboard.route
                else -> Route.ClubSetup.route
            }
        ) {

            // ---------------- LOGIN ----------------
            composable(Route.Auth.route) {
                AuthRoute(
                    onSuccess = { id, name, email ->
                        currentUserId = id
                        currentUsername = name
                        currentEmail = email

                        sessionViewModel.saveSession(
                            userId = id,
                            name = name,
                            email = email
                        )
                    }
                )
            }

            // ---------------- DASHBOARD ----------------
            composable(Route.Dashboard.route) {
                val playersVm: PlayersViewModel = hiltViewModel()
                val matchesVm: MatchesViewModel = hiltViewModel()
                val trainingsVm: TrainingsViewModel = hiltViewModel()
                val clubVm: ClubViewModel = hiltViewModel()

                LaunchedEffect(currentUserId) {
                    playersVm.setUser(currentUserId)
                    matchesVm.setUser(currentUserId)
                    trainingsVm.setUser(currentUserId)
                    clubVm.setUser(currentUserId)
                }

                val players by playersVm.players.collectAsState()
                val matches by matchesVm.matches.collectAsState()
                val trainings by trainingsVm.trainings.collectAsState()
                val club by clubVm.club.collectAsState()
                val clubLoaded by clubVm.loaded.collectAsState()

                LaunchedEffect(currentUserId, clubLoaded, club) {
                    if (currentUserId > 0L && clubLoaded && club == null) {
                        navController.navigate(Route.ClubSetup.route) {
                            popUpTo(Route.Dashboard.route) { inclusive = true }
                        }
                    }
                }

                DashboardScreen(
                    username = currentUsername,
                    club = club,
                    trainings = trainings,
                    matches = matches,
                    players = players,

                    onGoTraining = {
                        navController.navigateToTab(Route.Trainings.route)
                    },
                    onGoMatches = {
                        navController.navigateToTab(Route.Matches.route)
                    },
                    onGoPlayers = {
                        navController.navigateToTab(Route.Players.route)
                    },
                    onGoStats = {
                        navController.navigateToTab(Route.Stats.route)
                    },
                    onGoClub = {
                        navController.navigate(Route.Club.route)
                    },
                    onGoSettings = {
                        navController.navigate(Route.Settings.route)
                    },

                    onCreateTraining = {
                        navController.navigate(Route.TrainingForm.route)
                    },
                    onCreateMatch = {
                        navController.navigate(Route.MatchForm.route)
                    },
                    onCreatePlayer = {
                        navController.navigate(Route.PlayerForm.route)
                    },

                    onOpenTraining = { id ->
                        navController.navigate(
                            Route.TrainingFormWithId.createRoute(id.toInt())
                        )
                    },
                    onOpenMatch = { id ->
                        navController.navigate(
                            Route.MatchDetail.createRoute(id.toInt())
                        )
                    },
                    onOpenPlayer = { id ->
                        navController.navigate(
                            Route.PlayerDetail.createRoute(id.toInt())
                        )
                    }
                )
            }

            // ---------------- SETTINGS ----------------
            composable(Route.Settings.route) {
                SettingsScreen(
                    username = currentUsername,
                    onBack = {
                        navController.popBackStack()
                    },
                    onOpenAccount = {
                        navController.navigate(Route.Account.route)
                    },
                    onOpenClub = {
                        navController.navigate(Route.Club.route)
                    },
                    onOpenPrivacy = {
                        navController.navigate(Route.Privacy.route)
                    },
                    onOpenAbout = {
                        navController.navigate(Route.About.route)
                    },
                    onLogout = {
                        currentUserId = 0L
                        currentUsername = "Usuario"
                        currentEmail = ""

                        sessionViewModel.clearSession()

                        navController.navigate(Route.Auth.route) {
                            popUpTo(0) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            // ---------------- CLUB ----------------
            composable(Route.Club.route) {
                val vm: ClubViewModel = hiltViewModel()

                LaunchedEffect(currentUserId) {
                    vm.setUser(currentUserId)
                }

                val club by vm.club.collectAsState()

                ClubScreen(
                    club = club,
                    defaultCoachName = currentUsername,
                    onBack = {
                        navController.popBackStack()
                    },
                    onSave = { updatedClub ->
                        vm.save(updatedClub) {
                            showSnackbar("Club actualizado correctamente")

                            snackbarScope.launch {
                                delay(300)
                                navController.popBackStack()
                            }
                        }
                    }
                )
            }


            // ---------------- CLUB SETUP ----------------
            composable(Route.ClubSetup.route) {
                val vm: ClubViewModel = hiltViewModel()

                LaunchedEffect(currentUserId) {
                    vm.setUser(currentUserId)
                }

                ClubScreen(
                    club = null,
                    defaultCoachName = currentUsername,
                    isInitialSetup = true,
                    onBack = {},
                    onCancelInitialSetup = {
                        currentUserId = 0L
                        currentUsername = "Usuario"
                        currentEmail = ""

                        sessionViewModel.clearSession()

                        navController.navigate(Route.Auth.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onSave = { createdClub ->
                        vm.save(createdClub) {
                            sessionViewModel.markClubSetupCompleted()

                            showSnackbar("Club creado correctamente")

                            navController.navigate(Route.Dashboard.route) {
                                popUpTo(Route.ClubSetup.route) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                )
            }

            // ---------------- ACCOUNT ----------------
            composable(Route.Account.route) {
                AccountRoute(
                    name = currentUsername,
                    email = currentEmail,
                    onBack = {
                        navController.popBackStack()
                    },
                    onProfileUpdated = { newName, newEmail ->
                        currentUsername = newName
                        currentEmail = newEmail

                        sessionViewModel.saveSession(
                            userId = currentUserId,
                            name = newName,
                            email = newEmail
                        )

                        showSnackbar("Perfil actualizado correctamente")
                    },
                    onDeleteAccount = {
                        currentUserId = 0L
                        currentUsername = "Usuario"
                        currentEmail = ""

                        sessionViewModel.clearSession()

                        navController.navigate(Route.Auth.route) {
                            popUpTo(0) {
                                inclusive = true
                            }
                        }

                        showSnackbar("Cuenta eliminada")
                    }
                )
            }

            // ---------------- ABOUT ----------------
            composable(Route.About.route) {
                AboutScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            // ---------------- PRIVACY ----------------
            composable(Route.Privacy.route) {
                PrivacyScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            // ---------------- TRAININGS ----------------
            composable(Route.Trainings.route) {
                val vm: TrainingsViewModel = hiltViewModel()

                LaunchedEffect(currentUserId) {
                    vm.setUser(currentUserId)
                }

                val trainings by vm.trainings.collectAsState()

                TrainingsScreen(
                    trainings = trainings,
                    onBack = {
                        navController.popBackStack()
                    },
                    onCreateTraining = {
                        navController.navigate(Route.TrainingForm.route)
                    },
                    onEditTraining = { training ->
                        navController.navigate(
                            Route.TrainingFormWithId.createRoute(training.id)
                        )
                    },
                    onDeleteTraining = { training ->
                        vm.delete(training)
                        showSnackbar("Entrenamiento eliminado")
                    },
                    onToggleDone = { training ->
                        vm.toggleDone(training)
                        showSnackbar(
                            if (training.isDone) {
                                "Entrenamiento marcado como pendiente"
                            } else {
                                "Entrenamiento marcado como hecho"
                            }
                        )
                    },

                    onGoDashboard = {
                        navController.navigateToTab(Route.Dashboard.route)
                    },
                    onGoMatches = {
                        navController.navigateToTab(Route.Matches.route)
                    },
                    onGoPlayers = {
                        navController.navigateToTab(Route.Players.route)
                    },
                    onGoStats = {
                        navController.navigateToTab(Route.Stats.route)
                    }
                )
            }

            composable(Route.TrainingForm.route) {
                val vm: TrainingsViewModel = hiltViewModel()

                LaunchedEffect(currentUserId) {
                    vm.setUser(currentUserId)
                }

                val trainings by vm.trainings.collectAsState()

                TrainingFormScreen(
                    initial = null,
                    existingTrainings = trainings,
                    onBack = {
                        navController.popBackStack()
                    },
                    onSave = { training ->
                        vm.save(training.copy(id = 0))
                        navController.popBackStack()
                        showSnackbar("Entrenamiento creado correctamente")
                    }
                )
            }

            composable(Route.TrainingFormWithId.route) { backStackEntry ->
                val vm: TrainingsViewModel = hiltViewModel()

                LaunchedEffect(currentUserId) {
                    vm.setUser(currentUserId)
                }

                val trainings by vm.trainings.collectAsState()

                val id = backStackEntry.arguments
                    ?.getString(Route.TrainingFormWithId.ARG_ID)
                    ?.toIntOrNull()

                if (id == null) {
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                    return@composable
                }

                val current = trainings.firstOrNull { it.id == id }

                if (current == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                    return@composable
                }

                TrainingFormScreen(
                    initial = current,
                    existingTrainings = trainings,
                    onBack = {
                        navController.popBackStack()
                    },
                    onSave = { edited ->
                        vm.save(edited)
                        navController.popBackStack()
                        showSnackbar("Entrenamiento actualizado correctamente")
                    }
                )
            }

            // ---------------- MATCHES ----------------
            composable(Route.Matches.route) {
                val vm: MatchesViewModel = hiltViewModel()

                LaunchedEffect(currentUserId) {
                    vm.setUser(currentUserId)
                }

                val matches by vm.matches.collectAsState()

                MatchesScreen(
                    matches = matches,
                    onBack = {
                        navController.popBackStack()
                    },
                    onCreateMatch = {
                        navController.navigate(Route.MatchForm.route)
                    },
                    onOpenMatch = { match ->
                        navController.navigate(
                            Route.MatchDetail.createRoute(match.id)
                        )
                    },
                    onEditMatch = { match ->
                        navController.navigate(
                            Route.MatchFormWithId.createRoute(match.id)
                        )
                    },
                    onDeleteMatch = { match ->
                        vm.delete(match)
                        showSnackbar("Partido eliminado")
                    },

                    onGoDashboard = {
                        navController.navigateToTab(Route.Dashboard.route)
                    },
                    onGoTraining = {
                        navController.navigateToTab(Route.Trainings.route)
                    },
                    onGoPlayers = {
                        navController.navigateToTab(Route.Players.route)
                    },
                    onGoStats = {
                        navController.navigateToTab(Route.Stats.route)
                    }
                )
            }

            composable(Route.MatchForm.route) {
                val vm: MatchesViewModel = hiltViewModel()

                LaunchedEffect(currentUserId) {
                    vm.setUser(currentUserId)
                }

                MatchFormScreen(
                    initial = null,
                    onBack = {
                        navController.popBackStack()
                    },
                    onSave = { match ->
                        vm.save(match.copy(id = 0))
                        navController.popBackStack()
                        showSnackbar("Partido creado correctamente")
                    }
                )
            }

            composable(Route.MatchDetail.route) { backStackEntry ->
                val vm: MatchesViewModel = hiltViewModel()

                LaunchedEffect(currentUserId) {
                    vm.setUser(currentUserId)
                }

                val id = backStackEntry.arguments
                    ?.getString(Route.MatchDetail.ARG_ID)
                    ?.toIntOrNull()

                if (id == null) {
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                    return@composable
                }

                val current by vm.matchById(id).collectAsState(initial = null)

                if (current == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                    return@composable
                }

                val match = current ?: return@composable

                MatchDetailScreen(
                    match = match,
                    onBack = {
                        navController.popBackStack()
                    },
                    onEdit = { match ->
                        navController.navigate(
                            Route.MatchFormWithId.createRoute(match.id)
                        )
                    }
                )
            }

            composable(Route.MatchFormWithId.route) { backStackEntry ->
                val vm: MatchesViewModel = hiltViewModel()

                LaunchedEffect(currentUserId) {
                    vm.setUser(currentUserId)
                }

                val id = backStackEntry.arguments
                    ?.getString(Route.MatchFormWithId.ARG_ID)
                    ?.toIntOrNull()

                if (id == null) {
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                    return@composable
                }

                val current by vm.matchById(id).collectAsState(initial = null)

                if (current == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                    return@composable
                }

                MatchFormScreen(
                    initial = current,
                    onBack = {
                        navController.popBackStack()
                    },
                    onSave = { edited ->
                        vm.save(edited)
                        navController.popBackStack()
                        showSnackbar("Partido actualizado correctamente")
                    }
                )
            }

            // ---------------- PLAYERS ----------------
            composable(Route.Players.route) {
                val vm: PlayersViewModel = hiltViewModel()

                LaunchedEffect(currentUserId) {
                    vm.setUser(currentUserId)
                }

                val players by vm.players.collectAsState()
                val selectedFormationId by vm.selectedFormationId.collectAsState()

                PlayersScreen(
                    players = players,
                    selectedFormationId = selectedFormationId,
                    onBack = {
                        navController.popBackStack()
                    },
                    onCreatePlayer = {
                        navController.navigate(Route.PlayerForm.route)
                    },
                    onEditPlayer = { player ->
                        navController.navigate(
                            Route.PlayerFormWithId.createRoute(player.id)
                        )
                    },
                    onOpenPlayer = { player ->
                        navController.navigate(
                            Route.PlayerDetail.createRoute(player.id)
                        )
                    },
                    onDeletePlayer = { player ->
                        vm.delete(player)
                        showSnackbar("Jugador eliminado")
                    },
                    onSavePlayer = { player ->
                        vm.save(player)
                    },
                    onSaveSelectedFormation = { formationId ->
                        vm.saveSelectedFormation(formationId)
                    },

                    onGoDashboard = {
                        navController.navigateToTab(Route.Dashboard.route)
                    },
                    onGoTraining = {
                        navController.navigateToTab(Route.Trainings.route)
                    },
                    onGoMatches = {
                        navController.navigateToTab(Route.Matches.route)
                    },
                    onGoStats = {
                        navController.navigateToTab(Route.Stats.route)
                    }
                )
            }

            composable(Route.PlayerForm.route) {
                val vm: PlayersViewModel = hiltViewModel()

                LaunchedEffect(currentUserId) {
                    vm.setUser(currentUserId)
                }

                val players by vm.players.collectAsState()

                PlayerFormScreen(
                    initial = null,
                    existingPlayers = players,
                    onBack = {
                        navController.popBackStack()
                    },
                    onSave = { player ->
                        vm.save(player.copy(id = 0))
                        navController.popBackStack()
                        showSnackbar("Jugador creado correctamente")
                    }
                )
            }

            composable(Route.PlayerFormWithId.route) { backStackEntry ->
                val vm: PlayersViewModel = hiltViewModel()

                LaunchedEffect(currentUserId) {
                    vm.setUser(currentUserId)
                }

                val players by vm.players.collectAsState()

                val id = backStackEntry.arguments
                    ?.getString(Route.PlayerFormWithId.ARG_ID)
                    ?.toIntOrNull()

                if (id == null) {
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                    return@composable
                }

                val current = players.firstOrNull { it.id == id }

                if (current == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                    return@composable
                }

                PlayerFormScreen(
                    initial = current,
                    existingPlayers = players,
                    onBack = {
                        navController.popBackStack()
                    },
                    onSave = { edited ->
                        vm.save(edited)
                        navController.popBackStack()
                        showSnackbar("Jugador actualizado correctamente")
                    }
                )
            }

            composable(Route.PlayerDetail.route) { backStackEntry ->
                val vm: PlayersViewModel = hiltViewModel()

                LaunchedEffect(currentUserId) {
                    vm.setUser(currentUserId)
                }

                val id = backStackEntry.arguments
                    ?.getString(Route.PlayerDetail.ARG_ID)
                    ?.toIntOrNull()

                if (id == null) {
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                    return@composable
                }

                val current by vm.playerById(id).collectAsState(initial = null)

                if (current == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                    return@composable
                }

                val player = current ?: return@composable

                PlayerDetailScreen(
                    player = player,
                    onBack = {
                        navController.popBackStack()
                    },
                    onEdit = { player ->
                        navController.navigate(
                            Route.PlayerFormWithId.createRoute(player.id)
                        )
                    }
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
                    onBack = {
                        navController.popBackStack()
                    },

                    onGoDashboard = {
                        navController.navigateToTab(Route.Dashboard.route)
                    },
                    onGoTraining = {
                        navController.navigateToTab(Route.Trainings.route)
                    },
                    onGoMatches = {
                        navController.navigateToTab(Route.Matches.route)
                    },
                    onGoPlayers = {
                        navController.navigateToTab(Route.Players.route)
                    }
                )
            }
        }

        AppSnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 28.dp, start = 20.dp, end = 20.dp)
        )
    }
}

private fun NavHostController.navigateToTab(route: String) {
    navigate(route) {
        launchSingleTop = true
        restoreState = true
        popUpTo(Route.Dashboard.route) {
            saveState = true
        }
    }
}