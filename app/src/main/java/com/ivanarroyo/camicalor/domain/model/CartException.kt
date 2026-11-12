package com.ivanarroyo.camicalor.domain.model

/** Errores de negocio del carrito: el dominio explica POR QUÉ no se pudo agregar. */
sealed class CartException(message: String) : Exception(message) {
    class OutOfStock(productId: String) : CartException("El producto '$productId' está agotado")
    class SizeNotAvailable(productId: String, size: Size) :
        CartException("El producto '$productId' no viene en talla ${size.code}")
}
