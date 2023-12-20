package com.helpful.stuSchedule.ui.others.bellSchedule

import androidx.lifecycle.viewModelScope
import com.helpful.stuSchedule.data.models.other.BellScheduleItem
import com.helpful.stuSchedule.data.network.OtherClient
import com.helpful.stuSchedule.views.ModifiedViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BellScheduleViewModel : ModifiedViewModel() {
    private val client = MutableStateFlow(OtherClient())
    private val mutableSchedule: MutableStateFlow<List<BellScheduleItem?>> =
        MutableStateFlow(emptyList())

    val schedule: StateFlow<List<BellScheduleItem?>>
        get() = mutableSchedule
    fun loadSchedule() {
        viewModelScope.launch {
            val schedule: MutableList<BellScheduleItem?> = client.value.getBellSchedule()
                .toMutableList()
            if (schedule.size > 1) {
                for ((additionalIndex, i) in (1..<schedule.size).withIndex()) {
                    schedule.add(i + additionalIndex, null)
                }
            }
            mutableSchedule.value = schedule
        }
    }
}