package com.helpful.stuSchedule.ui.receivingData.group

import android.os.Bundle
import androidx.core.os.bundleOf
import com.helpful.stuSchedule.data.models.receivingData.Group
import com.helpful.stuSchedule.data.models.receivingData.StudentDataContainer
import com.helpful.stuSchedule.ui.receivingData.receivingData.ReceivingDataFragment
import com.helpful.stuSchedule.ui.receivingData.receivingData.ReceivingDataViewModel

class GroupViewModel : ReceivingDataViewModel<Group, Pair<Int, Int>>() {
    override suspend fun getData(inputData: Pair<Int, Int>): List<Group> {
        return client.getGroupsByCourseAndFacultyId(inputData.first, inputData.second).also {
            dataList.value = it
        }
    }

    override fun makeStudentDataContainerBundle(whereStartsReceiving: Int): Bundle = bundleOf(
        StudentDataContainer.BUNDLE_KEY to StudentDataContainer(
            studentDataContainer.value?.structure,
            studentDataContainer.value?.faculty,
            studentDataContainer.value?.course,
            selectedData.value
        ),
        ReceivingDataFragment.WHERE_STARTS_CHANGING to whereStartsReceiving
    )
}