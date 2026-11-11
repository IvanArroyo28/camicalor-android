package com.ivanarroyo.camicalor.presentation.detail

import com.ivanarroyo.camicalor.domain.model.Product
import com.ivanarroyo.camicalor.domain.model.Size

sealed interface ProductDetailUiState {
    data object Loading : ProductDetailUiState

    data class Success(
        val product: Product,
        val selectedSize: Size? = null,
    ) : ProductDetailUiState {
        /** Solo se puede agregar si hay stock y ya se eligió talla. */
        val canAddToCart: Boolean get() = product.inStock && selectedSize != null
    }

    data class Error(val reason: ErrorReason) : ProductDetailUiState

    enum class ErrorReason { NETWORK, NOT_FOUND, UNKNOWN }
}
