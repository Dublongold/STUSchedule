package com.helpful.stuSchedule.helpful

import com.helpful.stuSchedule.models.CancellationToken
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