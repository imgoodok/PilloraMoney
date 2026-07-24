package com.example.pilloramoney.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun PilloraBottomBar(
    currentRoute: Any?,
    onNavigate: (Any) -> Unit,
    onFabClick: () -> Unit
) {
    // We use a Box to stack the BottomAppBar and the elevated FAB
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        BottomAppBar(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 8.dp,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Button 1: Home
                BottomNavItem(
                    icon = Icons.Default.Home,
                    label = "Home",
                    isSelected = currentRoute?.toString()?.contains("Home") == true,
                    onClick = { onNavigate(com.example.pilloramoney.navigation.Screen.Home) }
                )

                // Button 2: Planilha
                BottomNavItem(
                    icon = Icons.Default.TableChart,
                    label = "Planilha",
                    isSelected = currentRoute?.toString()?.contains("Spreadsheet") == true,
                    onClick = { onNavigate(com.example.pilloramoney.navigation.Screen.Spreadsheet) }
                )

                // Space for FAB (Middle)
                Spacer(modifier = Modifier.width(64.dp))

                // Button 4: Calculadora
                BottomNavItem(
                    icon = Icons.Default.Calculate,
                    label = "Calculadora",
                    isSelected = currentRoute?.toString()?.contains("Calculator") == true,
                    onClick = { onNavigate(com.example.pilloramoney.navigation.Screen.Calculator) }
                )

                // Button 5: Perfil/Outro (User didn't specify, I'll use Settings or similar)
                BottomNavItem(
                    icon = Icons.Default.Person,
                    label = "Perfil",
                    isSelected = currentRoute?.toString()?.contains("Settings") == true,
                    onClick = { onNavigate(com.example.pilloramoney.navigation.Screen.Settings) }
                )
            }
        }

        // The Central Circular Button (FAB)
        FloatingActionButton(
            onClick = onFabClick,
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .offset(y = (-20).dp) // This makes it pop out upwards
                .size(64.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Adicionar", tint = Color.White)
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
        icon = { Icon(icon, contentDescription = label) },
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}
