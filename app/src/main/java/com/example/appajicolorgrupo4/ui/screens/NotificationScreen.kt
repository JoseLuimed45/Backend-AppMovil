package com.example.appajicolorgrupo4.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.appajicolorgrupo4.data.Notificacion
import com.example.appajicolorgrupo4.data.TipoNotificacion
import com.example.appajicolorgrupo4.navigation.Screen
import com.example.appajicolorgrupo4.ui.components.AppBackground
import com.example.appajicolorgrupo4.ui.components.AppNavigationDrawer
import com.example.appajicolorgrupo4.ui.components.BottomNavigationBar
import com.example.appajicolorgrupo4.ui.components.TopBarWithCart
import com.example.appajicolorgrupo4.viewmodel.MainViewModel
import com.example.appajicolorgrupo4.viewmodel.NotificacionesViewModel
import com.example.appajicolorgrupo4.viewmodel.UsuarioViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    mainViewModel: MainViewModel,
    notificacionesViewModel: NotificacionesViewModel,
    usuarioViewModel: UsuarioViewModel // Parámetro añadido
) {
    val notificaciones by notificacionesViewModel.notificaciones.collectAsState()
    val formatoFecha = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    AppBackground {
        AppNavigationDrawer(
            drawerState = drawerState,
            scope = scope,
            currentRoute = Screen.Notification.route,
            onHomeClick = { mainViewModel.navigate(Screen.Home.route) },
            onProfileClick = { mainViewModel.navigate(Screen.Profile.route) },
            onSettingsClick = { mainViewModel.navigate(Screen.Settings.route) },
            onNotificationsClick = { /* Ya estás aquí */ },
            onCartClick = { mainViewModel.navigate(Screen.Cart.route) },
            onOrderHistoryClick = { mainViewModel.navigate(Screen.OrderHistory.route) },
            onLogoutClick = { usuarioViewModel.cerrarSesion() } // CORREGIDO
        ) {
            Scaffold(
                topBar = {
                    TopBarWithCart(
                        title = "Notificaciones",
                        scope = scope,
                        drawerState = drawerState,
                        onCartClick = { mainViewModel.navigate(Screen.Cart.route) }
                    )
                },
                bottomBar = {
                    BottomNavigationBar(
                        currentRoute = Screen.Notification.route,
                        onNavigate = { route -> mainViewModel.navigate(route) }
                    )
                },
                containerColor = androidx.compose.ui.graphics.Color.Transparent
            ) { paddingValues ->
                if (notificaciones.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(paddingValues).padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Notifications, "Sin notificaciones", Modifier.size(80.dp))
                        Spacer(Modifier.height(16.dp))
                        Text("No tienes notificaciones")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(notificaciones, key = { it.id }) { notificacion ->
                            NotificacionCard(
                                notificacion = notificacion,
                                formatoFecha = formatoFecha,
                                onClick = { notificacionesViewModel.onNotificacionClicked(notificacion) },
                                onDelete = { notificacionesViewModel.eliminarNotificacion(notificacion.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificacionCard(
    notificacion: Notificacion,
    formatoFecha: SimpleDateFormat,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    // ... (La implementación de NotificacionCard se mantiene igual)
}
