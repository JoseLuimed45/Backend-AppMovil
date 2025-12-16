package com.example.appajicolorgrupo4.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appajicolorgrupo4.ui.components.AppBackground
import com.example.appajicolorgrupo4.viewmodel.MainViewModel
import com.example.appajicolorgrupo4.viewmodel.UsuarioViewModel

@Composable
fun DebugScreen(mainViewModel: MainViewModel, usuarioViewModel: UsuarioViewModel) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Base de Datos Limpiada") },
            text = { Text("Todos los datos de usuario y pedidos han sido eliminados.") },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    AppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text("Pantalla de Depuración", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Base de Datos", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { usuarioViewModel.clearAllData { showDialog = true } },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) { Text("Limpiar BD (Usuarios y Pedidos)") }
                }
            }
        }
    }
}
