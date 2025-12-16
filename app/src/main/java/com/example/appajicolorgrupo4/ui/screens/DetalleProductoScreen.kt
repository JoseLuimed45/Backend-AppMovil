package com.example.appajicolorgrupo4.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.appajicolorgrupo4.viewmodel.CarritoViewModel
import com.example.appajicolorgrupo4.viewmodel.MainViewModel
import com.example.appajicolorgrupo4.viewmodel.ProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
    productoId: String,
    productoViewModel: ProductoViewModel,
    carritoViewModel: CarritoViewModel,
    mainViewModel: MainViewModel
) {
    LaunchedEffect(productoId) {
        productoViewModel.onProductoSelected(productoId)
    }

    val uiState by productoViewModel.detalleState.collectAsState()
    val producto = uiState.producto

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.showSnackbar) {
        if (uiState.showSnackbar) {
            val result = snackbarHostState.showSnackbar(
                message = "Producto agregado al carrito",
                actionLabel = "Ver Carrito",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                productoViewModel.onViewCart()
            }
            productoViewModel.onSnackbarDismissed()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(producto?.nombre ?: "Detalle") },
                navigationIcon = { 
                    IconButton(onClick = { mainViewModel.navigateBack() }) { 
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (producto == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) { /* ... UI de carga o error ... */ }
        } else {
            LazyColumn(modifier = Modifier.padding(paddingValues)) {
                // ... Contenido del detalle del producto que lee de `uiState`

                item {
                    Button(
                        onClick = { productoViewModel.onAddToCart(carritoViewModel) },
                        enabled = producto.stock > 0
                    ) {
                        Text("Agregar al Carrito")
                    }
                }
            }
        }
    }
}
