package com.helpful.stuSchedule.data.models

data class DateContainer(
    val year: Int?,
    val month: Int?,
    val dayOfMonth: Int?,
    val errorType: Int,
) {
    companion object {
        const val LOG_TAG = "Data container"
    }
}