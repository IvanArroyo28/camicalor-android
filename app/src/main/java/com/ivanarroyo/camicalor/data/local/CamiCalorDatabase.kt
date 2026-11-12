package com.ivanarroyo.camicalor.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Base de datos local. Si en el futuro cambia la tabla, se sube [version]
 * y se agrega una migración para no perder los carritos de los usuarios.
 */
@Database(entities = [CartItemEntity::class], version = 1, exportSchema = false)
abstract class CamiCalorDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
}
