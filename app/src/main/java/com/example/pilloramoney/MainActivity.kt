package com.example.pilloramoney

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.pilloramoney.navigation.Screen
import com.example.pilloramoney.notifications.NotificationScheduler
import com.example.pilloramoney.notifications.PilloraNotificationManager
import com.example.pilloramoney.ui.components.PilloraBottomBar
import com.example.pilloramoney.ui.components.PilloraDrawer
import com.example.pilloramoney.ui.screens.*
import com.example.pilloramoney.ui.theme.PilloraMoneyTheme
import com.example.pilloramoney.ui.viewmodels.AuthViewModel
import com.example.pilloramoney.ui.viewmodels.SubscriptionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            scheduleNotifications()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        try {
            PilloraNotificationManager.createNotificationChannel(this)
            checkNotificationPermission()
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Erro ao iniciar notificações: ${e.message}")
        }
        handleDeepLink(intent)

        setContent {
            val context = LocalContext.current
            val sharedPrefs = remember { context.getSharedPreferences("prefs", android.content.Context.MODE_PRIVATE) }
            var themePref by remember { mutableStateOf(sharedPrefs.getString("theme", "System") ?: "System") }
            var initialDestination by remember { mutableStateOf<String?>(intent.getStringExtra("destination")) }

            // Obter idioma atual do AppCompatDelegate
            val currentAppLocales = AppCompatDelegate.getApplicationLocales()
            val languagePref = if (currentAppLocales.isEmpty) "System" else currentAppLocales.toLanguageTags()
            
            val authViewModel: AuthViewModel = androidx.hilt.navigation.compose.hiltViewModel()

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
                    },
                    currentLanguage = languagePref,
                    onLanguageChange = { newLang ->
                        val appLocale: LocaleListCompat = if (newLang == "System") {
                            LocaleListCompat.getEmptyLocaleList()
                        } else {
                            LocaleListCompat.forLanguageTags(newLang)
                        }
                        AppCompatDelegate.setApplicationLocales(appLocale)
                    },
                    initialDestination = initialDestination,
                    onDestinationHandled = { initialDestination = null },
                    isUserLoggedIn = authViewModel.currentUser.collectAsState().value != null,
                    authViewModel = authViewModel
                )
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: android.content.Intent) {
        val data = intent.data
        if (data != null) {
            when (data.host) {
                "spreadsheet" -> {
                    // Navegar para a tela de projeção
                    intent.putExtra("destination", "spreadsheet")
                }
                "savings" -> {
                    // Navegar para a tela de economia
                    intent.putExtra("destination", "savings")
                }
            }
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    scheduleNotifications()
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            scheduleNotifications()
        }
    }

    private fun scheduleNotifications() {
        NotificationScheduler.scheduleDailyNotifications(this)
    }
}

