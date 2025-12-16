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
import com.example.appajicolorgrupo4.data.local.user.UserEntity
import com.example.appajicolorgrupo4.data.session.SessionManager
import com.example.appajicolorgrupo4.navigation.Screen
import com.example.appajicolorgrupo4.ui.components.AppBackground
import com.example.appajicolorgrupo4.ui.theme.AmarilloAji
import com.example.appajicolorgrupo4.ui.theme.MoradoAji
import com.example.appajicolorgrupo4.viewmodel.MainViewModel
import com.example.appajicolorgrupo4.viewmodel.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsuariosScreen(
    mainViewModel: MainViewModel,
    usuarioViewModel: UsuarioViewModel
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    // --- Guardián de Seguridad ---
    LaunchedEffect(Unit) {
        if (!sessionManager.isAdmin()) {
            mainViewModel.navigate(
                route = Screen.Home.route, // CORREGIDO
                popUpToRoute = Screen.AdminUsuarios.route, // CORREGIDO
                inclusive = true
            )
        }
    }

    var usuarios by remember { mutableStateOf<List<UserEntity>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        // TODO: Implementar en ViewModel la carga de todos los usuarios.
        kotlinx.coroutines.delay(500)
        isLoading = false
    }

    AppBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            "Usuarios Registrados",
                            color = AmarilloAji,
                            fontWeight = FontWeight.Bold
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = { mainViewModel.navigateBack() }) {
                            Icon(Icons.Default.ArrowBack, "Volver", tint = AmarilloAji)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MoradoAji)
                )
            },
            containerColor = androidx.compose.ui.graphics.Color.Transparent
        ) { paddingValues ->
            // ... (El resto de la UI se mantiene igual)
        }
    }
}

@Composable
private fun UsuarioCard(usuario: UserEntity) {
    // ... (El card se mantiene igual)
}
