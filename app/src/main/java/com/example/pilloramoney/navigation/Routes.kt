package com.example.pilloramoney.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object Home : Screen()
    
    @Serializable
    data object Spreadsheet : Screen() // "Projeção" in the drawer
    
    @Serializable
    data object BalanceHorizon : Screen() // "Horizonte de Saldo"
    
    @Serializable
    data object AddTransaction : Screen() // "Lançamentos"

    @Serializable
    data object SavingsDetail : Screen() // "Detalhes de Economia"
    
    @Serializable
    data object Calculator : Screen()
    
    @Serializable
    data object Categories : Screen()
    
    @Serializable
    data object Settings : Screen()
}
