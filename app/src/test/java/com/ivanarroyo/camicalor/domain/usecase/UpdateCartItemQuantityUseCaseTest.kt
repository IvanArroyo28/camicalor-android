package com.ivanarroyo.camicalor.domain.usecase

import com.ivanarroyo.camicalor.domain.model.Cart
import com.ivanarroyo.camicalor.domain.model.CartItem
import com.ivanarroyo.camicalor.domain.model.Size
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class UpdateCartItemQuantityUseCaseTest {

    private val repository = FakeCartRepository().apply {
        cart.value = Cart(listOf(CartItem("cc-001", "Sol Caribe", "", 59900, Size.M, quantity = 3)))
    }
    private val updateQuantity = UpdateCartItemQuantityUseCase(repository)

    private fun quantity() = repository.cart.value.items.single().quantity

    @Test
    fun `updates to the requested quantity`() = runBlocking {
        updateQuantity("cc-001", Size.M, 5)
        assertEquals(5, quantity())
    }

    @Test
    fun `quantity never goes below one`() = runBlocking {
        updateQuantity("cc-001", Size.M, 0)
        assertEquals(1, quantity())
    }

    @Test
    fun `quantity never goes above the maximum`() = runBlocking {
        updateQuantity("cc-001", Size.M, 50)
        assertEquals(Cart.MAX_QUANTITY_PER_ITEM, quantity())
    }
}
