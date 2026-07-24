package com.example.pilloramoney

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pilloramoney.navigation.Screen
import com.example.pilloramoney.ui.components.PilloraBottomBar
import com.example.pilloramoney.ui.components.PilloraDrawer
import com.example.pilloramoney.ui.screens.CalculatorScreen
import com.example.pilloramoney.ui.screens.HomeScreen
import com.example.pilloramoney.ui.screens.SettingsScreen
import com.example.pilloramoney.ui.screens.SpreadsheetScreen
import com.example.pilloramoney.ui.theme.PilloraMoneyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PilloraMoneyTheme {
                PilloraApp()
            }
        }
    }
}

@Composable
fun PilloraApp() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            PilloraDrawer(
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                closeDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                PilloraBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    onFabClick = {
                        // Ação do botão central (pode abrir diálogo rápido)
                    }
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home
                ) {
                    composable<Screen.Home> {
                        HomeScreen()
                    }
                    composable<Screen.Spreadsheet> {
                        SpreadsheetScreen()
                    }
                    composable<Screen.Calculator> {
                        CalculatorScreen()
                    }
                    composable<Screen.Settings> {
                        SettingsScreen()
                    }
                }
            }
        }
    }
}
