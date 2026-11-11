package com.ivanarroyo.camicalor.domain.usecase

import com.ivanarroyo.camicalor.domain.model.Product
import com.ivanarroyo.camicalor.domain.repository.ProductRepository
import javax.inject.Inject

class GetProductByIdUseCase @Inject constructor(
    private val repository: ProductRepository,
) {
    suspend operator fun invoke(id: String): Result<Product> = repository.getProductById(id)
}
