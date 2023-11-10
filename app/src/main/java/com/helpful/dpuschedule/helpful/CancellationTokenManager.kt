package com.helpful.dpuschedule.helpful

import com.helpful.dpuschedule.models.CancellationToken
import kotlinx.coroutines.flow.MutableStateFlow

class CancellationTokenManager {
    private val token = MutableStateFlow(CancellationToken())

    fun cancel() {
        token.value.cancel()
    }

    fun change() {
        token.value = CancellationToken()
    }

    fun cancelAndChange() {
        cancel()
        change()
    }

    fun isCancelled() = token.value.isCancelled()

    fun getToken(): CancellationToken {
        return token.value
    }
}