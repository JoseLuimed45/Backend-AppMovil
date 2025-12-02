package com.example.appajicolorgrupo4.ui.screens

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
import androidx.navigation.NavController
import com.example.appajicolorgrupo4.data.models.Product
import com.example.appajicolorgrupo4.data.remote.RetrofitInstance
import com.example.appajicolorgrupo4.data.repository.ProductRepository
import com.example.appajicolorgrupo4.ui.components.AppBackground
import com.example.appajicolorgrupo4.ui.theme.AmarilloAji
import com.example.appajicolorgrupo4.ui.theme.MoradoAji
import com.example.appajicolorgrupo4.viewmodel.AdminProductViewModel
import com.example.appajicolorgrupo4.viewmodel.AdminProductViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductosScreen(navController: NavController) {
    val context = LocalContext.current
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
                    title = { Text("Administración de Productos", color = AmarilloAji) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, "Volver", tint = AmarilloAji)
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.cargarProductos() }) {
                            Icon(Icons.Default.Refresh, "Recargar", tint = AmarilloAji)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MoradoAji,
                        titleContentColor = AmarilloAji
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = AmarilloAji,
                    contentColor = MoradoAji
                ) {
                    Icon(Icons.Default.Add, "Agregar Producto")
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = androidx.compose.ui.graphics.Color.Transparent
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                when {
                    isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = AmarilloAji)
                    productos.isEmpty() -> {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.ShoppingCart, null, modifier = Modifier.size(64.dp), tint = MoradoAji)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("No hay productos", style = MaterialTheme.typography.titleLarge, color = MoradoAji)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { showAddDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = AmarilloAji, contentColor = MoradoAji)
                            ) {
                                Icon(Icons.Default.Add, null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Agregar Producto")
                            }
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(productos) { producto ->
                                ProductoAdminCard(
                                    producto = producto,
                                    onEdit = { selectedProduct = producto; showEditDialog = true },
                                    onDelete = { selectedProduct = producto; showDeleteDialog = true }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        ProductoFormDialog(
            title = "Agregar Producto",
            onDismiss = { showAddDialog = false },
            onConfirm = { nombre, descripcion, precio, categoria, stock ->
                viewModel.crearProducto(nombre, descripcion, precio, categoria, stock)
                showAddDialog = false
            }
        )
    }

    if (showEditDialog && selectedProduct != null) {
        ProductoFormDialog(
            title = "Editar Producto",
            producto = selectedProduct,
            onDismiss = { showEditDialog = false; selectedProduct = null },
            onConfirm = { nombre, descripcion, precio, categoria, stock ->
                selectedProduct?.let { viewModel.actualizarProducto(it.id, nombre, descripcion, precio, categoria, stock) }
                showEditDialog = false; selectedProduct = null
            }
        )
    }

    if (showDeleteDialog && selectedProduct != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false; selectedProduct = null },
            title = { Text("Confirmar Eliminación", color = MoradoAji) },
            text = { Text("¿Eliminar '${selectedProduct?.nombre}'?", color = MoradoAji) },
            confirmButton = {
                Button(
                    onClick = { selectedProduct?.let { viewModel.eliminarProducto(it.id) }; showDeleteDialog = false; selectedProduct = null },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false; selectedProduct = null }) {
                    Text("Cancelar", color = MoradoAji)
                }
            },
            containerColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.95f)
        )
    }
}

@Composable
private fun ProductoAdminCard(producto: Product, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(producto.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MoradoAji)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(producto.descripcion, style = MaterialTheme.typography.bodySmall, color = MoradoAji.copy(alpha = 0.7f), maxLines = 2)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Default.Edit, "Editar", tint = AmarilloAji)
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Default.Delete, "Eliminar", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Precio", style = MaterialTheme.typography.labelSmall, color = MoradoAji.copy(alpha = 0.6f))
                    Text("$$${producto.precio}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MoradoAji)
                }
                Column {
                    Text("Stock", style = MaterialTheme.typography.labelSmall, color = MoradoAji.copy(alpha = 0.6f))
                    Text("${producto.stock} unidades", style = MaterialTheme.typography.bodyMedium, color = MoradoAji)
                }
                Column {
                    Text("Categoría", style = MaterialTheme.typography.labelSmall, color = MoradoAji.copy(alpha = 0.6f))
                    Text(producto.categoria, style = MaterialTheme.typography.bodyMedium, color = MoradoAji)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductoFormDialog(
    title: String,
    producto: Product? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int, String, Int) -> Unit
) {
    var nombre by remember { mutableStateOf(producto?.nombre ?: "") }
    var descripcion by remember { mutableStateOf(producto?.descripcion ?: "") }
    var precioText by remember { mutableStateOf(producto?.precio?.toString() ?: "") }
    var categoria by remember { mutableStateOf(producto?.categoria ?: "Serigrafía") }
    var stockText by remember { mutableStateOf(producto?.stock?.toString() ?: "100") }
    var expanded by remember { mutableStateOf(false) }

    val categorias = listOf("Serigrafía", "DTF", "Corporativa", "Accesorios")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, color = MoradoAji, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MoradoAji, unfocusedBorderColor = MoradoAji.copy(alpha = 0.5f))
                )
                OutlinedTextField(
                    value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(), minLines = 3, maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MoradoAji, unfocusedBorderColor = MoradoAji.copy(alpha = 0.5f))
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = precioText, onValueChange = { if (it.all { char -> char.isDigit() }) precioText = it },
                        label = { Text("Precio") }, modifier = Modifier.weight(1f), singleLine = true, prefix = { Text("$") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MoradoAji)
                    )
                    OutlinedTextField(
                        value = stockText, onValueChange = { if (it.all { char -> char.isDigit() }) stockText = it },
                        label = { Text("Stock") }, modifier = Modifier.weight(1f), singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MoradoAji)
                    )
                }
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = categoria, onValueChange = {}, readOnly = true, label = { Text("Categoría") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MoradoAji)
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        categorias.forEach { cat ->
                            DropdownMenuItem(text = { Text(cat) }, onClick = { categoria = cat; expanded = false })
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val precio = precioText.toIntOrNull() ?: 0
                    val stock = stockText.toIntOrNull() ?: 0
                    if (nombre.isNotBlank() && descripcion.isNotBlank() && precio > 0) {
                        onConfirm(nombre, descripcion, precio, categoria, stock)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AmarilloAji, contentColor = MoradoAji),
                border = BorderStroke(2.dp, MoradoAji)
            ) { Text(if (producto == null) "Crear" else "Actualizar", fontWeight = FontWeight.Bold) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar", color = MoradoAji) } },
        containerColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.95f)
    )
}

