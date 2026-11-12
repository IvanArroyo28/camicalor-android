package com.ivanarroyo.camicalor.domain.model

data class ShippingInfo(
    val fullName: String = "",
    val phone: String = "",
    val address: String = "",
    val neighborhood: String = "",
    val city: String = "Cartagena",
) {
    fun trimmed() = ShippingInfo(fullName.trim(), phone.trim(), address.trim(), neighborhood.trim(), city.trim())
}

enum class ShippingField { FULL_NAME, PHONE, ADDRESS, NEIGHBORHOOD, CITY }

enum class ShippingError { REQUIRED, TOO_SHORT, INVALID_PHONE }
