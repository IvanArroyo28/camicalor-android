package com.ivanarroyo.camicalor.domain.service

/**
 * Genera el número de pedido. Es una interfaz para que en los tests
 * podamos usar uno fijo y saber exactamente qué número esperar.
 */
fun interface OrderNumberGenerator {
    fun next(): String
}
