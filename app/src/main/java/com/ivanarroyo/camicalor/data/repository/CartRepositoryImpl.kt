package com.ivanarroyo.camicalor.data.repository

import com.ivanarroyo.camicalor.data.local.CartDao
import com.ivanarroyo.camicalor.data.mapper.toCartEntity
import com.ivanarroyo.camicalor.data.mapper.toDomain
import com.ivanarroyo.camicalor.domain.model.Cart
import com.ivanarroyo.camicalor.domain.model.Product
import com.ivanarroyo.camicalor.domain.model.Size
import com.ivanarroyo.camicalor.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val dao: CartDao,
) : CartRepository {

    override fun observeCart(): Flow<Cart> =
        dao.observeAll().map { entities -> Cart(entities.mapNotNull { it.toDomain() }) }

    override suspend fun addToCart(product: Product, size: Size, maxQuantity: Int) {
        dao.addOrIncrement(product.toCartEntity(size, addedAt = System.currentTimeMillis()), maxQuantity)
    }

    override suspend fun updateQuantity(productId: String, size: Size, quantity: Int) {
        dao.updateQuantity(productId, size.code, quantity)
    }

    override suspend fun removeItem(productId: String, size: Size) {
        dao.delete(productId, size.code)
    }

    override suspend fun clearCart() {
        dao.clear()
    }
}
