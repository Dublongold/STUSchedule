package com.helpful.stuSchedule.ui.receivingData.wantChangeGroup

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.helpful.stuSchedule.R
import com.helpful.stuSchedule.ui.receivingData.receivingData.ReceivingDataFragment
import com.helpful.stuSchedule.views.DefaultImplementationViewModel
import com.helpful.stuSchedule.views.custom.KebabMenuView
import com.helpful.stuSchedule.views.ModifiedFragment

class WantChangeFragment: ModifiedFragment() {
    override val viewModel: DefaultImplementationViewModel by viewModels()
    override val fragmentLayoutId = R.layout.fragment_want_change_group

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<AppCompatButton>(R.id.startButton).setOnClickListener {
            findNavController().navigate(
                R.id.from_wantChangeFragment_to_structureFragment,
                bundleOf(
                    ReceivingDataFragment.WHERE_STARTS_CHANGING to STARTS_FROM_WANT_CHANGE
                )
            )
        }
        view.run{
            findViewById<AppCompatButton>(R.id.cancelButton).setOnClickListener {
                findNavController().popBackStack()
            }
            findViewById<KebabMenuView>(R.id.kebabMenu)
                .setDefaultOnMenuItemSelected(this@WantChangeFragment)
        }
    }

    companion object {
        const val STARTS_FROM_WANT_CHANGE = 1
    }
}