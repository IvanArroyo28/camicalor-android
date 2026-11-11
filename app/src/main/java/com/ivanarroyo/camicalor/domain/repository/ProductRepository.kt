package com.ivanarroyo.camicalor.domain.repository

import com.ivanarroyo.camicalor.domain.model.Product

/**
 * Contrato que define el dominio. La capa de datos lo implementa
 * (inversión de dependencias): el dominio no sabe de dónde salen los productos.
 */
interface ProductRepository {
    suspend fun getProducts(): Result<List<Product>>

    /** Falla con [com.ivanarroyo.camicalor.domain.model.ProductNotFoundException] si no existe. */
    suspend fun getProductById(id: String): Result<Product>
}
