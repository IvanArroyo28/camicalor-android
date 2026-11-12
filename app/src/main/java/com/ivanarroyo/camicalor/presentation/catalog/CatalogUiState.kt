package com.ivanarroyo.camicalor.presentation.catalog

import com.ivanarroyo.camicalor.domain.model.Category
import com.ivanarroyo.camicalor.domain.model.Product

/** Todos los estados posibles de la pantalla. La UI solo dibuja el estado actual. */
sealed interface CatalogUiState {
    data object Loading : CatalogUiState

    data class Success(
        /** Productos que pasan la búsqueda y el filtro. */
        val products: List<Product>,
        /** Categorías que existen en el catálogo, para los chips. */
        val categories: List<Category>,
        val selectedCategory: Category?,
        /** true si el catálogo no tiene ningún producto (distinto de "la búsqueda no encontró nada"). */
        val isCatalogEmpty: Boolean,
    ) : CatalogUiState

    data class Error(val isNetworkError: Boolean) : CatalogUiState
}
