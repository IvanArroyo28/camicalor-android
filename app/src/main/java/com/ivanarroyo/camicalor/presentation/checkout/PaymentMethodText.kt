package com.ivanarroyo.camicalor.presentation.checkout

import androidx.annotation.StringRes
import com.ivanarroyo.camicalor.R
import com.ivanarroyo.camicalor.domain.model.PaymentMethod

/** El dominio no sabe de textos: la traducción a strings vive en la capa de presentación. */
@StringRes
fun PaymentMethod.titleRes(): Int = when (this) {
    PaymentMethod.CASH_ON_DELIVERY -> R.string.payment_cash_on_delivery
    PaymentMethod.BANK_TRANSFER -> R.string.payment_bank_transfer
}

@StringRes
fun PaymentMethod.descriptionRes(): Int = when (this) {
    PaymentMethod.CASH_ON_DELIVERY -> R.string.payment_cash_on_delivery_description
    PaymentMethod.BANK_TRANSFER -> R.string.payment_bank_transfer_description
}
