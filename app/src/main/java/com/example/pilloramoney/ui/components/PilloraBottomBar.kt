package com.example.pilloramoney.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.pilloramoney.navigation.Screen

@Composable
fun PilloraBottomBar(
    currentRoute: String?,
    onNavigate: (Any) -> Unit,
    onFabClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        NavigationBar(
            modifier = Modifier.fillMaxWidth().height(58.dp), // Slimmer
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            windowInsets = WindowInsets(0.dp) // Important to avoid extra padding
        ) {
            // Screen.Home
            BottomNavItem(
                icon = Icons.Default.Dashboard,
                label = "Totais!",
                isSelected = currentRoute?.contains("Home") == true,
                onClick = { onNavigate(Screen.Home) }
            )

            // Screen.Spreadsheet
            BottomNavItem(
                icon = Icons.Default.CalendarMonth,
                label = "Projeção",
                isSelected = currentRoute?.contains("Spreadsheet") == true,
                onClick = { onNavigate(Screen.Spreadsheet) }
            )

            // Middle Gap for FAB
            Spacer(modifier = Modifier.weight(1f))

            // Screen.Community
            BottomNavItem(
                icon = Icons.Default.Groups,
                label = "Comunidade",
                isSelected = currentRoute?.contains("Community") == true,
                onClick = { onNavigate(Screen.Community) }
            )

            // Screen.BalanceHorizon
            BottomNavItem(
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                label = "Horizonte",
                isSelected = currentRoute?.contains("BalanceHorizon") == true,
                onClick = { onNavigate(Screen.BalanceHorizon) }
            )
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = onFabClick,
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            modifier = Modifier
                .offset(y = (-14).dp) // Lower than before (-28)
                .size(51.dp), // Slightly smaller
            elevation = FloatingActionButtonDefaults.elevation(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Adicionar",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun RowScope.BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = isSelected,
        onClick = onClick,
        icon = { 
            Icon(
                icon, 
                contentDescription = label,
                modifier = Modifier.offset(y = (0).dp) // Slight lift
            ) 
        },
        label = null,
        alwaysShowLabel = false,
        colors = NavigationBarItemDefaults.colors(
            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
            selectedIconColor = MaterialTheme.colorScheme.primary,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}
