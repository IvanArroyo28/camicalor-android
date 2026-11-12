package com.ivanarroyo.camicalor.domain.model

data class Cart(val items: List<CartItem> = emptyList()) {

    /** Total de camisetas (sumando cantidades), para el contador del ícono. */
    val totalItems: Int get() = items.sumOf { it.quantity }

    /** Total a pagar en pesos. */
    val total: Long get() = items.sumOf { it.subtotal }

    val isEmpty: Boolean get() = items.isEmpty()

    companion object {
        /** Regla de negocio: máximo 10 unidades por camiseta y talla. */
        const val MAX_QUANTITY_PER_ITEM = 10
    }
}
