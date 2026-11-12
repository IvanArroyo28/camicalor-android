package com.ivanarroyo.camicalor.domain.usecase

import com.ivanarroyo.camicalor.domain.model.Cart
import com.ivanarroyo.camicalor.domain.model.CartException
import com.ivanarroyo.camicalor.domain.model.Product
import com.ivanarroyo.camicalor.domain.model.Size
import com.ivanarroyo.camicalor.domain.repository.CartRepository
import javax.inject.Inject

/**
 * Las reglas de negocio viven aquí, no en la pantalla:
 * no se agrega algo agotado ni una talla que la camiseta no tiene.
 */
class AddToCartUseCase @Inject constructor(
    private val repository: CartRepository,
) {
    suspend operator fun invoke(product: Product, size: Size): Result<Unit> {
        if (!product.inStock) return Result.failure(CartException.OutOfStock(product.id))
        if (size !in product.sizes) return Result.failure(CartException.SizeNotAvailable(product.id, size))
        return runCatchingNonCancellable {
            repository.addToCart(product, size, Cart.MAX_QUANTITY_PER_ITEM)
        }
    }
}
