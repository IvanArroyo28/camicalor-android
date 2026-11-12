package com.ivanarroyo.camicalor.domain.repository

import com.ivanarroyo.camicalor.domain.model.Cart
import com.ivanarroyo.camicalor.domain.model.Product
import com.ivanarroyo.camicalor.domain.model.Size
import kotlinx.coroutines.flow.Flow

interface CartRepository {

    /** Emite el carrito cada vez que cambia: la UI se actualiza sola. */
    fun observeCart(): Flow<Cart>

    /** Si ya existe la camiseta en esa talla, suma 1 (sin pasar de [maxQuantity]). */
    suspend fun addToCart(product: Product, size: Size, maxQuantity: Int)

    suspend fun updateQuantity(productId: String, size: Size, quantity: Int)

    suspend fun removeItem(productId: String, size: Size)

    suspend fun clearCart()
}
