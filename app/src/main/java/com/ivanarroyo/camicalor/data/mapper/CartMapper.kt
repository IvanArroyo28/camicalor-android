package com.ivanarroyo.camicalor.data.mapper

import com.ivanarroyo.camicalor.data.local.CartItemEntity
import com.ivanarroyo.camicalor.domain.model.CartItem
import com.ivanarroyo.camicalor.domain.model.Product
import com.ivanarroyo.camicalor.domain.model.Size

/** Devuelve null si la talla guardada ya no es válida, para descartarla sin romper la app. */
fun CartItemEntity.toDomain(): CartItem? {
    val validSize = Size.fromCode(size) ?: return null
    return CartItem(
        productId = productId,
        name = name,
        imageUrl = imageUrl,
        unitPrice = unitPrice,
        size = validSize,
        quantity = quantity,
    )
}

fun Product.toCartEntity(size: Size, addedAt: Long) = CartItemEntity(
    productId = id,
    size = size.code,
    name = name,
    imageUrl = imageUrl,
    unitPrice = price,
    quantity = 1,
    addedAt = addedAt,
)
