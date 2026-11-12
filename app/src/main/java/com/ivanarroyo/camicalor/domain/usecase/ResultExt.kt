package com.ivanarroyo.camicalor.domain.usecase

import kotlin.coroutines.cancellation.CancellationException

/** Como runCatching, pero sin tragarse la cancelación de corrutinas. */
internal inline fun <T> runCatchingNonCancellable(block: () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
