package com.helpful.stuSchedule.ui.others.bellSchedule

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.helpful.stuSchedule.R
import com.helpful.stuSchedule.ui.others.bellSchedule.recyclerViewAdapter.BellScheduleAdapter
import com.helpful.stuSchedule.views.ModifiedFragment

class BellScheduleFragment : ModifiedFragment() {
    override val viewModel: BellScheduleViewModel by viewModels()

    override val fragmentLayoutId = R.layout.fragment_bell_schedule

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val loadingProgressBar: ProgressBar = view.findViewById(R.id.listOfStudentsLoading)
        val adapter = BellScheduleAdapter().also {
            view.findViewById<RecyclerView>(R.id.bellSchedule).run {
                this.adapter = it
                layoutManager = LinearLayoutManager(context)
            }
        }
        observeStateFlow(viewModel.schedule) {
            loadingProgressBar.visibility = if (it.isEmpty()) {
                ProgressBar.VISIBLE
            } else {
                ProgressBar.GONE
            }
            adapter.updateItems(it)
        }
        if (viewModel.schedule.value.isEmpty()) {
            viewModel.loadSchedule()
        }
        super.onViewCreated(view, savedInstanceState)
    }
}