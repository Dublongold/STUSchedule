package com.helpful.stuSchedule.ui.receivingData.structure

import androidx.fragment.app.viewModels
import com.helpful.stuSchedule.R
import com.helpful.stuSchedule.data.models.receivingData.Structure
import com.helpful.stuSchedule.ui.receivingData.receivingData.ReceivingDataFragment

class StructureFragment : ReceivingDataFragment<Structure, Int>() {
    override val viewModel: StructureViewModel by viewModels()

    override val pageTitleId: Int = R.string.structure_title
    override val pageTextId: Int = R.string.structure_text
    override val pageLabelId: Int = R.string.structure_label
    override val pageTextArgs: Array<Any>? = null
    override val nextDestinationId: Int = R.id.from_structureFragment_to_facultyFragment

    override fun getDataParameters(): Int = 0

    override fun mapData(data: Structure): String {
        return if (!viewModel.isEnglish) {
            data.shortName
        }
        else {
            data.fullName
        }
    }

}