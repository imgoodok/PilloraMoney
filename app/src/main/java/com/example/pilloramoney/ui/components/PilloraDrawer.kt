package com.example.pilloramoney.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PilloraDrawer(
    onNavigate: (Any) -> Unit,
    closeDrawer: () -> Unit
) {
    ModalDrawerSheet {
        // Drawer Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "P",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Pillora Money",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "Seu controle financeiro nativo",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Drawer Items
        DrawerItem(
            icon = Icons.Default.Dashboard,
            label = "Dashboard",
            onClick = {
                onNavigate(com.example.pilloramoney.navigation.Screen.Home)
                closeDrawer()
            }
        )
        DrawerItem(
            icon = Icons.Default.TableRows,
            label = "Planilha Mensal",
            onClick = {
                onNavigate(com.example.pilloramoney.navigation.Screen.Spreadsheet)
                closeDrawer()
            }
        )
        DrawerItem(
            icon = Icons.Default.Calculate,
            label = "Calculadora de Gastos",
            onClick = {
                onNavigate(com.example.pilloramoney.navigation.Screen.Calculator)
                closeDrawer()
            }
        )
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        
        DrawerItem(
            icon = Icons.Default.Settings,
            label = "Configurações",
            onClick = {
                onNavigate(com.example.pilloramoney.navigation.Screen.Settings)
                closeDrawer()
            }
        )
    }
}

@Composable
fun DrawerItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        icon = { Icon(icon, contentDescription = null) },
        label = { Text(label) },
        selected = false,
        onClick = onClick,
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
}
