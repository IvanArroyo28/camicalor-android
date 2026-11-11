package com.ivanarroyo.camicalor.data.repository

import com.ivanarroyo.camicalor.data.mapper.toDomain
import com.ivanarroyo.camicalor.data.remote.CamiCalorApi
import com.ivanarroyo.camicalor.domain.model.Product
import com.ivanarroyo.camicalor.domain.repository.ProductRepository
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class ProductRepositoryImpl @Inject constructor(
    private val api: CamiCalorApi,
) : ProductRepository {

    override suspend fun getProducts(): Result<List<Product>> =
        try {
            Result.success(api.getProducts().products.map { it.toDomain() })
        } catch (e: CancellationException) {
            // Nunca tragarse la cancelación de una corrutina: se relanza.
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
}
