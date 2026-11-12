package com.ivanarroyo.camicalor.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.ivanarroyo.camicalor.domain.model.ProductNotFoundException
import com.ivanarroyo.camicalor.domain.model.Size
import com.ivanarroyo.camicalor.domain.usecase.AddToCartUseCase
import com.ivanarroyo.camicalor.domain.usecase.GetProductByIdUseCase
import com.ivanarroyo.camicalor.presentation.detail.ProductDetailUiState.ErrorReason
import com.ivanarroyo.camicalor.presentation.navigation.ProductDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getProductById: GetProductByIdUseCase,
    private val addToCart: AddToCartUseCase,
) : ViewModel() {

    /** El id llega por la ruta de navegación y sobrevive a la rotación de pantalla. */
    private val productId: String = savedStateHandle.toRoute<ProductDetailRoute>().productId

    private val _uiState = MutableStateFlow<ProductDetailUiState>(ProductDetailUiState.Loading)
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    /**
     * Eventos de una sola vez (mostrar un snackbar). No van en el estado porque
     * no deben repetirse al girar la pantalla.
     */
    private val _events = Channel<ProductDetailEvent>(Channel.BUFFERED)
    val events: Flow<ProductDetailEvent> = _events.receiveAsFlow()

    init {
        loadProduct()
    }

    fun loadProduct() {
        _uiState.value = ProductDetailUiState.Loading
        viewModelScope.launch {
            _uiState.value = getProductById(productId).fold(
                onSuccess = { product -> ProductDetailUiState.Success(product) },
                onFailure = { error ->
                    ProductDetailUiState.Error(
                        when (error) {
                            is IOException -> ErrorReason.NETWORK
                            is ProductNotFoundException -> ErrorReason.NOT_FOUND
                            else -> ErrorReason.UNKNOWN
                        }
                    )
                },
            )
        }
    }

    fun onAddToCart() {
        val state = _uiState.value as? ProductDetailUiState.Success ?: return
        val size = state.selectedSize ?: return
        viewModelScope.launch {
            val event = addToCart(state.product, size).fold(
                onSuccess = { ProductDetailEvent.AddedToCart(size) },
                onFailure = { ProductDetailEvent.AddToCartFailed },
            )
            _events.send(event)
        }
    }

    fun onSizeSelected(size: Size) {
        _uiState.update { state ->
            if (state is ProductDetailUiState.Success) state.copy(selectedSize = size) else state
        }
    }
}
