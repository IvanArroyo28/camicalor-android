package com.ivanarroyo.camicalor.domain.model

/**
 * Camiseta del catálogo, tal como la entiende la app.
 * No sabe nada de JSON ni de Retrofit: eso vive en la capa de datos.
 */
data class Product(
    val id: String,
    val name: String,
    val description: String,
    /** Precio en pesos colombianos, como entero (nunca Double para dinero). */
    val price: Long,
    val category: Category,
    val sizes: List<Size>,
    val imageUrl: String,
    val inStock: Boolean,
)
