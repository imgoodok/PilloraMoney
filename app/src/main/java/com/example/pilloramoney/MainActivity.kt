package com.example.pilloramoney

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pilloramoney.navigation.Screen
import com.example.pilloramoney.ui.components.PilloraBottomBar
import com.example.pilloramoney.ui.components.PilloraDrawer
import com.example.pilloramoney.ui.screens.*
import com.example.pilloramoney.ui.theme.PilloraMoneyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val sharedPrefs = remember { context.getSharedPreferences("prefs", Context.MODE_PRIVATE) }
            var themePref by remember { mutableStateOf(sharedPrefs.getString("theme", "System") ?: "System") }
            
            val darkTheme = when (themePref) {
                "Light" -> false
                "Dark" -> true
                else -> isSystemInDarkTheme()
            }

            PilloraMoneyTheme(darkTheme = darkTheme) {
                PilloraApp(
                    currentTheme = themePref,
                    onThemeChange = { newTheme ->
                        themePref = newTheme
                        sharedPrefs.edit().putString("theme", newTheme).apply()
                    }
                )
            }
        }
    }
}

@Composable
fun PilloraApp(currentTheme: String, onThemeChange: (String) -> Unit) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route
    val currentRoute = currentDestination?.substringAfterLast(".")

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
                        navController.navigate(Screen.AddTransaction)
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
                        HomeScreen(
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            onNavigateToSavings = { navController.navigate(Screen.SavingsDetail) }
                        )
                    }
                    composable<Screen.Spreadsheet> {
                        SpreadsheetScreen()
                    }
                    composable<Screen.BalanceHorizon> {
                        BalanceHorizonScreen()
                    }
                    composable<Screen.AddTransaction> {
                        AddTransactionScreen()
                    }
                    composable<Screen.SavingsDetail> {
                        SavingsDetailScreen(onBack = { navController.popBackStack() })
                    }
                    composable<Screen.Calculator> {
                        CalculatorScreen()
                    }
                    composable<Screen.Settings> {
                        SettingsScreen(
                            currentTheme = currentTheme,
                            onThemeChange = onThemeChange,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
