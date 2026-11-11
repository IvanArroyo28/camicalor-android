package com.ivanarroyo.camicalor.data.remote.dto

import kotlinx.serialization.Serializable

/** Refleja exactamente la forma de products.json. */
@Serializable
data class ProductsResponseDto(
    val products: List<ProductDto>,
)

@Serializable
data class ProductDto(
    val id: String,
    val name: String,
    val description: String,
    val price: Long,
    val category: String,
    val sizes: List<String>,
    val imageUrl: String,
    val inStock: Boolean,
)
