package com.example.appajicolorgrupo4.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.appajicolorgrupo4.data.local.database.AppDatabase
import com.example.appajicolorgrupo4.data.repository.UserRepository
import com.example.appajicolorgrupo4.data.session.SessionManager
import com.example.appajicolorgrupo4.ui.screens.*
import com.example.appajicolorgrupo4.viewmodel.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = androidx.compose.ui.platform.LocalContext.current

    // --- ViewModels y Dependencias ---
    val mainViewModel: MainViewModel = viewModel()
    val usuarioViewModel: UsuarioViewModel = viewModel()
    val carritoViewModel: CarritoViewModel = viewModel()

    val userDao = AppDatabase.getInstance(context).userDao()
    val userRepository = UserRepository(userDao)
    val sessionManager = SessionManager(context)

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(userRepository, sessionManager, mainViewModel)
    )

    val pedidosViewModel: PedidosViewModel = viewModel(
        factory = PedidosViewModelFactory(mainViewModel, carritoViewModel, usuarioViewModel)
    )

    val productoViewModel: ProductoViewModel = viewModel()
    val notificacionesViewModel: NotificacionesViewModel = viewModel()
    val postViewModel: PostViewModel = viewModel()

    // Observador central de eventos de navegación
    LaunchedEffect(key1 = mainViewModel) {
        mainViewModel.navigationEvents.collect { event ->
            when (event) {
                is NavigationEvent.NavigateTo -> {
                    navController.navigate(event.route) { // Ahora usa la ruta String directamente
                        event.popUpToRoute?.let { popUpTo ->
                            popUpTo(popUpTo) { inclusive = event.inclusive } // popUpTo también es un String
                        }
                        launchSingleTop = event.singleTop
                    }
                }
                is NavigationEvent.PopBackStack -> navController.popBackStack()
                is NavigationEvent.NavigateUp -> navController.navigateUp()
            }
        }
    }

    NavHost(navController = navController, startDestination = Screen.StartScreen.route) {
        // ... (el resto de las definiciones de composable no cambian)
    }
}
