package com.ivanarroyo.camicalor.domain.usecase

import com.ivanarroyo.camicalor.domain.model.Cart
import com.ivanarroyo.camicalor.domain.model.CartException
import com.ivanarroyo.camicalor.domain.model.Category
import com.ivanarroyo.camicalor.domain.model.Product
import com.ivanarroyo.camicalor.domain.model.Size
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AddToCartUseCaseTest {

    private val repository = FakeCartRepository()
    private val addToCart = AddToCartUseCase(repository)

    private val solCaribe = Product(
        id = "cc-001", name = "Sol Caribe", description = "", price = 59900,
        category = Category.PRINTED, sizes = listOf(Size.S, Size.M), imageUrl = "", inStock = true,
    )

    @Test
    fun `adds the product in the chosen size`() = runBlocking {
        val result = addToCart(solCaribe, Size.M)

        assertTrue(result.isSuccess)
        assertEquals(1, repository.cart.value.totalItems)
        assertEquals(Size.M, repository.cart.value.items.single().size)
    }

    @Test
    fun `adding the same product and size again increases quantity`() = runBlocking {
        addToCart(solCaribe, Size.M)
        addToCart(solCaribe, Size.M)

        assertEquals(2, repository.cart.value.items.single().quantity)
    }

    @Test
    fun `same product in another size is a separate line`() = runBlocking {
        addToCart(solCaribe, Size.M)
        addToCart(solCaribe, Size.S)

        assertEquals(2, repository.cart.value.items.size)
    }

    @Test
    fun `out of stock product is rejected`() = runBlocking {
        val result = addToCart(solCaribe.copy(inStock = false), Size.M)

        assertTrue(result.exceptionOrNull() is CartException.OutOfStock)
        assertTrue(repository.cart.value.isEmpty)
    }

    @Test
    fun `size the product does not have is rejected`() = runBlocking {
        val result = addToCart(solCaribe, Size.XL)

        assertTrue(result.exceptionOrNull() is CartException.SizeNotAvailable)
        assertTrue(repository.cart.value.isEmpty)
    }

    @Test
    fun `quantity never goes above the maximum`() = runBlocking {
        repeat(Cart.MAX_QUANTITY_PER_ITEM + 3) { addToCart(solCaribe, Size.M) }

        assertEquals(Cart.MAX_QUANTITY_PER_ITEM, repository.cart.value.items.single().quantity)
    }
}
