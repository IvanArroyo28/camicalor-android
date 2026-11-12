package com.ivanarroyo.camicalor.presentation.checkout

import com.ivanarroyo.camicalor.domain.model.Order

sealed interface CheckoutEvent {
    data class OrderPlaced(val order: Order) : CheckoutEvent
    data object OrderFailed : CheckoutEvent
}
