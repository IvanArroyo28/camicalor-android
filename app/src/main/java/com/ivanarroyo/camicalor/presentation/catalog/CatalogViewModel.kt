package com.ivanarroyo.camicalor.presentation.catalog

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanarroyo.camicalor.domain.model.Category
import com.ivanarroyo.camicalor.domain.model.Product
import com.ivanarroyo.camicalor.domain.usecase.FilterProductsUseCase
import com.ivanarroyo.camicalor.domain.usecase.GetProductsUseCase
import com.ivanarroyo.camicalor.domain.usecase.ObserveCartUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val getProducts: GetProductsUseCase,
    private val filterProducts: FilterProductsUseCase,
    observeCart: ObserveCartUseCase,
) : ViewModel() {

    /** Estado de la descarga, separado de lo que el usuario filtra. */
    private sealed interface LoadState {
        data object Loading : LoadState
        data class Loaded(val products: List<Product>) : LoadState
        data class Failed(val isNetworkError: Boolean) : LoadState
    }

    private val loadState = MutableStateFlow<LoadState>(LoadState.Loading)
    private val selectedCategory = MutableStateFlow<Category?>(null)

    /**
     * El texto del buscador vive en un State de Compose, no en un Flow:
     * así el TextField se actualiza al instante y el cursor no salta al escribir rápido.
     */
    var query by mutableStateOf("")
        private set

    /** Cada vez que cambia la descarga, el texto o la categoría, se recalcula la pantalla. */
    val uiState: StateFlow<CatalogUiState> =
        combine(loadState, snapshotFlow { query }, selectedCategory) { load, currentQuery, category ->
            when (load) {
                LoadState.Loading -> CatalogUiState.Loading
                is LoadState.Failed -> CatalogUiState.Error(load.isNetworkError)
                is LoadState.Loaded -> CatalogUiState.Success(
                    products = filterProducts(load.products, currentQuery, category),
                    categories = load.products.map { it.category }.distinct().sortedBy { it.ordinal },
                    selectedCategory = category,
                    isCatalogEmpty = load.products.isEmpty(),
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CatalogUiState.Loading,
        )

    /** Cuántas camisetas hay en el carrito, para el número sobre el ícono. */
    val cartItemCount: StateFlow<Int> = observeCart()
        .map { cart -> cart.totalItems }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initialValue = 0)

    init {
        loadProducts()
    }

    fun loadProducts() {
        loadState.value = LoadState.Loading
        viewModelScope.launch {
            loadState.value = getProducts().fold(
                onSuccess = { products -> LoadState.Loaded(products) },
                onFailure = { error -> LoadState.Failed(isNetworkError = error is IOException) },
            )
        }
    }

    fun onQueryChange(newQuery: String) {
        query = newQuery
    }

    /** Tocar la categoría ya seleccionada la quita (vuelve a "Todas"). */
    fun onCategorySelected(category: Category?) {
        selectedCategory.value = if (selectedCategory.value == category) null else category
    }

    fun clearFilters() {
        query = ""
        selectedCategory.value = null
    }
}
