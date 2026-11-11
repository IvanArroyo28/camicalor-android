package com.ivanarroyo.camicalor.domain.model

enum class Category(val id: String, val displayName: String) {
    PRINTED("estampadas", "Estampadas"),
    BASIC("basicas", "Básicas"),
    SPORT("deportivas", "Deportivas"),
    OVERSIZE("oversize", "Oversize"),
    OTHER("otras", "Otras");

    companion object {
        /** Si el JSON trae una categoría desconocida, no rompemos la app: va a OTHER. */
        fun fromId(id: String): Category = entries.firstOrNull { it.id == id } ?: OTHER
    }
}
