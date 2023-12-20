package com.helpful.stuSchedule.ui.main.views.schedule

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.helpful.stuSchedule.MainActivity.Companion.GROUP_ID_DEFAULT
import com.helpful.stuSchedule.MainActivity.Companion.SHARED_PREFERENCES_GROUP_ID
import com.helpful.stuSchedule.settings.SettingsOfUser
import com.helpful.stuSchedule.tools.objects.TimeConverter
import com.helpful.stuSchedule.R
import com.helpful.stuSchedule.ui.main.recyclerViews.ScheduleAdapter
import com.helpful.stuSchedule.views.ModifiedFragment
import kotlinx.coroutines.launch

class ScheduleFragment: ModifiedFragment() {

    override val viewModel by viewModels<ScheduleViewModel>()
    override val fragmentLayoutId = R.layout.fragment_schedule
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
        get() = defaultSharedPreferences
            .getInt(SHARED_PREFERENCES_GROUP_ID, GROUP_ID_DEFAULT)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.mutableCurrentDateFormat.value = getString(R.string.date_format_string)
        viewModel.setGroupId(groupId)

        val dateText = view.findViewById<TextView>(R.id.titleText)
        view.run {
            lessons = findViewById(R.id.lessons)
            lessonsLoading = findViewById(R.id.lessonsLoading)
            realTimeUpdateLoadingComponents = findViewById(R.id.realTimeUpdateLoadingComponents)
            endTimeOfScheduleItemMessage = findViewById(R.id.endTimeOfScheduleItemMessage)
        }

        adapter = ScheduleAdapter(
            emptyList(),
            viewModel.selectedLessonTypeTextType.value,
            viewModel.selectedColorOnOrange.value,
            viewModel.selectedLanguage.value
        )
        viewModel.adapter = adapter
        adapter.selectedDate = TimeConverter.takeDateOrReturnSelectedDate(weekday, selectedDate)

        lessons.adapter = adapter
        lessons.layoutManager = LinearLayoutManager(requireContext())

        val selectedBlack = viewModel.selectedColorOnOrange.value ==
                SettingsOfUser.SELECTED_COLOR_ON_ORANGE_BLACK
        if (viewModel.isOrangeTheme) {
            lessonsLoading.findViewWithTag<ProgressBar>("updates")?.indeterminateTintList =
                ColorStateList.valueOf(if (selectedBlack) Color.BLACK else Color.WHITE)
        }

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
                    if(it.second != -1 && it.first != ScheduleViewModel.SCHEDULE_ITEM_TYPE_NONE) {
                        endTimeOfScheduleItemMessage.run {
                            layoutParams = ConstraintLayout.LayoutParams(
                                ConstraintLayout.LayoutParams.MATCH_PARENT,
                                45.dp
                            ).apply {
                                bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                            }
                            text = if (it.first != ScheduleViewModel.SCHEDULE_ITEM_TYPE_BEFORE)
                                getString(
                                    R.string.end_time_of_schedule_item,
                                    getString(
                                        if (it.first == ScheduleViewModel.SCHEDULE_ITEM_TYPE_LESSON)
                                            R.string.of_lesson
                                        else
                                            R.string.of_break_between_lessons
                                ),
                                it.second
                            )
                            else getString(R.string.time_left_to_lessons, it.second)
                        }
                    }
                    else {
                        endTimeOfScheduleItemMessage.run{
                            layoutParams = ConstraintLayout.LayoutParams(
                                ConstraintLayout.LayoutParams.MATCH_PARENT,
                                0
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
                    val (day, month, year) = TimeConverter.fromDateToDayMonthAndYear(
                        selectedDate,
                        SettingsOfUser.SELECTED_DATE_FORMAT_REVERSED
                    )
                    val (first, second, third) = when (viewModel.selectedDateFormat.value) {
                        SettingsOfUser.SELECTED_DATE_FORMAT_EUROPA -> listOf(day, month, year)
                        SettingsOfUser.SELECTED_DATE_FORMAT_AMERICA -> listOf(month, day, year)
                        SettingsOfUser.SELECTED_DATE_FORMAT_REVERSED -> listOf(year, month, day)
                        SettingsOfUser.SELECTED_DATE_FORMAT_REVERSED_AMERICA ->
                            listOf(year, day, month)
                        else -> listOf(day, month, year)
                    }

                    val formattedSelectedDate = getString(R.string.date_format_string, first, second, third)
                    Log.i(LOG_TAG, "[Date setting] selectedDate: $selectedDate. Selected date format: ${viewModel.selectedDateFormat.value}")

                    dateText.text = getString(
                        R.string.schedule_date,
                        formattedSelectedDate,
                        dayOfWeekToString(selectedDate)
                    )
                }
            }
        }
        setSchedule()
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
        val weekdayTextType = viewModel.selectedWeekdayTextType.value
        val weekdayText = resources.getStringArray(
            if(weekdayTextType == SettingsOfUser.SELECTED_WEEKDAY_TEXT_TYPE_FULL)
                R.array.full_weekdays
            else
                R.array.short_weekdays
        )
        return weekdayText[dayOfWeek]
    }

    override fun onResume() {
        super.onResume()
        if(viewModel.realTimeCancellationToken.value.workIsFinished()) {
            viewModel.realTimeCancellationToken.value.change()
            viewModel.setDataStateAsUpdates()
            setSchedule(false)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.realTimeCancellationToken.value.cancel()
    }

    companion object {
        @Suppress("unused")
        const val LOG_TAG = "Schedule fragment"
        const val ARGUMENT_WEEKDAY = "weekday"
        const val ARGUMENT_SELECTED_DATE = "selectedDate"
    }
}