package com.ivanarroyo.camicalor.domain.model

/**
 * Una línea del carrito: una camiseta en una talla concreta.
 * La misma camiseta en M y en L son dos CartItem distintos.
 */
data class CartItem(
    val productId: String,
    val name: String,
    val imageUrl: String,
    /** Precio en pesos guardado al momento de agregarla. */
    val unitPrice: Long,
    val size: Size,
    val quantity: Int,
) {
    val subtotal: Long get() = unitPrice * quantity
}
