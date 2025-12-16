package com.example.appajicolorgrupo4.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.appajicolorgrupo4.data.Notificacion
import com.example.appajicolorgrupo4.data.TipoNotificacion
import com.example.appajicolorgrupo4.navigation.Screen
import com.example.appajicolorgrupo4.ui.components.AppBackground
import com.example.appajicolorgrupo4.ui.components.AppNavigationDrawer
import com.example.appajicolorgrupo4.ui.components.BottomNavigationBar
import com.example.appajicolorgrupo4.ui.components.TopBarWithCart
import com.example.appajicolorgrupo4.viewmodel.MainViewModel
import com.example.appajicolorgrupo4.viewmodel.NotificacionesViewModel
import com.example.appajicolorgrupo4.viewmodel.UsuarioViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    mainViewModel: MainViewModel,
    notificacionesViewModel: NotificacionesViewModel,
    usuarioViewModel: UsuarioViewModel
) {
    val notificaciones by notificacionesViewModel.notificaciones.collectAsState()
    val formatoFecha = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    AppBackground {
        AppNavigationDrawer(
            drawerState = drawerState,
            scope = scope,
            currentRoute = Screen.Notification.route,
            onHomeClick = { mainViewModel.navigate(Screen.Home.route) },
            onProfileClick = { mainViewModel.navigate(Screen.Profile.route) },
            onSettingsClick = { mainViewModel.navigate(Screen.Settings.route) },
            onNotificationsClick = { /* Ya estás aquí */ },
            onCartClick = { mainViewModel.navigate(Screen.Cart.route) },
            onOrderHistoryClick = { mainViewModel.navigate(Screen.OrderHistory.route) },
            onLogoutClick = { usuarioViewModel.cerrarSesion() }
        ) {
            Scaffold(
                topBar = {
                    TopBarWithCart(
                        title = "Notificaciones",
                        scope = scope,
                        drawerState = drawerState,
                        onCartClick = { mainViewModel.navigate(Screen.Cart.route) }
                    )
                },
                bottomBar = {
                    BottomNavigationBar(
                        currentRoute = Screen.Notification.route,
                        onNavigate = { route -> mainViewModel.navigate(route) }
                    )
                },
                containerColor = Color.Transparent
            ) { paddingValues ->
                if (notificaciones.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(paddingValues).padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            "Sin notificaciones",
                            Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "No tienes notificaciones",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(notificaciones, key = { it.id }) { notificacion ->
                            NotificacionCard(
                                notificacion = notificacion,
                                formatoFecha = formatoFecha,
                                onClick = { notificacionesViewModel.onNotificacionClicked(notificacion) },
                                onDelete = { notificacionesViewModel.eliminarNotificacion(notificacion.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificacionCard(
    notificacion: Notificacion,
    formatoFecha: SimpleDateFormat,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (notificacion.leida)
                MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
            else
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Ícono según tipo
            Icon(
                imageVector = when (notificacion.tipo) {
                    TipoNotificacion.COMPRA_EXITOSA -> Icons.Default.CheckCircle
                    TipoNotificacion.PEDIDO_CONFIRMADO -> Icons.Default.ShoppingBag
                    TipoNotificacion.PEDIDO_ENVIADO -> Icons.Default.LocalShipping
                    TipoNotificacion.PEDIDO_ENTREGADO -> Icons.Default.Done
                    TipoNotificacion.PROMOCION -> Icons.Default.LocalOffer
                    TipoNotificacion.GENERAL -> Icons.Default.Info
                },
                contentDescription = notificacion.tipo.name,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            // Contenido
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = notificacion.titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = if (notificacion.leida) FontWeight.Normal else FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = notificacion.mensaje,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
                Text(
                    text = formatoFecha.format(notificacion.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Botón eliminar
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
