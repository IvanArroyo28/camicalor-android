package com.ivanarroyo.camicalor.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ShippingPolicyTest {

    @Test
    fun `below the threshold shipping costs 10000`() {
        assertEquals(10_000L, ShippingPolicy.shippingCostFor(59_900))
    }

    @Test
    fun `from 150000 shipping is free`() {
        assertEquals(0L, ShippingPolicy.shippingCostFor(150_000))
        assertEquals(0L, ShippingPolicy.shippingCostFor(194_700))
    }

    @Test
    fun `remaining for free shipping is calculated`() {
        assertEquals(90_100L, ShippingPolicy.remainingForFreeShipping(59_900))
        assertEquals(0L, ShippingPolicy.remainingForFreeShipping(200_000))
    }

    @Test
    fun `cart totals include shipping`() {
        val cart = Cart(listOf(CartItem("cc-001", "Sol Caribe", "", 59_900, Size.M, 1)))
        val totals = cart.totals()

        assertEquals(59_900L, totals.subtotal)
        assertEquals(10_000L, totals.shippingCost)
        assertEquals(69_900L, totals.total)
    }

    @Test
    fun `empty cart has no shipping cost`() {
        assertTrue(Cart().totals().isFreeShipping)
    }
}
