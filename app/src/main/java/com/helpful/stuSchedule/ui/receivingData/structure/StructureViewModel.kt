package com.helpful.stuSchedule.ui.receivingData.structure

import android.os.Bundle
import androidx.core.os.bundleOf
import com.helpful.stuSchedule.data.models.receivingData.Structure
import com.helpful.stuSchedule.data.models.receivingData.StudentDataContainer
import com.helpful.stuSchedule.ui.receivingData.receivingData.ReceivingDataFragment
import com.helpful.stuSchedule.ui.receivingData.receivingData.ReceivingDataViewModel

class StructureViewModel: ReceivingDataViewModel<Structure, Int>() {
    override suspend fun getData(inputData: Int): List<Structure> {
        return client.getStructures().also {
            dataList.value = it
        }
    }

    override fun makeStudentDataContainerBundle(whereStartsReceiving: Int): Bundle = bundleOf(
        StudentDataContainer.BUNDLE_KEY to StudentDataContainer(
            selectedData.value,
            null,
            null,
            null
        ),
        ReceivingDataFragment.WHERE_STARTS_CHANGING to whereStartsReceiving
    )
}