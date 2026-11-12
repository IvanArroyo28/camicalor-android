package com.ivanarroyo.camicalor.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CartTest {

    private val cart = Cart(
        listOf(
            CartItem("cc-001", "Sol Caribe", "", unitPrice = 59900, size = Size.M, quantity = 2),
            CartItem("cc-009", "Bahía", "", unitPrice = 74900, size = Size.L, quantity = 1),
        )
    )

    @Test
    fun `total sums every subtotal`() {
        assertEquals(59900L * 2 + 74900L, cart.total)
    }

    @Test
    fun `totalItems counts quantities, not lines`() {
        assertEquals(3, cart.totalItems)
    }

    @Test
    fun `empty cart has zero total`() {
        assertTrue(Cart().isEmpty)
        assertEquals(0L, Cart().total)
    }
}
