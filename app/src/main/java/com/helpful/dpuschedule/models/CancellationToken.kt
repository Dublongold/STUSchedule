package com.helpful.dpuschedule.models

class CancellationToken {
    private var cancelled = false

    fun cancel() {
        cancelled = true
    }

    fun isCancelled() = cancelled
}