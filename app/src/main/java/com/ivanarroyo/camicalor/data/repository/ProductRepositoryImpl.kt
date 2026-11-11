package com.ivanarroyo.camicalor.data.repository

import com.ivanarroyo.camicalor.data.mapper.toDomain
import com.ivanarroyo.camicalor.data.remote.CamiCalorApi
import com.ivanarroyo.camicalor.domain.model.Product
import com.ivanarroyo.camicalor.domain.model.ProductNotFoundException
import com.ivanarroyo.camicalor.domain.repository.ProductRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class ProductRepositoryImpl @Inject constructor(
    private val api: CamiCalorApi,
) : ProductRepository {

    /**
     * Caché en memoria: el catálogo se descarga una sola vez mientras la app vive.
     * Así, al abrir el detalle de una camiseta no se vuelve a descargar todo.
     */
    private var cache: List<Product>? = null
    private val mutex = Mutex()

    override suspend fun getProducts(): Result<List<Product>> = safeCall {
        loadProducts()
    }

    override suspend fun getProductById(id: String): Result<Product> = safeCall {
        loadProducts().firstOrNull { it.id == id } ?: throw ProductNotFoundException(id)
    }

    /** El Mutex evita que dos pantallas descarguen el catálogo al mismo tiempo. */
    private suspend fun loadProducts(): List<Product> = mutex.withLock {
        cache ?: api.getProducts().products
            .map { it.toDomain() }
            .also { cache = it }
    }

    private inline fun <T> safeCall(block: () -> T): Result<T> =
        try {
            Result.success(block())
        } catch (e: CancellationException) {
            // Nunca tragarse la cancelación de una corrutina: se relanza.
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
}
