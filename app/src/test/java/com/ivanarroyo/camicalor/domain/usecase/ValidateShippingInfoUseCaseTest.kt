package com.ivanarroyo.camicalor.domain.usecase

import com.ivanarroyo.camicalor.domain.model.ShippingError
import com.ivanarroyo.camicalor.domain.model.ShippingField
import com.ivanarroyo.camicalor.domain.model.ShippingInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidateShippingInfoUseCaseTest {

    private val validate = ValidateShippingInfoUseCase()

    private val valid = ShippingInfo(
        fullName = "Ivan Arroyo",
        phone = "3001234567",
        address = "Calle 30 # 17-45",
        neighborhood = "Getsemaní",
        city = "Cartagena",
    )

    @Test
    fun `complete form has no errors`() {
        assertTrue(validate(valid).isEmpty())
    }

    @Test
    fun `empty fields are required`() {
        val errors = validate(ShippingInfo(city = ""))

        assertEquals(ShippingError.REQUIRED, errors[ShippingField.FULL_NAME])
        assertEquals(ShippingError.REQUIRED, errors[ShippingField.PHONE])
        assertEquals(ShippingError.REQUIRED, errors[ShippingField.ADDRESS])
        assertEquals(ShippingError.REQUIRED, errors[ShippingField.NEIGHBORHOOD])
        assertEquals(ShippingError.REQUIRED, errors[ShippingField.CITY])
    }

    @Test
    fun `phone with spaces or dashes is accepted`() {
        assertTrue(validate(valid.copy(phone = "300 123 4567")).isEmpty())
        assertTrue(validate(valid.copy(phone = "300-123-4567")).isEmpty())
    }

    @Test
    fun `phone must start with 3 and have 10 digits`() {
        assertEquals(ShippingError.INVALID_PHONE, validate(valid.copy(phone = "2001234567"))[ShippingField.PHONE])
        assertEquals(ShippingError.INVALID_PHONE, validate(valid.copy(phone = "30012345"))[ShippingField.PHONE])
        assertEquals(ShippingError.INVALID_PHONE, validate(valid.copy(phone = "300123456a"))[ShippingField.PHONE])
    }

    @Test
    fun `too short name and address are rejected`() {
        val errors = validate(valid.copy(fullName = "Iv", address = "Cll"))

        assertEquals(ShippingError.TOO_SHORT, errors[ShippingField.FULL_NAME])
        assertEquals(ShippingError.TOO_SHORT, errors[ShippingField.ADDRESS])
    }
}
