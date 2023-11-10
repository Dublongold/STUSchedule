package com.helpful.stuSchedule.views.selectWeekday

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Calendar

class SelectWeekdayViewModel : ViewModel() {
    private var privateSelectedItem = MutableStateFlow(-1)

    val selectedItem: StateFlow<Int>
        get() = privateSelectedItem

    fun getDateNow(formatString: String): String  {
        val calendar = Calendar.getInstance()
        return formatString.format(
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH] + 1,
            calendar[Calendar.DAY_OF_MONTH]
        )
    }

    fun selectItem(position: Int) {
        privateSelectedItem.value = when (position) {
            0 -> -1
            in 1 .. 5 -> position + 1
            6 -> 0
            else -> {
                Log.w("Select weekday VM", "[selectItem] Error: position == $position.")
                0
            }
        }
    }
}