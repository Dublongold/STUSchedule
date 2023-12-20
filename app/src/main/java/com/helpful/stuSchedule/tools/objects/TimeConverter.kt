package com.helpful.stuSchedule.tools.objects

import android.util.Log
import com.helpful.stuSchedule.settings.SettingsOfUser
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

    @Suppress("unused")
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

    /**
     * @return List of date elements in the next order: day, month, year. List size is always 3.
     */
    fun fromDateToDayMonthAndYear(
        date: String,
        currentDateFormat: Int = SettingsOfUser.SELECTED_DATE_FORMAT_REVERSED
    ): List<Int> {
        val dateElements = listOf(
            // First.
            date.substring(0, date.indexOfFirst { DateContainerBuilder.separators.contains(it) })
                .toIntOrNull(),
            // Second.
            date.substring(
                date.indexOfFirst { DateContainerBuilder.separators.contains(it) } + 1,
                date.indexOfLast { DateContainerBuilder.separators.contains(it) }
            ).toIntOrNull(),
            // Third.
            date.substring(
                date.indexOfLast { DateContainerBuilder.separators.contains(it) } + 1,
                date.length
            ).toIntOrNull()
        )

        if (dateElements.all { it != null }) {
            val notNullDateElements = dateElements.filterNotNull()
            val indices = when (currentDateFormat) {
                // DD/MM/YYYY
                SettingsOfUser.SELECTED_DATE_FORMAT_EUROPA -> {
                    listOf(0, 1, 2)
                }
                // MM/DD/YYYY
                SettingsOfUser.SELECTED_DATE_FORMAT_AMERICA -> {
                    listOf(1, 0, 2)
                }
                // YYYY/MM/DD
                SettingsOfUser.SELECTED_DATE_FORMAT_REVERSED -> {
                    listOf(2, 1, 0)
                }
                // YYYY/DD/MM
                SettingsOfUser.SELECTED_DATE_FORMAT_REVERSED_AMERICA -> {
                    listOf(1, 2, 0)
                }
                else -> throw IllegalArgumentException("Why your date format is " +
                        "invalid? ($currentDateFormat)")
            }
            Log.i(LOG_TAG, "[fromDateToDayMonthAndYear] Not null date elements: $notNullDateElements, indices: $indices, selectedDateFormat: $currentDateFormat")
            return listOf(
                notNullDateElements[indices[0]],
                notNullDateElements[indices[1]],
                notNullDateElements[indices[2]]
            )
        }
        return listOf(0, 0, 0)
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