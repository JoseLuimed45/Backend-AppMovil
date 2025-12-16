package com.example.appajicolorgrupo4.ui.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appajicolorgrupo4.data.models.Product
import com.example.appajicolorgrupo4.data.remote.RetrofitInstance
import com.example.appajicolorgrupo4.data.repository.ProductRepository
import com.example.appajicolorgrupo4.data.session.SessionManager
import com.example.appajicolorgrupo4.navigation.Screen
import com.example.appajicolorgrupo4.ui.components.AppBackground
import com.example.appajicolorgrupo4.viewmodel.AdminProductViewModel
import com.example.appajicolorgrupo4.viewmodel.AdminProductViewModelFactory
import com.example.appajicolorgrupo4.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductosScreen(
    mainViewModel: MainViewModel
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    // --- Guardián de Seguridad ---
    LaunchedEffect(Unit) {
        if (!sessionManager.isAdmin()) {
            mainViewModel.navigate(
                route = Screen.Home.route, // CORREGIDO
                popUpToRoute = Screen.AdminProductos.route, // CORREGIDO
                inclusive = true
            )
        }
    }

    val repository = remember { ProductRepository(RetrofitInstance.api) }
    val viewModel: AdminProductViewModel = viewModel(factory = AdminProductViewModelFactory(repository))

    val productos by viewModel.productos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSuccessMessage()
        }
    }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar("Error: $it")
            viewModel.clearError()
        }
    }

    AppBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Administración de Productos", color = MaterialTheme.colorScheme.onPrimary) },
                    actions = {
                        IconButton(onClick = { mainViewModel.navigate(Screen.AdminPedidos.route) }) {
                            Icon(Icons.Default.ShoppingCart, "Ver Pedidos", tint = MaterialTheme.colorScheme.onPrimary)
                        }
                        IconButton(onClick = { mainViewModel.navigate(Screen.AdminUsuarios.route) }) {
                            Icon(Icons.Default.Person, "Ver Usuarios", tint = MaterialTheme.colorScheme.onPrimary)
                        }
                        IconButton(onClick = { viewModel.cargarProductos() }) {
                            Icon(Icons.Default.Refresh, "Recargar", tint = MaterialTheme.colorScheme.onPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) { Icon(Icons.Default.Add, "Agregar Producto") }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = androidx.compose.ui.graphics.Color.Transparent
        ) { paddingValues ->
            // ... (El resto de la UI se mantiene igual)
        }
    }

    // ... (Los diálogos se mantienen igual)
}

// ... (ProductoAdminCard y ProductoFormDialog se mantienen igual)
