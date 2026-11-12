package com.ivanarroyo.camicalor.presentation.cart

import com.ivanarroyo.camicalor.domain.model.Cart

sealed interface CartUiState {
    data object Loading : CartUiState
    data class Content(val cart: Cart) : CartUiState
}
