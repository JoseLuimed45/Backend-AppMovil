package com.example.appajicolorgrupo4.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.appajicolorgrupo4.data.local.database.AppDatabase
import com.example.appajicolorgrupo4.data.repository.PedidoRepository
import com.example.appajicolorgrupo4.data.repository.RemotePedidoRepository
import com.example.appajicolorgrupo4.data.repository.UserRepository
import com.example.appajicolorgrupo4.data.session.SessionManager
import com.example.appajicolorgrupo4.ui.screens.*
import com.example.appajicolorgrupo4.viewmodel.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // --- Dependencias Únicas ---
    val db = AppDatabase.getInstance(context)
    val mainViewModel: MainViewModel = viewModel()
    val carritoViewModel: CarritoViewModel = viewModel()
    val userRepository = UserRepository(db.userDao())
    val sessionManager = SessionManager(context)
    val localPedidoRepository = PedidoRepository(db.pedidoDao())


    // --- ViewModels con Inyección de Dependencias ---

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(userRepository, sessionManager, mainViewModel)
    )

    val usuarioViewModel: UsuarioViewModel = viewModel(
        factory = UsuarioViewModelFactory(userRepository, sessionManager, mainViewModel, localPedidoRepository)
    )

    val pedidosViewModel: PedidosViewModel = viewModel(
        factory = PedidosViewModelFactory(mainViewModel, carritoViewModel, usuarioViewModel)
    )

    val notificacionesViewModel: NotificacionesViewModel = viewModel(
        factory = NotificacionesViewModelFactory(mainViewModel)
    )

    val productoViewModel: ProductoViewModel = viewModel(
        factory = ProductoViewModelFactory(mainViewModel)
    )

    // --- Observador de Navegación Central ---
    LaunchedEffect(key1 = mainViewModel) {
        mainViewModel.navigationEvents.collect { event ->
            when (event) {
                is NavigationEvent.NavigateTo -> {
                    navController.navigate(event.route) {
                        event.popUpToRoute?.let { popUpTo ->
                            popUpTo(popUpTo) { inclusive = event.inclusive }
                        }
                        launchSingleTop = event.singleTop
                    }
                }
                is NavigationEvent.PopBackStack -> navController.popBackStack()
                is NavigationEvent.NavigateUp -> navController.navigateUp()
            }
        }
    }

    // --- Grafo de Navegación ---
    NavHost(navController = navController, startDestination = Screen.StartScreen.route) {

        // Flujo de Autenticación
        composable(Screen.StartScreen.route) { StartScreen(mainViewModel) }
        composable(Screen.Init.route) { InitScreen(mainViewModel) }
        composable(Screen.Login.route) { LoginScreen(authViewModel, mainViewModel) }
        composable(Screen.Registro.route) { RegistroScreen(mainViewModel, authViewModel) }
        composable(Screen.PasswordRecovery.route) { PasswordRecoveryScreen(mainViewModel, authViewModel) }

        // Flujo Principal
        composable(Screen.Home.route) { HomeScreen(mainViewModel, usuarioViewModel) }
        composable(Screen.Profile.route) { ProfileScreen(mainViewModel, usuarioViewModel) }
        composable(Screen.Settings.route) { SettingScreen(mainViewModel) }
        composable(Screen.Notification.route) { NotificationScreen(mainViewModel, notificacionesViewModel) }
        composable(Screen.Debug.route) { DebugScreen(mainViewModel, usuarioViewModel) }

        // Flujo de Comercio
        composable(Screen.Catalogo.route) { CatalogoProductosScreen(productoViewModel, mainViewModel) }
        composable(Screen.DetalleProducto.routePattern, arguments = listOf(navArgument("productoId") { type = NavType.StringType })) {
            val productoId = it.arguments?.getString("productoId")
            if (productoId != null) {
                DetalleProductoScreen(productoId, productoViewModel, carritoViewModel, mainViewModel)
            }
        }
        composable(Screen.Cart.route) { CartScreen(carritoViewModel, mainViewModel, usuarioViewModel) }
        composable(Screen.Checkout.route) { CheckoutScreen(mainViewModel, carritoViewModel, usuarioViewModel) }
        composable(Screen.PaymentMethods.route) { PaymentMethodsScreen(mainViewModel, carritoViewModel, pedidosViewModel, usuarioViewModel) }
        composable(Screen.Success.routePattern, arguments = listOf(navArgument("numeroPedido") { type = NavType.StringType; nullable = true })) {
            val numeroPedido = it.arguments?.getString("numeroPedido")
            SuccessScreen(mainViewModel, numeroPedido)
        }

        // Flujo de Pedidos
        composable(Screen.OrderHistory.route) { OrderHistoryScreen(pedidosViewModel, usuarioViewModel, mainViewModel) }
        composable(Screen.DetallePedido.routePattern, arguments = listOf(navArgument("numeroPedido") { type = NavType.StringType })) {
            val numeroPedido = it.arguments?.getString("numeroPedido")
            if (numeroPedido != null) {
                DetallePedidoScreen(mainViewModel, pedidosViewModel, numeroPedido)
            }
        }

        // Flujo de Administrador
        composable(Screen.AdminProductos.route) { AdminProductosScreen(mainViewModel) }
        composable(Screen.AdminPedidos.route) { AdminPedidosScreen(mainViewModel, pedidosViewModel) }
        composable(Screen.AdminUsuarios.route) { AdminUsuariosScreen(mainViewModel, usuarioViewModel) }
    }
}