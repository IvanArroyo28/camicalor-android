package com.ivanarroyo.camicalor.domain.usecase

import com.ivanarroyo.camicalor.domain.model.Category
import com.ivanarroyo.camicalor.domain.model.Product
import java.text.Normalizer
import javax.inject.Inject

/**
 * Filtra el catálogo por texto y categoría.
 * Es una función pura (sin red ni estado), así que es fácil de testear.
 */
class FilterProductsUseCase @Inject constructor() {

    operator fun invoke(
        products: List<Product>,
        query: String,
        category: Category?,
    ): List<Product> {
        val normalizedQuery = query.normalizeForSearch()
        return products.filter { product ->
            val matchesCategory = category == null || product.category == category
            val matchesQuery = normalizedQuery.isEmpty() ||
                product.name.normalizeForSearch().contains(normalizedQuery) ||
                product.description.normalizeForSearch().contains(normalizedQuery)
            matchesCategory && matchesQuery
        }
    }

    /** "  Básica " -> "basica": ignora mayúsculas, tildes y espacios sobrantes. */
    private fun String.normalizeForSearch(): String =
        Normalizer.normalize(trim().lowercase(), Normalizer.Form.NFD)
            .replace(DIACRITICS, "")

    private companion object {
        val DIACRITICS = Regex("\\p{Mn}+")
    }
}
