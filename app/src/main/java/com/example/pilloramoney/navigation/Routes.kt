package com.example.pilloramoney.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object Home : Screen()
    @Serializable
    data object Spreadsheet : Screen()
    @Serializable
    data object Calculator : Screen()
    @Serializable
    data object Settings : Screen() // For the drawer
}
