package com.ivanarroyo.camicalor.presentation.catalog

import com.ivanarroyo.camicalor.domain.model.Product

/** Todos los estados posibles de la pantalla. La UI solo dibuja el estado actual. */
sealed interface CatalogUiState {
    data object Loading : CatalogUiState
    data class Success(val products: List<Product>) : CatalogUiState
    data class Error(val isNetworkError: Boolean) : CatalogUiState
}
