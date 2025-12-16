package com.example.appajicolorgrupo4.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appajicolorgrupo4.navigation.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AppNavigationDrawer(
    drawerState: DrawerState,
    scope: CoroutineScope,
    currentRoute: String,
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onCartClick: () -> Unit,
    onOrderHistoryClick: () -> Unit,
    onLogoutClick: () -> Unit, // PARÁMETRO AÑADIDO
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Inicio") },
                    selected = currentRoute == Screen.Home.route,
                    onClick = { scope.launch { drawerState.close() }; onHomeClick() }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Perfil") },
                    selected = currentRoute == Screen.Profile.route,
                    onClick = { scope.launch { drawerState.close() }; onProfileClick() }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, null) },
                    label = { Text("Ajustes") },
                    selected = currentRoute == Screen.Settings.route,
                    onClick = { scope.launch { drawerState.close() }; onSettingsClick() }
                )
                Divider(Modifier.padding(vertical = 12.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Notifications, null) },
                    label = { Text("Notificaciones") },
                    selected = currentRoute == Screen.Notification.route,
                    onClick = { scope.launch { drawerState.close() }; onNotificationsClick() }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.ShoppingCart, null) },
                    label = { Text("Carrito") },
                    selected = currentRoute == Screen.Cart.route,
                    onClick = { scope.launch { drawerState.close() }; onCartClick() }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.List, null) },
                    label = { Text("Mis Pedidos") },
                    selected = currentRoute == Screen.OrderHistory.route,
                    onClick = { scope.launch { drawerState.close() }; onOrderHistoryClick() }
                )
                Spacer(Modifier.weight(1f))
                NavigationDrawerItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, null) },
                    label = { Text("Cerrar Sesión") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() }; onLogoutClick() }
                )
                Spacer(Modifier.height(12.dp))
            }
        },
        content = content
    )
}
