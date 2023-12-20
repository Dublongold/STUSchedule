@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.helpful.stuSchedule.tools

import com.helpful.stuSchedule.data.models.cancellationToken.CancellationToken
import kotlinx.coroutines.flow.MutableStateFlow

class CancellationTokenManager {
    private val token = MutableStateFlow(CancellationToken())

    fun cancel() {
        token.value.cancel()
    }

    fun inProcess() {
        token.value.inProcess()
    }

    fun change() {
        token.value = CancellationToken()
    }

    fun finishAndChange() {
        finish()
        change()
    }

    fun finish() {
        token.value.finish()
    }

    fun isCancelled() = token.value.isCancelled()
    fun isInProcess() = token.value.isInProcess()

    fun workIsFinished() = token.value.isCancelled() || token.value.isFinished()

    fun getToken(): CancellationToken {
        if (token.value.isFinished()) {
            change()
        }
        return token.value
    }
}