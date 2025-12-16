package com.example.appajicolorgrupo4.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.appajicolorgrupo4.ui.theme.AmarilloAji
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithCart(
    title: String,
    scope: CoroutineScope,
    drawerState: DrawerState,
    onCartClick: () -> Unit,
    additionalActions: @Composable () -> Unit = {}
) {
    TopAppBar(
        title = { Text(title, color = AmarilloAji) },
        navigationIcon = {
            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                Icon(Icons.Default.Menu, "Abrir menú")
            }
        },
        actions = {
            additionalActions()
            IconButton(onClick = onCartClick) {
                Icon(Icons.Default.ShoppingCart, "Carrito")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}
