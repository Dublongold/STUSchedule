package com.helpful.stuSchedule.ui.others.listOfStudents

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.helpful.stuSchedule.MainActivity
import com.helpful.stuSchedule.R
import com.helpful.stuSchedule.ui.others.listOfStudents.recyclerViewAdapter.ListOfStudentsAdapter
import com.helpful.stuSchedule.views.ModifiedFragment


class ListOfStudentsFragment : ModifiedFragment() {
    override val viewModel: ListOfStudentsViewModel by viewModels()

    override val fragmentLayoutId: Int = R.layout.fragment_list_of_students

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = ListOfStudentsAdapter(viewModel.selectedLanguage.value).also {
            view.findViewById<RecyclerView>(R.id.listOfStudents).run {
                adapter = it
                layoutManager = LinearLayoutManager(context)
            }
        }
        val loadingProgressBar: ProgressBar = view.findViewById(R.id.listOfStudentsLoading)
        
        viewModel.groupId = defaultSharedPreferences.getInt (
            MainActivity.SHARED_PREFERENCES_GROUP_ID,
            MainActivity.GROUP_ID_DEFAULT
        )
        super.onViewCreated(view, savedInstanceState)
        observeStateFlow(viewModel.students) {
            loadingProgressBar.visibility = if (it.isEmpty())
                    ProgressBar.VISIBLE
                else
                    ProgressBar.GONE
            adapter.updateItems(it)
        }
        if (viewModel.students.value.isEmpty()) {
            viewModel.loadStudents()
        }
    }
}