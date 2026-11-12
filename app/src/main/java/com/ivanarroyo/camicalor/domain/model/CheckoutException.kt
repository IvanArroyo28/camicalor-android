package com.ivanarroyo.camicalor.domain.model

sealed class CheckoutException(message: String) : Exception(message) {
    class EmptyCart : CheckoutException("El carrito está vacío")
    class InvalidShippingInfo(val errors: Map<ShippingField, ShippingError>) :
        CheckoutException("Datos de envío inválidos: $errors")
}
