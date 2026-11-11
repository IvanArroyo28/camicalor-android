package com.ivanarroyo.camicalor.presentation.util

import java.text.NumberFormat
import java.util.Locale

private val copFormat: NumberFormat =
    NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO")).apply {
        maximumFractionDigits = 0
    }

/** 59900 -> "$ 59.900" */
fun Long.toCopPrice(): String = copFormat.format(this)
