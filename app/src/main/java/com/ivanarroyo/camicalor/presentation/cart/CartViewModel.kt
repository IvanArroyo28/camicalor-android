package com.ivanarroyo.camicalor.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanarroyo.camicalor.domain.model.CartItem
import com.ivanarroyo.camicalor.domain.usecase.ObserveCartUseCase
import com.ivanarroyo.camicalor.domain.usecase.RemoveFromCartUseCase
import com.ivanarroyo.camicalor.domain.usecase.UpdateCartItemQuantityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    observeCart: ObserveCartUseCase,
    private val updateQuantity: UpdateCartItemQuantityUseCase,
    private val removeFromCart: RemoveFromCartUseCase,
) : ViewModel() {

    /**
     * No guardamos el carrito aquí: lo observamos desde Room.
     * Cuando cambias una cantidad, Room emite el carrito nuevo y la pantalla se redibuja sola.
     */
    val uiState: StateFlow<CartUiState> = observeCart()
        .map { cart -> CartUiState.Content(cart) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CartUiState.Loading,
        )

    fun onIncrease(item: CartItem) {
        viewModelScope.launch { updateQuantity(item.productId, item.size, item.quantity + 1) }
    }

    fun onDecrease(item: CartItem) {
        viewModelScope.launch { updateQuantity(item.productId, item.size, item.quantity - 1) }
    }

    fun onRemove(item: CartItem) {
        viewModelScope.launch { removeFromCart(item.productId, item.size) }
    }
}
