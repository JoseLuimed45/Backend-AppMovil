package com.example.appajicolorgrupo4.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.example.appajicolorgrupo4.viewmodel.MainViewModel
import com.example.appajicolorgrupo4.viewmodel.PedidosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallePedidoScreen(
    mainViewModel: MainViewModel,
    pedidosViewModel: PedidosViewModel,
    numeroPedido: String
) {
    // Lógica para cargar el detalle del pedido cuando el numeroPedido cambia
    LaunchedEffect(numeroPedido) {
        // En el futuro, aquí llamarías a una función en tu ViewModel:
        // pedidosViewModel.cargarDetalleDePedido(numeroPedido)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Pedido") },
                navigationIcon = {
                    IconButton(onClick = { mainViewModel.navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            // Aquí iría la UI que muestra los detalles del pedido,
            // leyendo el estado desde el `pedidosViewModel`.
            Text(text = "Mostrando detalle del pedido: $numeroPedido")
        }
    }
}
