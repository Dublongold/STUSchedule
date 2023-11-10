package com.helpful.dpuschedule.models

import java.util.Calendar

data class DateContainer(
    val year: Int?,
    val month: Int?,
    val dayOfMonth: Int?,
    val errorType: Int,
) {
    companion object {
        private val dateRegex = Regex("""\d\d\d\d-\d\d?-\d\d?""")
        fun checkUserDate(selectedDate: String): DateContainer {
            var year: Int? = null
            var month: Int? = null
            var day: Int? = null
            var errorType: Int = -1

            val currentYear = Calendar.getInstance()[Calendar.YEAR]

            if(dateRegex.matches(selectedDate)) {
                year = selectedDate.substring(0, selectedDate.indexOfFirst { it == '-' })
                    .toIntOrNull()
                month = selectedDate.substring(
                    selectedDate.indexOfFirst { it == '-' } + 1,
                    selectedDate.indexOfLast { it == '-' }
                ).toIntOrNull()
                day = selectedDate.substring(
                    selectedDate.indexOfLast { it == '-' } + 1,
                    selectedDate.length
                ).toIntOrNull()
                val conditions = listOf(year != null && month != null && day != null,
                    year in (currentYear - 3)..currentYear,
                    month in 1..12,
                    isCorrectDay(day, month, year))
                // If all conditions is false!!!
                if( conditions.any { !it }) {
                    errorType = conditions.indexOfFirst { !it } + 2
                }
            }
            else {
                errorType = 1
            }
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
    }
}