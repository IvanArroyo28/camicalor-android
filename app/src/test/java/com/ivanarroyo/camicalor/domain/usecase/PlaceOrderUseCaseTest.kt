package com.ivanarroyo.camicalor.domain.usecase

import com.ivanarroyo.camicalor.domain.model.Cart
import com.ivanarroyo.camicalor.domain.model.CartItem
import com.ivanarroyo.camicalor.domain.model.CheckoutException
import com.ivanarroyo.camicalor.domain.model.PaymentMethod
import com.ivanarroyo.camicalor.domain.model.ShippingInfo
import com.ivanarroyo.camicalor.domain.model.Size
import com.ivanarroyo.camicalor.domain.service.OrderNumberGenerator
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaceOrderUseCaseTest {

    private val repository = FakeCartRepository()
    private val placeOrder = PlaceOrderUseCase(
        cartRepository = repository,
        validateShippingInfo = ValidateShippingInfoUseCase(),
        orderNumberGenerator = OrderNumberGenerator { "CC-1234" },
    )

    private val validInfo = ShippingInfo(
        fullName = "  Ivan Arroyo ",
        phone = "3001234567",
        address = "Calle 30 # 17-45",
        neighborhood = "Getsemaní",
    )

    private fun fillCart() {
        repository.cart.value = Cart(
            listOf(
                CartItem("cc-001", "Sol Caribe", "", 59_900, Size.M, 2),
                CartItem("cc-009", "Bahía", "", 74_900, Size.L, 1),
            )
        )
    }

    @Test
    fun `places the order with totals and clears the cart`() = runBlocking {
        fillCart()

        val order = placeOrder(validInfo, PaymentMethod.BANK_TRANSFER).getOrThrow()

        assertEquals("CC-1234", order.id)
        assertEquals(3, order.itemCount)
        assertEquals(194_700L, order.totals.subtotal)
        assertEquals(0L, order.totals.shippingCost)
        assertEquals("Ivan Arroyo", order.shippingInfo.fullName)
        assertTrue(repository.cart.value.isEmpty)
    }

    @Test
    fun `empty cart cannot be ordered`() = runBlocking {
        val result = placeOrder(validInfo, PaymentMethod.CASH_ON_DELIVERY)

        assertTrue(result.exceptionOrNull() is CheckoutException.EmptyCart)
    }

    @Test
    fun `invalid shipping info fails and keeps the cart`() = runBlocking {
        fillCart()

        val result = placeOrder(validInfo.copy(phone = "123"), PaymentMethod.CASH_ON_DELIVERY)

        assertTrue(result.exceptionOrNull() is CheckoutException.InvalidShippingInfo)
        assertFalse(repository.cart.value.isEmpty)
    }
}
