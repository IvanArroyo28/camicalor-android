package com.ivanarroyo.camicalor.data.mapper

import com.ivanarroyo.camicalor.data.remote.dto.ProductDto
import com.ivanarroyo.camicalor.domain.model.Category
import com.ivanarroyo.camicalor.domain.model.Size
import org.junit.Assert.assertEquals
import org.junit.Test

class ProductMapperTest {

    private fun dto(category: String = "estampadas", sizes: List<String> = listOf("S", "M")) = ProductDto(
        id = "cc-001",
        name = "Camiseta Sol Caribe",
        description = "Descripción",
        price = 59900,
        category = category,
        sizes = sizes,
        imageUrl = "https://example.com/img.png",
        inStock = true,
    )

    @Test
    fun `maps known category`() {
        assertEquals(Category.PRINTED, dto(category = "estampadas").toDomain().category)
    }

    @Test
    fun `unknown category falls back to OTHER`() {
        assertEquals(Category.OTHER, dto(category = "categoria-rara").toDomain().category)
    }

    @Test
    fun `invalid sizes are discarded`() {
        assertEquals(listOf(Size.S, Size.XL), dto(sizes = listOf("S", "XXXL", "xl")).toDomain().sizes)
    }

    @Test
    fun `price is kept as integer pesos`() {
        assertEquals(59900L, dto().toDomain().price)
    }
}
