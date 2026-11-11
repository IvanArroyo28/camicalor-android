package com.ivanarroyo.camicalor.data.mapper

import com.ivanarroyo.camicalor.data.remote.dto.ProductDto
import com.ivanarroyo.camicalor.domain.model.Category
import com.ivanarroyo.camicalor.domain.model.Product
import com.ivanarroyo.camicalor.domain.model.Size

/** Convierte el DTO (formato del JSON) en el modelo de dominio. */
fun ProductDto.toDomain(): Product = Product(
    id = id,
    name = name,
    description = description,
    price = price,
    category = Category.fromId(category),
    sizes = sizes.mapNotNull { Size.fromCode(it) },
    imageUrl = imageUrl,
    inStock = inStock,
)
