package com.helpful.stuSchedule.ui.receivingData.faculty

import android.os.Bundle
import androidx.core.os.bundleOf
import com.helpful.stuSchedule.data.models.receivingData.Faculty
import com.helpful.stuSchedule.data.models.receivingData.StudentDataContainer
import com.helpful.stuSchedule.ui.receivingData.receivingData.ReceivingDataFragment
import com.helpful.stuSchedule.ui.receivingData.receivingData.ReceivingDataViewModel

class FacultyViewModel: ReceivingDataViewModel<Faculty, Int>() {
    override suspend fun getData(inputData: Int): List<Faculty> {
        return client.getFacultiesByStructureId(inputData).also {
            dataList.value = it
        }
    }

    override fun makeStudentDataContainerBundle(whereStartsReceiving: Int): Bundle = bundleOf(
        StudentDataContainer.BUNDLE_KEY to StudentDataContainer(
            studentDataContainer.value?.structure,
            selectedData.value,
            null,
            null
        ),
        ReceivingDataFragment.WHERE_STARTS_CHANGING to whereStartsReceiving
    )
}