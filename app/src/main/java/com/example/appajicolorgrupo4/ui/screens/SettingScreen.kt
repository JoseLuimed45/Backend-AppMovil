package com.example.appajicolorgrupo4.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.appajicolorgrupo4.navigation.Screen
import com.example.appajicolorgrupo4.ui.components.AppBackground
import com.example.appajicolorgrupo4.ui.components.AppNavigationDrawer
import com.example.appajicolorgrupo4.ui.components.BottomNavigationBar
import com.example.appajicolorgrupo4.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    mainViewModel: MainViewModel
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    AppBackground {
        AppNavigationDrawer(
            drawerState = drawerState,
            scope = scope,
            currentRoute = Screen.Settings.route,
            onHomeClick = { mainViewModel.navigate(Screen.Home.route) },
            onProfileClick = { mainViewModel.navigate(Screen.Profile.route) },
            onSettingsClick = { },
            onNotificationsClick = { mainViewModel.navigate(Screen.Notification.route) },
            onCartClick = { mainViewModel.navigate(Screen.Cart.route) },
            onOrderHistoryClick = { mainViewModel.navigate(Screen.OrderHistory.route) },
            onLogoutClick = { mainViewModel.navigate(Screen.Login.route) }
        ) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text("Configuración") },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menú")
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                    )
                },
                bottomBar = {
                    BottomNavigationBar(
                        currentRoute = Screen.Settings.route,
                        onNavigate = { route -> mainViewModel.navigate(route) }
                    )
                },
                containerColor = Color.Transparent
            ) { innerPadding ->
                Column(
                    modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Bienvenido a Configuración")
                    Spacer(Modifier.height(24.dp))
                    Button(onClick = { mainViewModel.navigate(Screen.Home.route) }) {
                        Text("Ir a Inicio")
                    }
                    Spacer(Modifier.height(32.dp))
                    Button(
                        onClick = { mainViewModel.navigate(Screen.Debug.route) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("🛠️ Modo Depuración")
                    }
                }
            }
        }
    }
}
