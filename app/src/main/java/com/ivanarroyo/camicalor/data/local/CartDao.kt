package com.ivanarroyo.camicalor.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CartDao {

    /** Room vuelve a emitir automáticamente cada vez que la tabla cambia. */
    @Query("SELECT * FROM cart_items ORDER BY addedAt ASC")
    abstract fun observeAll(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE productId = :productId AND size = :size")
    abstract suspend fun find(productId: String, size: String): CartItemEntity?

    @Insert
    abstract suspend fun insert(item: CartItemEntity)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE productId = :productId AND size = :size")
    abstract suspend fun updateQuantity(productId: String, size: String, quantity: Int)

    @Query("DELETE FROM cart_items WHERE productId = :productId AND size = :size")
    abstract suspend fun delete(productId: String, size: String)

    @Query("DELETE FROM cart_items")
    abstract suspend fun clear()

    /**
     * Leer y escribir dentro de una transacción: si dos toques llegan casi al mismo tiempo,
     * no se pierde ninguna suma.
     */
    @Transaction
    open suspend fun addOrIncrement(newItem: CartItemEntity, maxQuantity: Int) {
        val existing = find(newItem.productId, newItem.size)
        if (existing == null) {
            insert(newItem)
        } else {
            val quantity = (existing.quantity + 1).coerceAtMost(maxQuantity)
            updateQuantity(existing.productId, existing.size, quantity)
        }
    }
}
