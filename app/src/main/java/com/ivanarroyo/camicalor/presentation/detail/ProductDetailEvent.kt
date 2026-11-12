package com.ivanarroyo.camicalor.presentation.detail

import com.ivanarroyo.camicalor.domain.model.Size

sealed interface ProductDetailEvent {
    data class AddedToCart(val size: Size) : ProductDetailEvent
    data object AddToCartFailed : ProductDetailEvent
}
