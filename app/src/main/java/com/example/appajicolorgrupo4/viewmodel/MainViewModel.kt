package com.example.appajicolorgrupo4.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appajicolorgrupo4.navigation.NavigationEvent
import com.example.appajicolorgrupo4.navigation.Screen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    /**
     * Función de navegación principal. Acepta una ruta como String para máxima flexibilidad.
     */
    fun navigate(route: String, popUpToRoute: String? = null, inclusive: Boolean = false, singleTop: Boolean = true) {
        viewModelScope.launch {
            _navigationEvents.emit(
                NavigationEvent.NavigateTo(route, popUpToRoute, inclusive, singleTop)
            )
        }
    }

    /**
     * Función de conveniencia para navegar a rutas simples definidas en la clase Screen.
     */
    fun navigate(screen: Screen, popUpToRoute: Screen? = null, inclusive: Boolean = false, singleTop: Boolean = true) {
        viewModelScope.launch {
            _navigationEvents.emit(
                NavigationEvent.NavigateTo(screen.route, popUpToRoute?.route, inclusive, singleTop)
            )
        }
    }

    fun navigateBack() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.PopBackStack)
        }
    }

    fun navigateUp() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.NavigateUp)
        }
    }
}
