package com.helpful.stuSchedule.ui.receivingData.group

import androidx.fragment.app.viewModels
import com.helpful.stuSchedule.R
import com.helpful.stuSchedule.data.models.receivingData.Group
import com.helpful.stuSchedule.ui.receivingData.receivingData.ReceivingDataFragment

class GroupFragment : ReceivingDataFragment<Group, Pair<Int, Int>>() {
    override val viewModel: GroupViewModel by viewModels()

    override val pageTitleId: Int = R.string.group_title
    override val pageTextId: Int = R.string.group_text
    override val pageLabelId: Int = R.string.group_label
    override val pageTextArgs: Array<Any>
        get() = arrayOf(
        viewModel.studentDataContainer.value?.structure?.run{
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
        viewModel.studentDataContainer.value?.course?.course ?: getString(R.string.error),
    )
    override val nextDestinationId: Int = R.id.from_groupFragment_to_confirmDataFragment

    override fun getDataParameters(): Pair<Int, Int> =
        (viewModel.studentDataContainer.value?.course?.course ?: 0) to
                (viewModel.studentDataContainer.value?.faculty?.id ?: 0)

    override fun mapData(data: Group): String = data.name
}