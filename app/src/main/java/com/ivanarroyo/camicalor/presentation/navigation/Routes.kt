package com.ivanarroyo.camicalor.presentation.navigation

import kotlinx.serialization.Serializable

/** Rutas type-safe: el compilador verifica los argumentos, no hay strings como "detail/{id}". */
@Serializable
data object CatalogRoute

@Serializable
data class ProductDetailRoute(val productId: String)

@Serializable
data object CartRoute

@Serializable
data object CheckoutRoute

/** Solo pasamos lo que la pantalla de confirmación necesita mostrar. */
@Serializable
data class OrderConfirmationRoute(
    val orderId: String,
    val customerName: String,
    val total: Long,
    val itemCount: Int,
    val paymentMethod: String,
)
