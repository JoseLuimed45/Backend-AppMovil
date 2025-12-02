package com.example.appajicolorgrupo4.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appajicolorgrupo4.data.repository.ProductRepository

/**
 * Factory para crear instancias de AdminProductViewModel con dependencias
 */
class AdminProductViewModelFactory(
    private val repository: ProductRepository
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminProductViewModel::class.java)) {
            return AdminProductViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
