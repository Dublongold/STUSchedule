package com.helpful.stuSchedule.tools.objects

import android.util.Log
import com.helpful.stuSchedule.data.models.DateContainer
import com.helpful.stuSchedule.settings.SettingsOfUser
import java.util.Calendar

object DateContainerBuilder {
    const val separators = """[\.-\\/\s]"""
    private val dateRegex =
        Regex("""\d\d?$separators.\d\d?$separators.\d\d?\d?\d?""")
    private val reversedDateRegex =
        Regex("""\d\d?\d?\d?$separators.\d\d?$separators.\d\d?""")
    fun checkUserDate(selectedDate: String, selectedDateFormat: Int): DateContainer {
        val currentDateRegex = if(selectedDateFormat in listOf(
                SettingsOfUser.SELECTED_DATE_FORMAT_EUROPA,
                SettingsOfUser.SELECTED_DATE_FORMAT_AMERICA,
            )) dateRegex else reversedDateRegex
        var year: Int = -1
        var month: Int = -1
        var day: Int = -1
        var errorType: Int = -1

        val currentYear = Calendar.getInstance()[Calendar.YEAR]

        if(currentDateRegex.matches(selectedDate)) {
            val (tempDay, tempMonth, tempYear) = TimeConverter.fromDateToDayMonthAndYear(
                selectedDate,
                selectedDateFormat
            )

            if (tempDay != 0 && tempMonth != 0 && tempYear != 0) {
                day = tempDay
                month = tempMonth
                year = tempYear
            }

            val conditions = listOf(
                day != -1 && month != -1 && year != -1,
                year in (currentYear - 3)..currentYear,
                month in 1..12,
                isCorrectDay(day, month, year)
            )
            // If all conditions is false!!!
            if( conditions.any { !it }) {
                errorType = conditions.indexOfFirst { !it } + 2
            }
        }
        else {
            errorType = 1
        }
        Log.i(
            LOG_TAG, "[checkUserDate] year: $year, month: $month, day: $day, errorType: $errorType;" +
                "\nselectedDate: $selectedDate, selectedDateFormat: $selectedDateFormat")
        return DateContainer(year, month, day, errorType)
    }
    private fun isLeap(year: Int?) = year != null && year % 4 == 0 && year % 100 != 0
            || year != null && year % 400 == 0

    private fun isCorrectDay(day: Int?, month: Int?, year: Int?): Boolean {
        return day in 1..lastDayOfMonth(month, year)
    }

    fun lastDayOfMonth(month: Int?, year: Int?): Int {
        return when(month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            2 -> if(isLeap(year)) 29 else 28
            4, 6, 9, 11 -> 30
            else -> 0
        }
    }

    const val LOG_TAG = "Date container builder"
}