package com.helpful.stuSchedule.models

import com.google.gson.annotations.SerializedName

data class WordTime (
    var datetime: String,
    @SerializedName("day_of_week")
    val dayOfWeek: Int
    // API give some more properties, but there need only this two.
) {
    val date: String
        get() = datetime.substring(0, datetime.indexOf('T'))
    val time: String
        get() = datetime.substring(datetime.indexOf('T') + 1, datetime.indexOf('.'))
    companion object {
        const val DEFAULT_DATETIME = "0000-00-00T00:00:00.000000+00:00"
        const val DEFAULT_DATE = "0000-00-00"
        val DEFAULT
            get() = WordTime(DEFAULT_DATETIME, 1)
    }
}