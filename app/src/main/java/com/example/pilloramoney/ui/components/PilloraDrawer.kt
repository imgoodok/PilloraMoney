package com.example.pilloramoney.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
import com.example.pilloramoney.navigation.Screen
import com.example.pilloramoney.ui.theme.*

@Composable
fun PilloraDrawer(
    onNavigate: (Any) -> Unit,
    closeDrawer: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.background,
        drawerContentColor = MaterialTheme.colorScheme.onBackground,
        drawerShape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
    ) {
        // Drawer Header (Native Profile Card style)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                .padding(top = 48.dp, bottom = 24.dp, start = 24.dp, end = 24.dp)
        ) {
            Column {
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "P",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Pillora Money",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Gestão Financeira Pro",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Sections
        DrawerSectionHeader("PRINCIPAL")
        DrawerItem(Icons.Default.Dashboard, "Dashboard") {
            onNavigate(Screen.Home)
            closeDrawer()
        }
        DrawerItem(Icons.Default.CalendarMonth, "Projeção Diária") {
            onNavigate(Screen.Spreadsheet)
            closeDrawer()
        }
        DrawerItem(Icons.AutoMirrored.Filled.TrendingUp, "Horizonte de Saldo") {
            onNavigate(Screen.BalanceHorizon)
            closeDrawer()
        }

        Spacer(modifier = Modifier.height(8.dp))
        DrawerSectionHeader("FERRAMENTAS")
        DrawerItem(Icons.Default.AddCircle, "Novo Lançamento") {
            onNavigate(Screen.AddTransaction)
            closeDrawer()
        }
        DrawerItem(Icons.Default.Calculate, "Calculadora") {
            onNavigate(Screen.Calculator)
            closeDrawer()
        }
        DrawerItem(Icons.Default.Category, "Categorias") {
            /* Categories */
        }

        Spacer(modifier = Modifier.weight(1f))
        
        HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))
        
        DrawerItem(
            icon = Icons.AutoMirrored.Filled.Logout,
            label = "Sair",
            color = MaterialTheme.colorScheme.error
        ) {
            /* Logout */
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun DrawerSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 8.dp)
    )
}

@Composable
fun DrawerItem(
    icon: ImageVector,
    label: String,
    color: Color = Color.Unspecified,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        icon = { Icon(icon, contentDescription = null, tint = if (color != Color.Unspecified) color else MaterialTheme.colorScheme.onSurfaceVariant) },
        label = { Text(label, color = if (color != Color.Unspecified) color else MaterialTheme.colorScheme.onSurface) },
        selected = false,
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent
        )
    )
}
