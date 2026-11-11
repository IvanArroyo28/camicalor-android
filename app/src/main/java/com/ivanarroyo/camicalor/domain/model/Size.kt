package com.ivanarroyo.camicalor.domain.model

enum class Size(val code: String) {
    S("S"), M("M"), L("L"), XL("XL");

    companion object {
        /** Devuelve null si la talla no es válida, para poder descartarla. */
        fun fromCode(code: String): Size? = entries.firstOrNull { it.code.equals(code, ignoreCase = true) }
    }
}
