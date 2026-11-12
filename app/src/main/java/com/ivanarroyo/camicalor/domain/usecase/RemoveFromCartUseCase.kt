package com.ivanarroyo.camicalor.domain.usecase

import com.ivanarroyo.camicalor.domain.model.Size
import com.ivanarroyo.camicalor.domain.repository.CartRepository
import javax.inject.Inject

class RemoveFromCartUseCase @Inject constructor(
    private val repository: CartRepository,
) {
    suspend operator fun invoke(productId: String, size: Size) = repository.removeItem(productId, size)
}
