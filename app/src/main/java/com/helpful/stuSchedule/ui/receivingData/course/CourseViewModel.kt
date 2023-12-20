package com.helpful.stuSchedule.ui.receivingData.course

import android.os.Bundle
import androidx.core.os.bundleOf
import com.helpful.stuSchedule.data.models.receivingData.Course
import com.helpful.stuSchedule.data.models.receivingData.StudentDataContainer
import com.helpful.stuSchedule.ui.receivingData.receivingData.ReceivingDataFragment
import com.helpful.stuSchedule.ui.receivingData.receivingData.ReceivingDataViewModel

class CourseViewModel : ReceivingDataViewModel<Course, Int>() {
    override suspend fun getData(inputData: Int): List<Course> {
        return client.getCoursesByFacultyId(inputData).also {
            dataList.value = it
        }
    }

    override fun makeStudentDataContainerBundle(whereStartsReceiving: Int): Bundle = bundleOf(
        StudentDataContainer.BUNDLE_KEY to StudentDataContainer(
            studentDataContainer.value?.structure,
            studentDataContainer.value?.faculty,
            selectedData.value,
            null
        ),
        ReceivingDataFragment.WHERE_STARTS_CHANGING to whereStartsReceiving
    )
}