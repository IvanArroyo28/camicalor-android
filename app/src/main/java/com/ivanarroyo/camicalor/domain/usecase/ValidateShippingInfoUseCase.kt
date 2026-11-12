package com.ivanarroyo.camicalor.domain.usecase

import com.ivanarroyo.camicalor.domain.model.ShippingError
import com.ivanarroyo.camicalor.domain.model.ShippingField
import com.ivanarroyo.camicalor.domain.model.ShippingInfo
import javax.inject.Inject

/** Devuelve los errores por campo. Un mapa vacío significa que el formulario es válido. */
class ValidateShippingInfoUseCase @Inject constructor() {

    operator fun invoke(info: ShippingInfo): Map<ShippingField, ShippingError> = buildMap {
        val name = info.fullName.trim()
        when {
            name.isEmpty() -> put(ShippingField.FULL_NAME, ShippingError.REQUIRED)
            name.length < MIN_NAME_LENGTH -> put(ShippingField.FULL_NAME, ShippingError.TOO_SHORT)
        }

        // Se aceptan espacios y guiones: "300 123 4567" o "300-123-4567".
        val phoneDigits = info.phone.filterNot { it.isWhitespace() || it == '-' }
        when {
            phoneDigits.isEmpty() -> put(ShippingField.PHONE, ShippingError.REQUIRED)
            !COLOMBIAN_MOBILE.matches(phoneDigits) -> put(ShippingField.PHONE, ShippingError.INVALID_PHONE)
        }

        val address = info.address.trim()
        when {
            address.isEmpty() -> put(ShippingField.ADDRESS, ShippingError.REQUIRED)
            address.length < MIN_ADDRESS_LENGTH -> put(ShippingField.ADDRESS, ShippingError.TOO_SHORT)
        }

        if (info.neighborhood.isBlank()) put(ShippingField.NEIGHBORHOOD, ShippingError.REQUIRED)
        if (info.city.isBlank()) put(ShippingField.CITY, ShippingError.REQUIRED)
    }

    companion object {
        const val MIN_NAME_LENGTH = 3
        const val MIN_ADDRESS_LENGTH = 6

        /** Celular colombiano: 10 dígitos y empieza por 3. */
        private val COLOMBIAN_MOBILE = Regex("^3\\d{9}$")
    }
}
