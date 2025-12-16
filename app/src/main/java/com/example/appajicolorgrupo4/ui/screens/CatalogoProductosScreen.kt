package com.example.appajicolorgrupo4.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.appajicolorgrupo4.data.Producto
import com.example.appajicolorgrupo4.navigation.Screen
import com.example.appajicolorgrupo4.ui.components.AppBackground
import com.example.appajicolorgrupo4.ui.components.BottomNavigationBar
import com.example.appajicolorgrupo4.ui.components.TopBarWithCart
import com.example.appajicolorgrupo4.viewmodel.MainViewModel
import com.example.appajicolorgrupo4.viewmodel.ProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoProductosScreen(
    productoViewModel: ProductoViewModel,
    mainViewModel: MainViewModel
) {
    val uiState by productoViewModel.catalogoState.collectAsState() // CORREGIDO
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    AppBackground {
        Scaffold(
            topBar = {
                TopBarWithCart(
                    title = "Catálogo",
                    scope = scope,
                    drawerState = drawerState,
                    onCartClick = { mainViewModel.navigate(Screen.Cart.route) }
                )
            },
            bottomBar = {
                BottomNavigationBar(
                    currentRoute = Screen.Catalogo.route,
                    onNavigate = { route -> mainViewModel.navigate(route) }
                )
            }
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                // ... (Filtros de categoría sin cambios, llaman a productoViewModel.onCategoriaChange)

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.productos) { producto ->
                        ProductoCard(
                            producto = producto,
                            onClick = { productoViewModel.onProductoClicked(producto) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductoCard(
    producto: Producto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        // ... (Contenido del card sin cambios)
    }
}
