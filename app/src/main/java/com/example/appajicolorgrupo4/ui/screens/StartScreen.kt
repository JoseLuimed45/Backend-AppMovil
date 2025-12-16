package com.example.appajicolorgrupo4.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.appajicolorgrupo4.R
import com.example.appajicolorgrupo4.navigation.Screen
import com.example.appajicolorgrupo4.ui.components.AppBackground
import com.example.appajicolorgrupo4.data.session.SessionManager
import com.example.appajicolorgrupo4.viewmodel.MainViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun StartScreen(
    mainViewModel: MainViewModel
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    var isLoggedIn by remember { mutableStateOf(false) }

    // Verificar sesión de forma segura después de la composición
    LaunchedEffect(Unit) {
        try {
            isLoggedIn = sessionManager.isLoggedIn()
        } catch (e: Exception) {
            // Si falla, asumimos que no hay sesión
            isLoggedIn = false
        }
    }

    AppBackground {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Botón con imagen del logo - solo el área de la imagen es clickeable
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(R.drawable.logo_principal)
                    .size(512) // limitar tamaño de decodificación
                    .crossfade(true)
                    .build(),
                contentDescription = "Logo Aji Color",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(400.dp)
                    .graphicsLayer(clip = true)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        val destination = if (isLoggedIn) Screen.Home else Screen.Init
                        // CORREGIDO: Usar .route para pasar el String
                        mainViewModel.navigate(route = destination.route, popUpToRoute = Screen.StartScreen.route, inclusive = true)
                    }
            )
        }
    }
}
