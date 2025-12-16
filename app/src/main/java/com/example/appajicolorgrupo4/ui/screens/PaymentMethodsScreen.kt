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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appajicolorgrupo4.data.MetodoPago
import com.example.appajicolorgrupo4.ui.components.AppBackground
import com.example.appajicolorgrupo4.viewmodel.CarritoViewModel
import com.example.appajicolorgrupo4.viewmodel.MainViewModel
import com.example.appajicolorgrupo4.viewmodel.PedidosViewModel
import com.example.appajicolorgrupo4.viewmodel.UsuarioViewModel
import java.net.URLDecoder
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodsScreen(
    mainViewModel: MainViewModel,
    carritoViewModel: CarritoViewModel,
    pedidosViewModel: PedidosViewModel,
    usuarioViewModel: UsuarioViewModel,
    direccionEnvio: String?,
    telefono: String?,
    notasAdicionales: String?
) {
    var metodoSeleccionado by remember { mutableStateOf<MetodoPago?>(null) }
    var mostrarDialogoPago by remember { mutableStateOf(false) }
    var procesandoPago by remember { mutableStateOf(false) }

    val total by carritoViewModel.total.collectAsState()
    val currentUser by usuarioViewModel.currentUser.collectAsState()

    LaunchedEffect(Unit) {
        usuarioViewModel.cargarPerfil()
    }

    val metodosPago = listOf(MetodoPago.TARJETA_CREDITO, MetodoPago.TARJETA_DEBITO)
    val formatoMoneda = remember { NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL")).apply { maximumFractionDigits = 0 } }
    val direccionDecodificada = remember(direccionEnvio) { URLDecoder.decode(direccionEnvio ?: "", "UTF-8") }
    val telefonoDecodificado = remember(telefono) { URLDecoder.decode(telefono ?: "", "UTF-8") }
    val notasDecodificadas = remember(notasAdicionales) { URLDecoder.decode(notasAdicionales ?: "", "UTF-8") }

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
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
                )
            },
            containerColor = androidx.compose.ui.graphics.Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)
            ) {
                // ... (UI de Total, selección de método y botón principal sin cambios)

                // Diálogo de confirmación de pago ficticio (Ahora mucho más simple)
                if (mostrarDialogoPago) {
                    AlertDialog(
                        onDismissRequest = { if (!procesandoPago) mostrarDialogoPago = false },
                        icon = { if (!procesandoPago) Icon(Icons.Default.ShoppingCart, null, tint = MaterialTheme.colorScheme.primary) },
                        title = { Text(if (procesandoPago) "Procesando Pago..." else "Confirmar Pago") },
                        text = { /* ... (Texto del diálogo sin cambios) ... */ },
                        confirmButton = {
                            if (!procesandoPago) {
                                Button(
                                    onClick = {
                                        procesandoPago = true
                                        metodoSeleccionado?.let { metodo ->
                                            pedidosViewModel.confirmarCompraFicticia(
                                                metodoPago = metodo,
                                                direccionEnvio = direccionDecodificada,
                                                telefono = telefonoDecodificado,
                                                notasAdicionales = notasDecodificadas
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
        }
    }
}

// ... (MetodoPagoItem y obtenerDescripcionMetodo sin cambios)
