package com.ivanarroyo.camicalor.domain.usecase

import com.ivanarroyo.camicalor.domain.model.CheckoutException
import com.ivanarroyo.camicalor.domain.model.Order
import com.ivanarroyo.camicalor.domain.model.PaymentMethod
import com.ivanarroyo.camicalor.domain.model.ShippingInfo
import com.ivanarroyo.camicalor.domain.model.totals
import com.ivanarroyo.camicalor.domain.repository.CartRepository
import com.ivanarroyo.camicalor.domain.service.OrderNumberGenerator
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Confirma el pedido (simulado): valida los datos, arma la orden con el carrito actual
 * y vacía el carrito. En una app real, aquí se enviaría la orden a un servidor.
 */
class PlaceOrderUseCase @Inject constructor(
    private val cartRepository: CartRepository,
    private val validateShippingInfo: ValidateShippingInfoUseCase,
    private val orderNumberGenerator: OrderNumberGenerator,
) {
    suspend operator fun invoke(info: ShippingInfo, paymentMethod: PaymentMethod): Result<Order> {
        val errors = validateShippingInfo(info)
        if (errors.isNotEmpty()) return Result.failure(CheckoutException.InvalidShippingInfo(errors))

        return runCatchingNonCancellable {
            val cart = cartRepository.observeCart().first()
            if (cart.isEmpty) throw CheckoutException.EmptyCart()

            val order = Order(
                id = orderNumberGenerator.next(),
                items = cart.items,
                totals = cart.totals(),
                shippingInfo = info.trimmed(),
                paymentMethod = paymentMethod,
            )
            cartRepository.clearCart()
            order
        }
    }
}
