package com.ivanarroyo.camicalor.domain.model

data class Order(
    val id: String,
    val items: List<CartItem>,
    val totals: OrderTotals,
    val shippingInfo: ShippingInfo,
    val paymentMethod: PaymentMethod,
) {
    val itemCount: Int get() = items.sumOf { it.quantity }
}
