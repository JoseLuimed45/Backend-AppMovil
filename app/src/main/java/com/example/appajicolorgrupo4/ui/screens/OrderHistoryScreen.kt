package com.example.appajicolorgrupo4.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.appajicolorgrupo4.navigation.Screen
import com.example.appajicolorgrupo4.ui.components.AppBackground
import com.example.appajicolorgrupo4.ui.components.AppNavigationDrawer
import com.example.appajicolorgrupo4.ui.components.BottomNavigationBar
import com.example.appajicolorgrupo4.ui.components.TopBarWithCart
import com.example.appajicolorgrupo4.viewmodel.MainViewModel
import com.example.appajicolorgrupo4.viewmodel.PedidosViewModel
import com.example.appajicolorgrupo4.viewmodel.UsuarioViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    pedidosViewModel: PedidosViewModel,
    usuarioViewModel: UsuarioViewModel,
    mainViewModel: MainViewModel
) {
    val pedidos by pedidosViewModel.pedidos.collectAsState()
    val usuarioState by usuarioViewModel.uiState.collectAsState()

    // Carga los pedidos del usuario una vez que el ID de mongo esté disponible.
    LaunchedEffect(usuarioState.currentUser?.mongoId) {
        usuarioState.currentUser?.mongoId?.let { userId ->
            pedidosViewModel.cargarPedidosUsuario(userId)
        }
    }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    AppBackground {
        AppNavigationDrawer(
            drawerState = drawerState,
            scope = scope,
            currentRoute = Screen.OrderHistory.route,
            onHomeClick = { mainViewModel.navigate(Screen.Home.route) },
            onProfileClick = { mainViewModel.navigate(Screen.Profile.route) },
            onSettingsClick = { mainViewModel.navigate(Screen.Settings.route) },
            onNotificationsClick = { mainViewModel.navigate(Screen.Notification.route) },
            onCartClick = { mainViewModel.navigate(Screen.Cart.route) },
            onOrderHistoryClick = { /* Ya estás aquí */ },
            onLogoutClick = { usuarioViewModel.cerrarSesion() }
        ) {
            Scaffold(
                topBar = {
                    TopBarWithCart(
                        title = "Mis Pedidos",
                        scope = scope,
                        drawerState = drawerState,
                        onCartClick = { mainViewModel.navigate(Screen.Cart.route) }
                    )
                },
                bottomBar = {
                    BottomNavigationBar(
                        currentRoute = Screen.OrderHistory.route,
                        onNavigate = { route -> mainViewModel.navigate(route) }
                    )
                },
                containerColor = Color.Transparent
            ) { innerPadding ->
                if (pedidos.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = "Sin pedidos",
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Aún no tienes pedidos",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(pedidos) { pedido ->
                            // Aquí iría el Composable para mostrar cada item del historial de pedidos.
                            // Por ejemplo: PedidoHistorialCard(pedido = pedido)
                            Text("Pedido: ${pedido.numeroPedido}")
                        }
                    }
                }
            }
        }
    }
}