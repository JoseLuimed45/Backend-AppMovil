package com.example.appajicolorgrupo4.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appajicolorgrupo4.BuildConfig
import com.example.appajicolorgrupo4.data.remote.BackendProbeResult
import com.example.appajicolorgrupo4.data.remote.BackendDiagnostics
import com.example.appajicolorgrupo4.ui.components.AppBackground
import com.example.appajicolorgrupo4.viewmodel.UsuarioViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class DebugBackendViewModel : ViewModel() {
    fun runProbe(onResult: (BackendProbeResult) -> Unit) {
        viewModelScope.launch {
            val result = BackendDiagnostics.probeProductos()
            onResult(result)
        }
    }
}

@Composable
fun DebugScreen(navController: NavController, usuarioViewModel: UsuarioViewModel) {
    val context = LocalContext.current
    val debugVm: DebugBackendViewModel = viewModel() // Obtener VM en contexto composable
    var showDialog by remember { mutableStateOf(false) }
    var probeResult by remember { mutableStateOf<BackendProbeResult?>(null) }
    var probing by remember { mutableStateOf(false) }

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

            // Sección Limpieza
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Base de Datos", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            usuarioViewModel.clearAllData { showDialog = true }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) { Text("Limpiar BD (Usuarios y Pedidos)") }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Sección Diagnóstico Backend
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Diagnóstico Backend", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text("BASE_URL: ${BuildConfig.BASE_URL}")
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            probing = true
                            probeResult = null
                            // Usar el ViewModel obtenido arriba, sin invocar viewModel() aquí
                            debugVm.runProbe { result ->
                                probeResult = result
                                probing = false
                            }
                        }, enabled = !probing
                    ) {
                        if (probing) CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp) else Icon(Icons.Default.NetworkCheck, null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (probing) "Probando..." else "Probar conexión productos")
                    }
                    Spacer(Modifier.height(12.dp))
                    probeResult?.let { pr ->
                        Text("Resultado: ${pr.message}", color = if (pr.ok) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                        Text("HTTP: ${pr.httpCode ?: "-"} • Latencia: ${pr.latencyMs} ms")
                        pr.rawError?.let { raw ->
                            Spacer(Modifier.height(4.dp))
                            Text("Raw: ${raw.take(120)}")
                        }
                    }
                }
            }
        }
    }
}
