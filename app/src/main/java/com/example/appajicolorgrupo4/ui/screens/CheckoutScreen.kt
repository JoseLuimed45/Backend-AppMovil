package com.example.appajicolorgrupo4.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.appajicolorgrupo4.navigation.Screen
import com.example.appajicolorgrupo4.ui.components.AppBackground
import com.example.appajicolorgrupo4.viewmodel.CarritoViewModel
import com.example.appajicolorgrupo4.viewmodel.MainViewModel
import com.example.appajicolorgrupo4.viewmodel.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    mainViewModel: MainViewModel,
    carritoViewModel: CarritoViewModel,
    usuarioViewModel: UsuarioViewModel
) {
    val productos by carritoViewModel.productos.collectAsState()
    val uiState by usuarioViewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        usuarioViewModel.cargarPerfil()
    }

    AppBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Confirmar Pedido") },
                    navigationIcon = {
                        IconButton(onClick = { mainViewModel.navigateBack() }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues)
            ) {
                // ... (El contenido de la pantalla no cambia)

                item {
                    Button(
                        onClick = { mainViewModel.navigate(Screen.PaymentMethods.route) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = uiState.direccion.isNotBlank() && uiState.telefono.isNotBlank() && productos.isNotEmpty()
                    ) {
                        Text("Seleccionar Método de Pago")
                    }
                }
            }
        }
    }
}
