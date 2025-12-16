package com.example.appajicolorgrupo4.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appajicolorgrupo4.data.MetodoPago
import com.example.appajicolorgrupo4.ui.components.AppBackground
import com.example.appajicolorgrupo4.viewmodel.CarritoViewModel
import com.example.appajicolorgrupo4.viewmodel.MainViewModel
import com.example.appajicolorgrupo4.viewmodel.PedidosViewModel
import com.example.appajicolorgrupo4.viewmodel.UsuarioViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodsScreen(
    mainViewModel: MainViewModel,
    carritoViewModel: CarritoViewModel,
    pedidosViewModel: PedidosViewModel,
    usuarioViewModel: UsuarioViewModel
) {
    var metodoSeleccionado by remember { mutableStateOf<MetodoPago?>(null) }
    var mostrarDialogoPago by remember { mutableStateOf(false) }
    var procesandoPago by remember { mutableStateOf(false) }

    val total by carritoViewModel.total.collectAsState()
    val usuarioState by usuarioViewModel.uiState.collectAsState() // CORREGIDO: Observar el único uiState

    val metodosPago = listOf(MetodoPago.TARJETA_CREDITO, MetodoPago.TARJETA_DEBITO)
    val formatoMoneda = remember { NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL")).apply { maximumFractionDigits = 0 } }

    AppBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Método de Pago") },
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
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)
            ) {
                // ... (UI de Total, selección de método, etc. se mantiene igual)

                // Botón confirmar pago
                Button(
                    onClick = {
                        if (metodoSeleccionado != null && usuarioState.currentUser != null) { // CORREGIDO
                            mostrarDialogoPago = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = metodoSeleccionado != null && usuarioState.currentUser != null && !procesandoPago // CORREGIDO
                ) {
                    if (procesandoPago) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Text("Confirmar Pago", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }

    // Diálogo de confirmación de pago ficticio
    if (mostrarDialogoPago) {
        AlertDialog(
            onDismissRequest = { if (!procesandoPago) mostrarDialogoPago = false },
            title = { Text(if (procesandoPago) "Procesando Pago..." else "Confirmar Pago") },
            text = { Text("Este es un proceso de pago ficticio con fines demostrativos.") },
            confirmButton = {
                if (!procesandoPago) {
                    Button(
                        onClick = {
                            procesandoPago = true
                            metodoSeleccionado?.let { metodo ->
                                pedidosViewModel.confirmarCompraFicticia(
                                    metodoPago = metodo,
                                    direccionEnvio = usuarioState.direccion,
                                    telefono = usuarioState.telefono,
                                    notasAdicionales = ""
                                )
                            }
                        }
                    ) {
                        Text("Confirmar Pago Ficticio")
                    }
                }
            },
            dismissButton = {
                if (!procesandoPago) {
                    TextButton(onClick = { mostrarDialogoPago = false }) {
                        Text("Cancelar")
                    }
                }
            }
        )
    }
}
