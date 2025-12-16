package com.example.appajicolorgrupo4.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.appajicolorgrupo4.R
import com.example.appajicolorgrupo4.navigation.Screen
import com.example.appajicolorgrupo4.ui.components.*
import com.example.appajicolorgrupo4.viewmodel.MainViewModel
import com.example.appajicolorgrupo4.viewmodel.UsuarioViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    mainViewModel: MainViewModel,
    usuarioViewModel: UsuarioViewModel
) {
    // Carga el perfil una sola vez al entrar en la pantalla
    LaunchedEffect(Unit) {
        usuarioViewModel.cargarPerfil()
    }

    val uiState by usuarioViewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Snackbar para mostrar el resultado de la actualización
    LaunchedEffect(uiState.updateMessage) {
        if (uiState.updateMessage != null) {
            // Aquí se podría mostrar un Snackbar
            kotlinx.coroutines.delay(3000) // Simula el tiempo que se muestra el mensaje
            usuarioViewModel.limpiarMensajeActualizacion()
        }
    }

    AppBackground {
        AppNavigationDrawer(
            drawerState = drawerState,
            scope = scope,
            currentRoute = Screen.Profile.route,
            onHomeClick = { mainViewModel.navigate(Screen.Home.route) },
            onProfileClick = { /* Ya estás aquí */ },
            onSettingsClick = { mainViewModel.navigate(Screen.Settings.route) },
            onNotificationsClick = { mainViewModel.navigate(Screen.Notification.route) },
            onCartClick = { mainViewModel.navigate(Screen.Cart.route) },
            onOrderHistoryClick = { mainViewModel.navigate(Screen.OrderHistory.route) },
            onLogoutClick = { usuarioViewModel.cerrarSesion() }
        ) {
            Scaffold(
                topBar = {
                    TopBarWithCart(
                        title = "Mi Perfil",
                        scope = scope,
                        drawerState = drawerState,
                        onCartClick = { mainViewModel.navigate(Screen.Cart.route) }
                    )
                },
                bottomBar = {
                    BottomNavigationBar(
                        currentRoute = Screen.Profile.route,
                        onNavigate = { route -> mainViewModel.navigate(route) }
                    )
                },
                containerColor = Color.Transparent
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (uiState.currentUser == null) {
                        CircularProgressIndicator()
                    } else {
                        ProfileImageSelector(
                            defaultImageRes = R.drawable.profile,
                            onImageSelected = { uri -> usuarioViewModel.guardarFotoPerfil(uri) },
                            currentImageUri = uiState.profileImageUri
                        )
                        Spacer(Modifier.height(16.dp))

                        OutlinedTextField(
                            value = uiState.nombre,
                            onValueChange = { if (uiState.isEditMode) usuarioViewModel.onNombreChange(it) },
                            label = { Text("Nombre") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = uiState.isEditMode
                        )
                        OutlinedTextField(
                            value = uiState.correo,
                            onValueChange = { if (uiState.isEditMode) usuarioViewModel.onCorreoChange(it) },
                            label = { Text("Correo") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = uiState.isEditMode
                        )
                        OutlinedTextField(
                            value = uiState.telefono,
                            onValueChange = { if (uiState.isEditMode) usuarioViewModel.onTelefonoChange(it) },
                            label = { Text("Teléfono") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = uiState.isEditMode
                        )
                        OutlinedTextField(
                            value = uiState.direccion,
                            onValueChange = { if (uiState.isEditMode) usuarioViewModel.onDireccionChange(it) },
                            label = { Text("Dirección") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = uiState.isEditMode
                        )

                        Spacer(Modifier.height(24.dp))

                        if (uiState.isEditMode) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { usuarioViewModel.cancelarEdicion() },
                                    modifier = Modifier.weight(1f)
                                ) { Text("Cancelar") }
                                Button(
                                    onClick = { usuarioViewModel.guardarPerfil() },
                                    modifier = Modifier.weight(1f)
                                ) { Text("Guardar") }
                            }
                        } else {
                            Button(
                                onClick = { usuarioViewModel.activarEdicion() },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("Modificar Datos") }
                        }

                        Spacer(Modifier.height(32.dp))

                        Button(
                            onClick = { usuarioViewModel.cerrarSesion() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar Sesión")
                            Spacer(Modifier.width(8.dp))
                            Text("Cerrar Sesión")
                        }
                    }
                }
            }
        }
    }
}