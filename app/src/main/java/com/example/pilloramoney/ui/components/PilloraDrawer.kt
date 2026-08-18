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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pilloramoney.R
import androidx.compose.ui.unit.sp
import com.example.pilloramoney.navigation.Screen
import com.example.pilloramoney.ui.theme.*

@Composable
fun PilloraDrawer(
    userEmail: String,
    onNavigate: (Any) -> Unit,
    closeDrawer: () -> Unit,
    onLogout: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(250.dp), // Thinner drawer
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
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = if (userEmail.isNotEmpty()) userEmail.take(1).uppercase() else "P",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (userEmail.isNotEmpty()) userEmail.split("@")[0] else "Pillora Money",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = userEmail.ifEmpty { "Gestão Financeira Pro" },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Sections
        DrawerSectionHeader(stringResource(R.string.drawer_main))
        DrawerItem(Icons.Default.Dashboard, stringResource(R.string.drawer_totals)) {
            onNavigate(Screen.Home)
            closeDrawer()
        }
        DrawerItem(Icons.Default.CalendarMonth, stringResource(R.string.drawer_projection)) {
            onNavigate(Screen.Spreadsheet)
            closeDrawer()
        }
        DrawerItem(Icons.AutoMirrored.Filled.TrendingUp, stringResource(R.string.drawer_balance_horizon)) {
            onNavigate(Screen.BalanceHorizon)
            closeDrawer()
        }

        Spacer(modifier = Modifier.height(8.dp))
        DrawerSectionHeader(stringResource(R.string.drawer_tools))
        DrawerItem(Icons.Default.AddCircle, stringResource(R.string.drawer_transactions)) {
            onNavigate(Screen.AddTransaction)
            closeDrawer()
        }
        DrawerItem(Icons.Default.Calculate, stringResource(R.string.drawer_calculator)) {
            onNavigate(Screen.Calculator)
            closeDrawer()
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        DrawerSectionHeader(stringResource(R.string.drawer_system))
        DrawerItem(Icons.Default.Star, stringResource(R.string.drawer_subscription)) {
            onNavigate(Screen.Subscription)
            closeDrawer()
        }
        DrawerItem(Icons.Default.Settings, stringResource(R.string.drawer_settings)) {
            onNavigate(Screen.Settings)
            closeDrawer()
        }

        Spacer(modifier = Modifier.weight(1f))
        
        HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))
        
        DrawerItem(
            icon = Icons.AutoMirrored.Filled.Logout,
            label = stringResource(R.string.drawer_logout),
            color = MaterialTheme.colorScheme.error
        ) {
            onLogout()
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
