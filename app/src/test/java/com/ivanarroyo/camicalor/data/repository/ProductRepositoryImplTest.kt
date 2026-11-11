package com.ivanarroyo.camicalor.data.repository

import com.ivanarroyo.camicalor.data.remote.CamiCalorApi
import com.ivanarroyo.camicalor.data.remote.dto.ProductDto
import com.ivanarroyo.camicalor.data.remote.dto.ProductsResponseDto
import com.ivanarroyo.camicalor.domain.model.ProductNotFoundException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class ProductRepositoryImplTest {

    /** API falsa: no toca internet y cuenta cuántas veces la llamaron. */
    private class FakeApi(var failWith: Exception? = null) : CamiCalorApi {
        var calls = 0
        override suspend fun getProducts(): ProductsResponseDto {
            calls++
            failWith?.let { throw it }
            return ProductsResponseDto(listOf(dto("cc-001"), dto("cc-002")))
        }
    }

    @Test
    fun `catalog is downloaded only once thanks to the cache`() = runBlocking {
        val api = FakeApi()
        val repository = ProductRepositoryImpl(api)

        repository.getProducts()
        repository.getProducts()
        repository.getProductById("cc-002")

        assertEquals(1, api.calls)
    }

    @Test
    fun `getProductById returns the matching product`() = runBlocking {
        val result = ProductRepositoryImpl(FakeApi()).getProductById("cc-002")

        assertEquals("cc-002", result.getOrThrow().id)
    }

    @Test
    fun `getProductById fails with ProductNotFoundException for unknown id`() = runBlocking {
        val result = ProductRepositoryImpl(FakeApi()).getProductById("no-existe")

        assertTrue(result.exceptionOrNull() is ProductNotFoundException)
    }

    @Test
    fun `network error is returned as failure and next call retries`() = runBlocking {
        val api = FakeApi(failWith = IOException("sin red"))
        val repository = ProductRepositoryImpl(api)

        val first = repository.getProducts()
        api.failWith = null
        val second = repository.getProducts()

        assertTrue(first.exceptionOrNull() is IOException)
        assertTrue(second.isSuccess)
        assertEquals(2, api.calls)
    }

    private companion object {
        fun dto(id: String) = ProductDto(
            id = id,
            name = "Camiseta $id",
            description = "",
            price = 50000,
            category = "basicas",
            sizes = listOf("M"),
            imageUrl = "",
            inStock = true,
        )
    }
}
