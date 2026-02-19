package com.example.mvp.ui.navigation

sealed class Route(val route: String) {

    // Auth / Dashboard
    data object Auth : Route("auth")
    data object Dashboard : Route("dashboard")

    // Trainings
    data object Trainings : Route("trainings")
    data object TrainingForm : Route("training_form")
    data object TrainingFormWithId : Route("training_form/{trainingId}") {
        const val ARG_ID = "trainingId"
        fun createRoute(id: Int) = "training_form/$id"
    }

    // Matches
    data object Matches : Route("matches")
    data object MatchForm : Route("match_form")
    data object MatchFormWithId : Route("match_form/{matchId}") {
        const val ARG_ID = "matchId"
        fun createRoute(id: Int) = "match_form/$id"
    }
}