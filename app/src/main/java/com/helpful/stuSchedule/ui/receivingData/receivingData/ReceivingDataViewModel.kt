package com.helpful.stuSchedule.ui.receivingData.receivingData

import android.os.Bundle
import com.helpful.stuSchedule.data.network.AsuClient
import com.helpful.stuSchedule.data.models.receivingData.StudentDataContainer
import com.helpful.stuSchedule.views.ModifiedViewModel
import kotlinx.coroutines.flow.MutableStateFlow

abstract class ReceivingDataViewModel<T, P>: ModifiedViewModel() {
    protected val client = AsuClient()

    protected val selectedData =
        MutableStateFlow<T?>(null)
    protected val dataList =
        MutableStateFlow<List<T>?>(null)
    var studentDataContainer =
        MutableStateFlow<StudentDataContainer?>(null)

    val selectedGroup
        get() = selectedData.value!!

    abstract suspend fun getData(inputData: P): List<T>

    fun selectData(position: Int) {
        dataList.value?.let {
            selectedData.value = it[position]
        }
    }

    abstract fun makeStudentDataContainerBundle(whereStartsReceiving: Int): Bundle
}