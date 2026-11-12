package com.ivanarroyo.camicalor.domain.usecase

import com.ivanarroyo.camicalor.domain.model.Cart
import com.ivanarroyo.camicalor.domain.model.Size
import com.ivanarroyo.camicalor.domain.repository.CartRepository
import javax.inject.Inject

class UpdateCartItemQuantityUseCase @Inject constructor(
    private val repository: CartRepository,
) {
    /** La cantidad siempre queda entre 1 y el máximo. Para quitar, se usa [RemoveFromCartUseCase]. */
    suspend operator fun invoke(productId: String, size: Size, quantity: Int) {
        repository.updateQuantity(productId, size, quantity.coerceIn(1, Cart.MAX_QUANTITY_PER_ITEM))
    }
}
