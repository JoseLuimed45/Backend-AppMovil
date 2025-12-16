package com.example.appajicolorgrupo4.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appajicolorgrupo4.navigation.Screen
import com.example.appajicolorgrupo4.ui.components.*
import com.example.appajicolorgrupo4.viewmodel.MainViewModel
import com.example.appajicolorgrupo4.viewmodel.UsuarioViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    mainViewModel: MainViewModel,
    usuarioViewModel: UsuarioViewModel
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val productosDestacados = remember { emptyList<ProductoCarousel>() }
    val productosNuevos = remember { emptyList<ProductoCarousel>() }

    AppBackground {
        AppNavigationDrawer(
            drawerState = drawerState,
            scope = scope,
            currentRoute = Screen.Home.route,
            onHomeClick = { scope.launch { drawerState.close() } },
            onProfileClick = { mainViewModel.navigate(Screen.Profile.route) },
            onSettingsClick = { mainViewModel.navigate(Screen.Settings.route) },
            onNotificationsClick = { mainViewModel.navigate(Screen.Notification.route) },
            onCartClick = { mainViewModel.navigate(Screen.Cart.route) },
            onOrderHistoryClick = { mainViewModel.navigate(Screen.OrderHistory.route) },
            onLogoutClick = { usuarioViewModel.cerrarSesion() }
        ) {
            Scaffold(
                topBar = {
                    TopBarWithCart(
                        title = "Inicio",
                        scope = scope,
                        drawerState = drawerState,
                        onCartClick = { mainViewModel.navigate(Screen.Cart.route) }
                    )
                },
                bottomBar = {
                    BottomNavigationBar(
                        currentRoute = Screen.Home.route,
                        onNavigate = { route -> mainViewModel.navigate(route) }
                    )
                },
                containerColor = Color.Transparent
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "¡Bienvenido a Aji Color!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                    )

                    CarouselProductosCompacto(
                        productos = productosDestacados,
                        titulo = "Productos Destacados",
                        onProductClick = { producto ->
                            mainViewModel.navigate(Screen.DetalleProducto.createRoute(producto.id))
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CarouselProductosCompacto(
                        productos = productosNuevos,
                        titulo = "Nuevos Productos",
                        onProductClick = { producto ->
                            mainViewModel.navigate(Screen.DetalleProducto.createRoute(producto.id))
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { mainViewModel.navigate(Screen.Catalogo.route) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Ver Todo el Catálogo")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