@Composable
fun PilloraApp(
    currentTheme: String, 
    onThemeChange: (String) -> Unit,
    currentLanguage: String,
    onLanguageChange: (String) -> Unit,
    initialDestination: String? = null,
    onDestinationHandled: () -> Unit = {},
    isUserLoggedIn: Boolean,
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route
    val currentRoute = currentDestination?.substringAfterLast(".")

    val subscriptionViewModel: SubscriptionViewModel = androidx.hilt.navigation.compose.hiltViewModel()
    val subscription by subscriptionViewModel.subscriptionStatus.collectAsState()

    // Proteção de Rotas: Redirecionar para Login se não estiver logado
    LaunchedEffect(isUserLoggedIn) {
        if (!isUserLoggedIn) {
            navController.navigate(Screen.Login) {
                popUpTo(0) { inclusive = true }
            }
        } else if (currentDestination?.contains("Login") == true || currentDestination?.contains("Register") == true) {
            navController.navigate(Screen.Home) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // Downgrade Check
    LaunchedEffect(subscription, currentRoute) {
        if (subscription.status == com.example.pilloramoney.data.model.SubscriptionStatus.EXPIRED && 
            isUserLoggedIn && currentRoute != "Downgrade") {
            navController.navigate(Screen.Downgrade) {
                launchSingleTop = true
            }
        }
    }

    LaunchedEffect(initialDestination) {
        if (initialDestination != null && isUserLoggedIn) {
            when (initialDestination) {
                "spreadsheet" -> {
                    navController.navigate(Screen.Spreadsheet) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
                "savings" -> {
                    navController.navigate(Screen.SavingsDetail) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            }
            onDestinationHandled()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = isUserLoggedIn,
        drawerContent = {
            val user = authViewModel.currentUser.collectAsState().value
            PilloraDrawer(
                userEmail = user?.email ?: "",
                userPhotoUrl = user?.photoUrl?.toString(),
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                closeDrawer = { scope.launch { drawerState.close() } },
                onLogout = {
                    authViewModel.signOut()
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets(0.dp),
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                if (isUserLoggedIn) {
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
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                NavHost(
                    navController = navController,
                    startDestination = if (isUserLoggedIn) Screen.Home else Screen.Login
                ) {
                    composable<Screen.Login> {
                        LoginScreen(
                            viewModel = authViewModel,
                            onNavigateToRegister = { navController.navigate(Screen.Register) },
                            onLoginSuccess = { navController.navigate(Screen.Home) { popUpTo(Screen.Login) { inclusive = true } } }
                        )
                    }
                    composable<Screen.Register> {
                        RegisterScreen(
                            viewModel = authViewModel,
                            onNavigateToLogin = { navController.popBackStack() },
                            onRegisterSuccess = { navController.navigate(Screen.Home) { popUpTo(Screen.Register) { inclusive = true } } }
                        )
                    }
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
                            currentLanguage = currentLanguage,
                            onLanguageChange = onLanguageChange,
                            onBack = { navController.popBackStack() },
                            onLogout = { authViewModel.signOut() }
                        )
                    }
                    composable<Screen.Subscription> {
                        SubscriptionScreen(
                            viewModel = subscriptionViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable<Screen.Downgrade> {
                        DowngradeScreen(
                            viewModel = subscriptionViewModel,
                            onNavigateToSubscription = { navController.navigate(Screen.Subscription) },
                            onContinueFree = { 
                                subscriptionViewModel.updateSubscription(com.example.pilloramoney.data.model.SubscriptionStatus.FREE)
                                navController.navigate(Screen.Home) { popUpTo(Screen.Home) { inclusive = true } }
                            }
                        )
                    }
                    composable<Screen.Community> {
                        if (subscription.status == com.example.pilloramoney.data.model.SubscriptionStatus.PREMIUM) {
                            CommunityScreen(
                                onNavigateToCreate = { navController.navigate(Screen.CreateCommunity) },
                                onNavigateToBrowse = { navController.navigate(Screen.CommunityBrowse) },
                                onNavigateToProfile = { navController.navigate(Screen.CommunityProfile) }
                            )
                        } else {
                            // Placeholder UI while navigating
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                            LaunchedEffect(Unit) {
                                navController.navigate(Screen.Subscription) {
                                    popUpTo(Screen.Home)
                                }
                            }
                        }
                    }
                    composable<Screen.CreateCommunity> {
                        CreateCommunityScreen(onBack = { navController.popBackStack() })
                    }
                    composable<Screen.CommunityBrowse> {
                        CommunityBrowseScreen(
                            onBack = { navController.popBackStack() },
                            onCommunityClick = { id -> 
                                navController.navigate(Screen.CommunityDetail(id))
                            }
                        )
                    }
                    composable<Screen.CommunityProfile> {
                        CommunityProfileScreen(onBack = { navController.popBackStack() })
                    }
                    composable<Screen.CommunityDetail> { backStackEntry ->
                        val detail: Screen.CommunityDetail = backStackEntry.toRoute()
                        CommunityDetailScreen(
                            communityId = detail.communityId,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
