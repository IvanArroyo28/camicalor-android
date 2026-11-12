package com.ivanarroyo.camicalor.presentation.checkout

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanarroyo.camicalor.domain.model.CartItem
import com.ivanarroyo.camicalor.domain.model.OrderTotals
import com.ivanarroyo.camicalor.domain.model.PaymentMethod
import com.ivanarroyo.camicalor.domain.model.ShippingError
import com.ivanarroyo.camicalor.domain.model.ShippingField
import com.ivanarroyo.camicalor.domain.model.ShippingInfo
import com.ivanarroyo.camicalor.domain.model.totals
import com.ivanarroyo.camicalor.domain.usecase.ObserveCartUseCase
import com.ivanarroyo.camicalor.domain.usecase.PlaceOrderUseCase
import com.ivanarroyo.camicalor.domain.usecase.ValidateShippingInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Lo que el resumen del pedido necesita mostrar. */
data class CheckoutSummary(val items: List<CartItem>, val totals: OrderTotals)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    observeCart: ObserveCartUseCase,
    private val validateShippingInfo: ValidateShippingInfoUseCase,
    private val placeOrder: PlaceOrderUseCase,
) : ViewModel() {

    /** null mientras carga. */
    val summary: StateFlow<CheckoutSummary?> = observeCart()
        .map { cart -> CheckoutSummary(cart.items, cart.totals()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initialValue = null)

    // Los campos del formulario viven en State de Compose (igual que el buscador del catálogo).
    var form by mutableStateOf(ShippingInfo())
        private set

    var paymentMethod by mutableStateOf(PaymentMethod.CASH_ON_DELIVERY)
        private set

    var isPlacingOrder by mutableStateOf(false)
        private set

    /** Campos que el usuario ya tocó: solo a esos se les muestra el error (no regañamos antes de tiempo). */
    private var touchedFields by mutableStateOf(emptySet<ShippingField>())

    private val errors by derivedStateOf { validateShippingInfo(form) }

    val visibleErrors: Map<ShippingField, ShippingError> by derivedStateOf {
        errors.filterKeys { it in touchedFields }
    }

    val isFormValid: Boolean by derivedStateOf { errors.isEmpty() }

    private val _events = Channel<CheckoutEvent>(Channel.BUFFERED)
    val events: Flow<CheckoutEvent> = _events.receiveAsFlow()

    fun onFieldChange(field: ShippingField, value: String) {
        form = when (field) {
            ShippingField.FULL_NAME -> form.copy(fullName = value)
            ShippingField.PHONE -> form.copy(phone = value)
            ShippingField.ADDRESS -> form.copy(address = value)
            ShippingField.NEIGHBORHOOD -> form.copy(neighborhood = value)
            ShippingField.CITY -> form.copy(city = value)
        }
        touchedFields = touchedFields + field
    }

    fun onPaymentMethodSelected(method: PaymentMethod) {
        paymentMethod = method
    }

    fun onConfirm() {
        if (isPlacingOrder) return
        isPlacingOrder = true
        viewModelScope.launch {
            placeOrder(form, paymentMethod).fold(
                onSuccess = { order ->
                    // Se queda en "procesando" mientras navegamos a la confirmación.
                    _events.send(CheckoutEvent.OrderPlaced(order))
                },
                onFailure = {
                    touchedFields = ShippingField.entries.toSet()
                    isPlacingOrder = false
                    _events.send(CheckoutEvent.OrderFailed)
                },
            )
        }
    }
}
