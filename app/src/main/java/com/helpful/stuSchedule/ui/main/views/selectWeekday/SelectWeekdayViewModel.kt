package com.helpful.stuSchedule.ui.main.views.selectWeekday

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.os.bundleOf
import com.helpful.stuSchedule.data.models.DateContainer
import com.helpful.stuSchedule.tools.objects.DateContainerBuilder
import com.helpful.stuSchedule.tools.objects.TimeConverter
import com.helpful.stuSchedule.R
import com.helpful.stuSchedule.ui.main.views.schedule.ScheduleFragment
import com.helpful.stuSchedule.settings.SettingsOfUser
import com.helpful.stuSchedule.views.ModifiedViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Calendar

class SelectWeekdayViewModel : ModifiedViewModel() {
    private val mutableSelectedItem = MutableStateFlow(-1)
    private val mutableCurrentDateFormat = MutableStateFlow("")
    private val savedDateFormat = MutableStateFlow(selectedDateFormat.value)

    val selectedItem: StateFlow<Int>
        get() = mutableSelectedItem
    val mutableWeekdays = MutableStateFlow<Array<String>>(emptyArray())

    fun sameDateFormats(): Boolean {
        return selectedDateFormat.value == savedDateFormat.value
    }

    fun saveDateFormat() {
        savedDateFormat.value = selectedDateFormat.value
    }
    fun getDateNow(): String  {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH] + 1
        val day = calendar[Calendar.DAY_OF_MONTH]
        val (first, second, third) = when (selectedDateFormat.value) {
            SettingsOfUser.SELECTED_DATE_FORMAT_EUROPA -> listOf(day, month, year)
            SettingsOfUser.SELECTED_DATE_FORMAT_AMERICA -> listOf(month, day, year)
            SettingsOfUser.SELECTED_DATE_FORMAT_REVERSED -> listOf(year, month, day)
            SettingsOfUser.SELECTED_DATE_FORMAT_REVERSED_AMERICA -> listOf(year, day, month)
            else -> listOf(day, month, year)
        }
        return mutableCurrentDateFormat.value.format(first, second, third)
    }

    private fun convertDateToApiDate(date: String): String {
        val (day, month, year) = TimeConverter.fromDateToDayMonthAndYear(
            date,
            selectedDateFormat.value
        )
        val result = mutableCurrentDateFormat.value.format(year, month, day)
        Log.i(LOG_TAG, "[convertDateToApiDate] Result: $result")
        return result
    }

    fun showDateErrorMessage(context: Context, dateContainer: DateContainer) {
        val resources = context.resources
        val currentYear = Calendar.getInstance()[Calendar.YEAR]
        val errorMessages = resources.getStringArray(R.array.date_error_messages)
        val errorMessage = when(dateContainer.errorType) {
            0, 2 -> errorMessages[dateContainer.errorType]
            1 -> {
                val dateFormats = resources.getStringArray(R.array.date_formats)
                val currentDateFormat = dateFormats[selectedDateFormat.value]
                errorMessages[dateContainer.errorType].format(currentDateFormat)
            }
            3 -> errorMessages[dateContainer.errorType].format(currentYear - 3, currentYear, dateContainer.year)
            4 -> errorMessages[dateContainer.errorType].format(dateContainer.month)
            5 -> errorMessages[dateContainer.errorType].format(
                DateContainerBuilder.lastDayOfMonth(dateContainer.month, dateContainer.year), dateContainer.dayOfMonth
            )
            else -> errorMessages.last()
        }
        Toast.makeText(
            context,
            resources.getString(
                R.string.date_error_message_format_string,
                resources.getString(R.string.invalid_date),
                errorMessage
            ),
            Toast.LENGTH_SHORT
        ).show()
    }

    fun setCurrentDateFormat(dateFormat: String) {
        mutableCurrentDateFormat.value = dateFormat
    }

    fun selectItem(position: Int) {
        mutableSelectedItem.value = when (position) {
            0 -> -1
            in 1 .. 5 -> position + 1
            6 -> 0
            else -> {
                Log.w("Select weekday VM", "[selectItem] Error: position == $position.")
                0
            }
        }
    }

    fun getScheduleBundle(selectedDate: String) = bundleOf(
        ScheduleFragment.ARGUMENT_WEEKDAY to selectedItem.value,
        ScheduleFragment.ARGUMENT_SELECTED_DATE to
                when(selectedItem.value) {
                    0 -> convertDateToApiDate(selectedDate)
                    -1 -> convertDateToApiDate(getDateNow())
                    else -> null
                }
    )

    companion object {
        @Suppress("unused")
        private const val LOG_TAG = "Select weekday VM"
    }
}