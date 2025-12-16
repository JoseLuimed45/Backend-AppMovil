package com.example.appajicolorgrupo4.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.appajicolorgrupo4.R
import com.example.appajicolorgrupo4.data.ProductoCarrito
import com.example.appajicolorgrupo4.navigation.Screen
import com.example.appajicolorgrupo4.ui.components.AppBackground
import com.example.appajicolorgrupo4.ui.components.AppNavigationDrawer
import com.example.appajicolorgrupo4.ui.components.BottomNavigationBar
import com.example.appajicolorgrupo4.ui.components.TopBarWithCart
import com.example.appajicolorgrupo4.viewmodel.CarritoViewModel
import com.example.appajicolorgrupo4.viewmodel.MainViewModel
import com.example.appajicolorgrupo4.viewmodel.UsuarioViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    carritoViewModel: CarritoViewModel,
    mainViewModel: MainViewModel,
    usuarioViewModel: UsuarioViewModel // Parámetro añadido
) {
    val productos by carritoViewModel.productos.collectAsState()
    // ... (el resto de los estados del carrito)

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    AppBackground {
        AppNavigationDrawer(
            drawerState = drawerState,
            scope = scope,
            currentRoute = Screen.Cart.route,
            onHomeClick = { mainViewModel.navigate(Screen.Home.route) },
            onProfileClick = { mainViewModel.navigate(Screen.Profile.route) },
            onSettingsClick = { mainViewModel.navigate(Screen.Settings.route) },
            onNotificationsClick = { mainViewModel.navigate(Screen.Notification.route) },
            onCartClick = { /* Ya estás aquí */ },
            onOrderHistoryClick = { mainViewModel.navigate(Screen.OrderHistory.route) },
            onLogoutClick = { usuarioViewModel.cerrarSesion() } // Parámetro añadido
        ) {
            Scaffold(
                topBar = {
                    TopBarWithCart(
                        title = "Carrito",
                        scope = scope,
                        drawerState = drawerState,
                        onCartClick = { /* Ya estás aquí */ }
                    )
                },
                bottomBar = {
                    BottomNavigationBar(
                        currentRoute = Screen.Cart.route,
                        onNavigate = { route -> mainViewModel.navigate(route) }
                    )
                },
                // ... (El resto del Scaffold no cambia)
            ) { paddingValues ->
                // ... (El contenido de la columna no cambia)
            }
        }
    }
}

// ... (El composable ProductoCarritoItem se mantiene igual)
