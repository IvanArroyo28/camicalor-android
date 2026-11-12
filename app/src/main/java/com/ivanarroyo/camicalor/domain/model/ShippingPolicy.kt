package com.ivanarroyo.camicalor.domain.model

/** Reglas de envío de CamiCalor. */
object ShippingPolicy {
    const val SHIPPING_COST = 10_000L
    const val FREE_SHIPPING_THRESHOLD = 150_000L

    fun shippingCostFor(subtotal: Long): Long =
        if (subtotal == 0L || subtotal >= FREE_SHIPPING_THRESHOLD) 0L else SHIPPING_COST

    /** Cuánto le falta al cliente para tener envío gratis (0 si ya lo tiene). */
    fun remainingForFreeShipping(subtotal: Long): Long =
        (FREE_SHIPPING_THRESHOLD - subtotal).coerceAtLeast(0L)
}

data class OrderTotals(
    val subtotal: Long,
    val shippingCost: Long,
) {
    val total: Long get() = subtotal + shippingCost
    val isFreeShipping: Boolean get() = shippingCost == 0L
}

fun Cart.totals(): OrderTotals = OrderTotals(subtotal = total, shippingCost = ShippingPolicy.shippingCostFor(total))
