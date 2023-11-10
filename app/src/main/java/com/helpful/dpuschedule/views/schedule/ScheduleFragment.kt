package com.helpful.dpuschedule.views.schedule

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.helpful.dpuschedule.MainActivity
import com.helpful.dpuschedule.MainActivity.Companion.DEFAULT_SHARED_PREFERENCES
import com.helpful.dpuschedule.MainActivity.Companion.SHARED_PREFERENCES_WEEKDAY_TEXT_TYPE
import com.helpful.dpuschedule.MainActivity.Companion.WEEKDAY_TEXT_TYPE_DEFAULT
import com.helpful.dpuschedule.R
import com.helpful.dpuschedule.helpful.objects.TimeConverter
import com.helpful.dpuschedule.helpful.extentions.getSharedPreferences
import com.helpful.dpuschedule.recyclerViews.ScheduleAdapter
import kotlinx.coroutines.launch

class ScheduleFragment: Fragment() {

    private val viewModel by viewModels<ScheduleViewModel>()
    private lateinit var adapter: ScheduleAdapter
    private lateinit var lessons: RecyclerView
    private lateinit var lessonsLoading: ProgressBar
    private lateinit var realTimeUpdateLoadingComponents: LinearLayoutCompat
    private lateinit var endTimeOfScheduleItemMessage: TextView

    private val weekday
        get() = arguments?.getInt(ARGUMENT_WEEKDAY) ?: 0
    private val selectedDate
        get() = arguments?.getString(ARGUMENT_SELECTED_DATE)
    private val groupId
        get() = getSharedPreferences(
            DEFAULT_SHARED_PREFERENCES,
            AppCompatActivity.MODE_PRIVATE
        ).getInt(MainActivity.SHARED_PREFERENCES_GROUP_ID, 0)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setGroupId(groupId)

        val dateText = view.findViewById<TextView>(R.id.scheduleDate)
        view.run {
            lessons = findViewById(R.id.lessons)
            lessonsLoading = findViewById(R.id.lessonsLoading)
            realTimeUpdateLoadingComponents = findViewById(R.id.realTimeUpdateLoadingComponents)
            endTimeOfScheduleItemMessage = findViewById(R.id.endTimeOfScheduleItemMessage)
        }

        adapter = ScheduleAdapter(emptyList())
        adapter.selectedDate = TimeConverter.takeDateOrReturnSelectedDate(weekday, selectedDate)

        lessons.adapter = adapter
        lessons.layoutManager = LinearLayoutManager(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dataLoadedState.collect {
                    when(it) {
                        0 -> {
                            lessonsLoading.visibility = View.VISIBLE
                            realTimeUpdateLoadingComponents.visibility = View.GONE
                        }
                        1 -> {
                            lessonsLoading.visibility = View.GONE
                            realTimeUpdateLoadingComponents.visibility = View.GONE
                        }
                        2 -> {
                            lessonsLoading.visibility = View.GONE
                            realTimeUpdateLoadingComponents.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLessonToTimeLeft.collect {
                    if(it.second != -1) {
                        endTimeOfScheduleItemMessage.run {
                            layoutParams = ConstraintLayout.LayoutParams(
                                ConstraintLayout.LayoutParams.MATCH_PARENT,
                                ConstraintLayout.LayoutParams.WRAP_CONTENT
                            ).apply {
                                bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                            }
                            text = getString(
                                R.string.end_time_of_schedule_item,
                                getString(
                                    if (it.first)
                                        R.string.of_lesson
                                    else
                                        R.string.of_break_between_lessons
                                ),
                                it.second
                            )
                        }
                    }
                    else {
                        endTimeOfScheduleItemMessage.run{
                            layoutParams = ConstraintLayout.LayoutParams(
                                ConstraintLayout.LayoutParams.MATCH_PARENT,
                                1
                            ).apply {
                                bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                            }
                            text = null
                        }

                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.date.collect {
                    val selectedDate = this@ScheduleFragment.selectedDate ?: it
                    dateText.text = getString(
                        R.string.schedule_date,
                        selectedDate,
                        dayOfWeekToString(selectedDate)
                    )
                }
            }
        }
        setSchedule()
        view.findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setSchedule(needCheck: Boolean = true) {
        view?.let { view ->
            viewLifecycleOwner.lifecycleScope.launch {
                if (!needCheck || adapter.itemCount == 0) {
                    val lessons = viewModel.setSchedule(adapter, weekday, selectedDate)
                    if (lessons.isEmpty() && adapter.itemCount == 0) {
                        view.findViewById<TextView>(R.id.noLessonsMessage).visibility = View.VISIBLE
                    } else if (lessons.size == 1 && lessons.single() == null) {
                        view.findViewById<TextView>(R.id.noLessonsMessage).run {
                            visibility = View.VISIBLE
                            text = getString(R.string.invalid_group)
                            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                        }
                    }
                }
            }
        }
    }

    private fun dayOfWeekToString(selectedDate: String): String {
        val dayOfWeek = TimeConverter.getDayOfWeek(selectedDate)
        val weekdayTextType = getSharedPreferences(DEFAULT_SHARED_PREFERENCES, MODE_PRIVATE)
            .getInt(SHARED_PREFERENCES_WEEKDAY_TEXT_TYPE, WEEKDAY_TEXT_TYPE_DEFAULT)
        val weekdayText = resources.getStringArray(
            if(weekdayTextType == 1) R.array.short_weekdays else R.array.full_weekdays
        )
        return weekdayText[dayOfWeek]
    }

    override fun onResume() {
        super.onResume()
        if(viewModel.realTimeCancellationToken.value.isCancelled()) {
            viewModel.realTimeCancellationToken.value.change()
            viewModel.setDataStateAsUpdates()
            setSchedule(false)
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.realTimeCancellationToken.value.cancel()
    }

    companion object {
        const val LOG_TAG = "Schedule fragment"
        const val ARGUMENT_WEEKDAY = "weekday"
        const val ARGUMENT_SELECTED_DATE = "selectedDate"
    }
}