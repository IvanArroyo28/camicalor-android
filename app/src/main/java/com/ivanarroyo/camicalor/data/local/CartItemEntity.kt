package com.ivanarroyo.camicalor.data.local

import androidx.room.Entity

/**
 * Tabla del carrito. La clave es (productId, size):
 * así la misma camiseta en dos tallas son dos filas distintas.
 */
@Entity(tableName = "cart_items", primaryKeys = ["productId", "size"])
data class CartItemEntity(
    val productId: String,
    val size: String,
    val name: String,
    val imageUrl: String,
    val unitPrice: Long,
    val quantity: Int,
    /** Para mostrar el carrito en el orden en que se fue agregando. */
    val addedAt: Long,
)
