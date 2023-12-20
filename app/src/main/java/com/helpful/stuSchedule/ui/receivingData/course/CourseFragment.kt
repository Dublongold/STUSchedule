package com.helpful.stuSchedule.ui.receivingData.course

import androidx.fragment.app.viewModels
import com.helpful.stuSchedule.R
import com.helpful.stuSchedule.data.models.receivingData.Course
import com.helpful.stuSchedule.ui.receivingData.receivingData.ReceivingDataFragment

class CourseFragment : ReceivingDataFragment<Course, Int>() {
    override val viewModel: CourseViewModel by viewModels()

    override val pageTitleId: Int = R.string.course_title
    override val pageTextId: Int = R.string.course_text
    override val pageLabelId: Int = R.string.course_label
    override val pageTextArgs: Array<Any>
        get() = arrayOf(
        viewModel.studentDataContainer.value?.structure?.run {
            if (!viewModel.isEnglish)
                shortName
            else
                fullName
        } ?: getString(R.string.error),
        viewModel.studentDataContainer.value?.faculty?.run{
            if (!viewModel.isEnglish)
                shortName
            else
                fullName
        } ?: getString(R.string.error),
    )
    override val nextDestinationId: Int = R.id.from_courseFragment_to_groupFragment

    override fun getDataParameters(): Int = viewModel.studentDataContainer.value?.faculty?.id
        ?: 0

    override fun mapData(data: Course): String = data.course.toString()
}