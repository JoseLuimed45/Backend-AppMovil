package com.example.appajicolorgrupo4.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.example.appajicolorgrupo4.navigation.Screen

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar {
        val items = listOf(
            Screen.Catalogo,
            Screen.Notification,
            Screen.Home,
            Screen.Profile,
            Screen.OrderHistory
        )

        items.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = { onNavigate(screen.route) },
                icon = { 
                    when (screen) {
                        Screen.Catalogo -> Icon(Icons.Default.Search, "Catálogo")
                        Screen.Notification -> Icon(Icons.Default.Notifications, "Alertas")
                        Screen.Home -> Icon(Icons.Default.Home, "Inicio")
                        Screen.Profile -> Icon(Icons.Default.Person, "Perfil")
                        Screen.OrderHistory -> Icon(Icons.AutoMirrored.Filled.List, "Compras")
                        else -> { /* No-op */ }
                    }
                },
                label = { 
                    when (screen) {
                        Screen.Catalogo -> Text("Catálogo")
                        Screen.Notification -> Text("Alertas")
                        Screen.Home -> Text("Inicio")
                        Screen.Profile -> Text("Perfil")
                        Screen.OrderHistory -> Text("Compras")
                        else -> { /* No-op */ }
                    }
                }
            )
        }
    }
}
