package com.helpful.dpuschedule.helpful.objects

import android.util.Log
import java.util.Calendar

object TimeConverter {
    private const val LOG_TAG = "Time converter"
    private const val DEFAULT_TIME = "00:00:00"
    private const val resultString = "%d-%02d-%02d"
    private val magicNumberMonth = listOf(0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4)
    /** Format: HH-MM-SS */
    fun convertToMinutes(time: String?): Int {
        val timeVar = takeInNormalFormat(time)

        val result = timeVar.split(':').map(String::toInt).let {
            it[0] * 60 + it[1]
        }
        return result
    }

    fun convertToSeconds(time: String?): Int {
        val timeVar = takeInNormalFormat(time)
        val result = timeVar.split(':').map(String::toInt).let {
            it[0] * 60 * 60 + it[1] * 60 + it[2]
        }
        return result
    }

    private fun takeInNormalFormat(time: String?): String {
        if(time == null) {
            Log.w(LOG_TAG, "Time is null. \"$DEFAULT_TIME\" returned.")
            return DEFAULT_TIME
        }
        val result = if (time.count { it == ':' } == 1) "$time:00" else time
        if(result.count { it == ':' } == 0) {
            Log.w(LOG_TAG, "Time does not contain ':' characters. \"$DEFAULT_TIME\" returned.")
            return DEFAULT_TIME
        }
        else if(result.count {it == ':'} > 2) {
            Log.w(LOG_TAG, "Time contains to many ':' characters " +
                    "(${result.count { it == ':' }}). \"$DEFAULT_TIME\" returned.")
        }
        return result
    }
    // 0(Su), 1(Mo), 2(Tu), 3(We), 4(Th), 5(Fr), 6(Sa)
    fun getDayOfWeek(dateNow: String?): Int {
        if (dateNow == null) return -1
        var (year, month, day) = dateNow
            .split("-")
            .map { it.toInt() }
        if (month < 3) {
            year -= 1
        }
        return (year + year / 4 - year / 100 +
                year / 400 + magicNumberMonth[month - 1] + day) % 7
    }

    private fun takeDateDependsOnSelectedWeekday(weekday: Int): String {
        val datetime = Calendar.getInstance()
        if(datetime[Calendar.DAY_OF_WEEK] <= weekday) {
            datetime.add(Calendar.DAY_OF_WEEK, weekday - datetime[Calendar.DAY_OF_WEEK])
        }
        else {
            datetime.add(Calendar.DAY_OF_WEEK, 7 - datetime[Calendar.DAY_OF_WEEK] + weekday)
        }
        return resultString.format(
            datetime[Calendar.YEAR],
            datetime[Calendar.MONTH] + 1,
            datetime[Calendar.DAY_OF_MONTH]
        )
    }

    fun takeDateOrReturnSelectedDate(weekday: Int, selectedDate: String?) = selectedDate
        ?: takeDateDependsOnSelectedWeekday(weekday)
}