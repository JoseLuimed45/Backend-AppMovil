package com.example.appajicolorgrupo4.data.models

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("_id") val id: String? = null,
    val numeroPedido: String,
    val usuario: String, // User ID (ObjectId en backend)
    val productos: List<OrderProduct>,
    val subtotal: Double, // Decimal128 en backend
    val impuestos: Double, // Decimal128 en backend
    val costoEnvio: Double, // Decimal128 en backend
    val total: Double, // Decimal128 en backend
    val direccionEnvio: String,
    val telefono: String,
    val metodoPago: String,
    val estado: String = "CONFIRMADO",
    val createdAt: String? = null, // Timestamp del backend
    val updatedAt: String? = null
)

data class OrderProduct(
    val producto: String, // Product ID (ObjectId en backend)
    val cantidad: Int,
    val precioUnitario: Double, // Decimal128 en backend
    val talla: String? = null,
    val color: String? = null
)
