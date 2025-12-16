package com.example.appajicolorgrupo4.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class NotificacionesViewModelFactory(
    private val mainViewModel: MainViewModel
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificacionesViewModel::class.java)) {
            return NotificacionesViewModel(mainViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
