package com.example.appajicolorgrupo4.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appajicolorgrupo4.data.EstadoPedido
import com.example.appajicolorgrupo4.data.PedidoCompleto
import com.example.appajicolorgrupo4.data.session.SessionManager
import com.example.appajicolorgrupo4.navigation.Screen
import com.example.appajicolorgrupo4.ui.components.AppBackground
import com.example.appajicolorgrupo4.ui.theme.AmarilloAji
import com.example.appajicolorgrupo4.ui.theme.MoradoAji
import com.example.appajicolorgrupo4.viewmodel.MainViewModel
import com.example.appajicolorgrupo4.viewmodel.PedidosViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPedidosScreen(
    mainViewModel: MainViewModel,
    pedidosViewModel: PedidosViewModel
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    // --- Guardián de Seguridad ---
    LaunchedEffect(Unit) {
        if (!sessionManager.isAdmin()) {
            mainViewModel.navigate(
                route = Screen.Home.route, // CORREGIDO
                popUpToRoute = Screen.AdminPedidos.route, // CORREGIDO
                inclusive = true
            )
        }
    }

    val pedidos by pedidosViewModel.pedidos.collectAsState()
    
    val formatoMoneda = remember {
        NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL")).apply {
            maximumFractionDigits = 0
        }
    }
    
    val formatoFecha = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    AppBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            "Todos los Pedidos",
                            color = AmarilloAji,
                            fontWeight = FontWeight.Bold
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = { mainViewModel.navigateBack() }) {
                            Icon(Icons.Default.ArrowBack, "Volver", tint = AmarilloAji)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MoradoAji
                    )
                )
            },
            containerColor = androidx.compose.ui.graphics.Color.Transparent
        ) { paddingValues ->
            // ... (El resto de la UI se mantiene igual)
        }
    }
}

@Composable
private fun PedidoAdminCard(
    pedido: PedidoCompleto,
    formatoMoneda: NumberFormat,
    formatoFecha: SimpleDateFormat,
    onClick: () -> Unit
) {
    // ... (El card se mantiene igual)
}
