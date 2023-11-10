package com.helpful.stuSchedule.models

class CancellationToken {
    private var cancelled = false

    fun cancel() {
        cancelled = true
    }

    fun isCancelled() = cancelled
}