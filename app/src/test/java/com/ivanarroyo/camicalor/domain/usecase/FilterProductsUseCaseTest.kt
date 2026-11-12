package com.ivanarroyo.camicalor.domain.usecase

import com.ivanarroyo.camicalor.domain.model.Category
import com.ivanarroyo.camicalor.domain.model.Product
import com.ivanarroyo.camicalor.domain.model.Size
import org.junit.Assert.assertEquals
import org.junit.Test

class FilterProductsUseCaseTest {

    private val filter = FilterProductsUseCase()

    private val solCaribe = product("cc-001", "Camiseta Sol Caribe", Category.PRINTED, "Estampado de atardecer")
    private val basicaBlanca = product("cc-004", "Básica Blanca", Category.BASIC, "Combina con todo")
    private val deportivaBrisa = product("cc-007", "Deportiva Brisa", Category.SPORT, "Tela que seca rápido")
    private val all = listOf(solCaribe, basicaBlanca, deportivaBrisa)

    @Test
    fun `empty query and no category returns everything`() {
        assertEquals(all, filter(all, query = "", category = null))
    }

    @Test
    fun `search ignores accents and uppercase`() {
        assertEquals(listOf(basicaBlanca), filter(all, query = "BASICA", category = null))
    }

    @Test
    fun `search also looks in the description`() {
        assertEquals(listOf(deportivaBrisa), filter(all, query = "rapido", category = null))
    }

    @Test
    fun `category filter keeps only that category`() {
        assertEquals(listOf(solCaribe), filter(all, query = "", category = Category.PRINTED))
    }

    @Test
    fun `query and category are combined`() {
        assertEquals(emptyList<Product>(), filter(all, query = "brisa", category = Category.BASIC))
    }

    @Test
    fun `surrounding spaces are ignored`() {
        assertEquals(listOf(solCaribe), filter(all, query = "  caribe  ", category = null))
    }

    private fun product(id: String, name: String, category: Category, description: String) = Product(
        id = id,
        name = name,
        description = description,
        price = 50000,
        category = category,
        sizes = listOf(Size.M),
        imageUrl = "",
        inStock = true,
    )
}
