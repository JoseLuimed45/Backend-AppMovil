package com.example.appajicolorgrupo4.navigation

/**
 * Representa los eventos de navegación.
 * Ahora es más flexible al trabajar con rutas como String, el "lenguaje" nativo de NavController.
 */
sealed class NavigationEvent {
    /**
     * Navegar a un destino específico.
     * @param route La ruta como String, puede incluir argumentos (e.g., "success/12345").
     * @param popUpToRoute La ruta (como String) hasta la cual hacer pop. Null si no se necesita.
     */
    data class NavigateTo(
        val route: String,
        val popUpToRoute: String? = null,
        val inclusive: Boolean = false,
        val singleTop: Boolean = false
    ) : NavigationEvent()

    data object PopBackStack : NavigationEvent()
    data object NavigateUp : NavigationEvent()
}
