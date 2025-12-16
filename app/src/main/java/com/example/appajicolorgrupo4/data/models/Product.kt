package com.example.appajicolorgrupo4.data.models

import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos para Producto que coincide con ProductTest.kt (precio: Int) y con la API
 * (mapeable si se recibe Double).
 */
data class Product(
        val id: String,
        val nombre: String,
        val descripcion: String,
        val precio: Int,
        val categoria: String,
        val stock: Int,
        val imagenUrl: String,
        // Campos opcionales adicionales para compatibilidad UI (con valores por defecto)
        val imagenResId: Int? = null,
        val tipoTalla: String? = null,
        val permiteTipoInfantil: Boolean = false,
        val coloresDisponibles: List<String>? = null,
        val rating: Float = 0f,
        val cantidadReviews: Int = 0,
        // Campos del servidor que ignoramos
        @SerializedName("_id") val mongoId: String? = null,
        val createdAt: String? = null,
        val updatedAt: String? = null,
        @SerializedName("__v") val version: Int? = null
)
