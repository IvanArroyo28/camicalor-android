package com.ivanarroyo.camicalor.domain.usecase

import com.ivanarroyo.camicalor.domain.model.Cart
import com.ivanarroyo.camicalor.domain.model.CartItem
import com.ivanarroyo.camicalor.domain.model.Product
import com.ivanarroyo.camicalor.domain.model.Size
import com.ivanarroyo.camicalor.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** Repositorio en memoria para testear los casos de uso sin Room ni Android. */
class FakeCartRepository : CartRepository {

    val cart = MutableStateFlow(Cart())

    override fun observeCart(): Flow<Cart> = cart

    override suspend fun addToCart(product: Product, size: Size, maxQuantity: Int) {
        val items = cart.value.items
        val existing = items.find { it.productId == product.id && it.size == size }
        cart.value = Cart(
            if (existing == null) {
                items + CartItem(product.id, product.name, product.imageUrl, product.price, size, 1)
            } else {
                items.map { if (it == existing) it.copy(quantity = (it.quantity + 1).coerceAtMost(maxQuantity)) else it }
            }
        )
    }

    override suspend fun updateQuantity(productId: String, size: Size, quantity: Int) {
        cart.value = Cart(cart.value.items.map {
            if (it.productId == productId && it.size == size) it.copy(quantity = quantity) else it
        })
    }

    override suspend fun removeItem(productId: String, size: Size) {
        cart.value = Cart(cart.value.items.filterNot { it.productId == productId && it.size == size })
    }

    override suspend fun clearCart() {
        cart.value = Cart()
    }
}
