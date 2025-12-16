package com.example.appajicolorgrupo4.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appajicolorgrupo4.navigation.Screen
import com.example.appajicolorgrupo4.ui.components.AppBackground
import com.example.appajicolorgrupo4.viewmodel.MainViewModel

@Composable
fun SuccessScreen(
    mainViewModel: MainViewModel,
    numeroPedido: String?
) {
    AppBackground {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("✅", style = MaterialTheme.typography.displayLarge)
            Spacer(modifier = Modifier.height(24.dp))
            Text("¡Pedido realizado con éxito!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            if (numeroPedido != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Tu número de pedido es: $numeroPedido", style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = { mainViewModel.navigate(Screen.Home.route, popUpToRoute = Screen.Home.route, inclusive = true) }) {
                Text("Volver al Inicio")
            }
        }
    }
}
