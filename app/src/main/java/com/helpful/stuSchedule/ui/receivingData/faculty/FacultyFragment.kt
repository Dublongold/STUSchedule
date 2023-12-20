package com.helpful.stuSchedule.ui.receivingData.faculty

import androidx.fragment.app.viewModels
import com.helpful.stuSchedule.R
import com.helpful.stuSchedule.data.models.receivingData.Faculty
import com.helpful.stuSchedule.ui.receivingData.receivingData.ReceivingDataFragment

class FacultyFragment : ReceivingDataFragment<Faculty, Int>() {
    override val viewModel: FacultyViewModel by viewModels()

    override val pageTitleId: Int = R.string.faculty_title
    override val pageTextId: Int = R.string.faculty_text
    override val pageLabelId: Int = R.string.faculty_label
    override val pageTextArgs: Array<Any>
        get() = arrayOf(
        viewModel.studentDataContainer.value?.structure?.run {
            if (!viewModel.isEnglish)
                shortName
            else
                fullName
        } ?: getString(R.string.error)
    )
    override val nextDestinationId: Int = R.id.from_facultyFragment_to_courseFragment

    override fun getDataParameters(): Int = viewModel.studentDataContainer.value?.structure?.id
        ?: 0

    override fun mapData(data: Faculty): String {
        return if (!viewModel.isEnglish) {
            data.shortName
        }
        else {
            data.fullName
        }
    }
}