package com.helpful.stuSchedule.ui.receivingData.dataChanged

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.helpful.stuSchedule.R
import com.helpful.stuSchedule.ui.receivingData.receivingData.ReceivingDataFragment
import com.helpful.stuSchedule.views.DefaultImplementationViewModel
import com.helpful.stuSchedule.views.custom.KebabMenuView
import com.helpful.stuSchedule.views.ModifiedFragment

class DataChangedFragment: ModifiedFragment() {

    override val viewModel: DefaultImplementationViewModel by viewModels()
    override val fragmentLayoutId = R.layout.fragment_data_changed

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.run{
            findViewById<AppCompatButton>(R.id.startButton).setOnClickListener {
                findNavController().navigate(
                    R.id.from_dataChangedFragment_to_structureFragment,
                    bundleOf(
                        ReceivingDataFragment.WHERE_STARTS_CHANGING to STARTS_FROM_DATA_CHANGED
                    )
                )
            }
            findViewById<KebabMenuView>(R.id.kebabMenu)
                .setDefaultOnMenuItemSelected(this@DataChangedFragment)
        }
    }

    companion object {
        const val STARTS_FROM_DATA_CHANGED = 2
    }
}