package com.helpful.stuSchedule.models.cancellationToken

import kotlinx.coroutines.flow.MutableStateFlow

class CancellationToken {
    private var cancelled: MutableStateFlow<CancellationTokenStates> =
        MutableStateFlow(CancellationTokenStates.CREATED)

    fun cancel() {
        cancelled.value = CancellationTokenStates.CANCELLED
    }

    fun inProcess() {
        cancelled.value = CancellationTokenStates.IN_PROCESS
    }

    fun finish() {
        cancelled.value = CancellationTokenStates.FINISHED
    }

    fun isCancelled() = cancelled.value == CancellationTokenStates.CANCELLED
    fun isFinished() = cancelled.value == CancellationTokenStates.FINISHED
    fun isInProcess() = cancelled.value == CancellationTokenStates.IN_PROCESS
}