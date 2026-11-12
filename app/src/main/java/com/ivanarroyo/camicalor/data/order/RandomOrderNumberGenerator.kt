package com.ivanarroyo.camicalor.data.order

import com.ivanarroyo.camicalor.domain.service.OrderNumberGenerator
import javax.inject.Inject
import kotlin.random.Random

/** Número de pedido tipo "CC-4821". Suficiente para una demo; un servidor real daría el id. */
class RandomOrderNumberGenerator @Inject constructor() : OrderNumberGenerator {
    override fun next(): String = "CC-${Random.nextInt(1000, 10000)}"
}
