package com.helpful.dpuschedule.recyclerViews

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.helpful.dpuschedule.R
import com.helpful.dpuschedule.helpful.objects.ScheduleItemPassChecker
import com.helpful.dpuschedule.helpful.objects.TimeConverter
import com.helpful.dpuschedule.models.Lesson
import com.helpful.dpuschedule.models.WordTime
import com.helpful.dpuschedule.views.custom.schedule.BreakBetweenLessonsView
import com.helpful.dpuschedule.views.custom.schedule.LessonView
import com.helpful.dpuschedule.views.custom.schedule.ScheduleItemView

class ScheduleAdapter(private var items: List<Lesson?>): RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {
    private var nullableWordTime: WordTime? = null
    private val wordTime: WordTime
        get() = nullableWordTime ?: WordTime.DEFAULT

    var selectedDate: String? = null

    private var shouldCheck = false

    fun setWordTime(wordTime: WordTime) {
        nullableWordTime = wordTime
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        return ScheduleViewHolder(
            LayoutInflater
                .from(parent.context).inflate(if(viewType == 0)
                    R.layout.break_between_lessons_item
                else
                    R.layout.lesson_item,
                parent, false)
        )
    }

    fun loadLessons(lessons: List<Lesson?>) {
        items = lessons
        if(selectedDate == wordTime.date) {
            val lastLessonTimeEnd = items.lastOrNull()?.periods?.firstOrNull()?.timeEnd
            if (lastLessonTimeEnd != null) {
                val lessonTimeEnd = TimeConverter.convertToMinutes(lastLessonTimeEnd)
                val wordTimeInt = TimeConverter.convertToMinutes(wordTime.time)
                shouldCheck = ScheduleItemPassChecker.lessonsIsPass(wordTimeInt, lessonTimeEnd)
            }
        }
        notifyItemRangeChanged(0, items.size)
    }

    override fun getItemViewType(position: Int): Int {
        return if(items[position] == null) 0 else 1
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val lesson = items[position]
        val scheduleItemView = holder.scheduleItemView
        if(scheduleItemView != null) {
            if (lesson != null && scheduleItemView is LessonView) {
                scheduleItemView.run {
                    if(shouldCheck) {
                        changeBackgroundColorWithCheck(selectedDate, wordTime, lesson)
                    }
                    else {
                        changeBackgroundColor(lesson)
                    }
                    setViewsByLesson(lesson)
                }
            } else if (position > 0 && items.size > position + 1
                && scheduleItemView is BreakBetweenLessonsView) {

                val start = items[position - 1]?.periods?.firstOrNull()?.timeEnd
                val end = items[position + 1]?.periods?.firstOrNull()?.timeStart

                if (start != null && end != null) {
                    scheduleItemView.run {
                        if(shouldCheck) {
                            setUpBreakWithCheck(selectedDate, start, end, wordTime)
                        }
                        else {
                            setUpBreak(start, end)
                        }
                    }
                }
            }
        }
        else {
            Log.e(LOG_TAG, "Schedule item view have a wrong type.")
        }
    }

    override fun getItemCount(): Int  = items.size

    class ScheduleViewHolder(view: View): ViewHolder(view) {
        val scheduleItemView: ScheduleItemView? = when(view) {
            is LessonView -> view
            is BreakBetweenLessonsView -> view
            else -> null
        }
    }

    companion object {
        private const val LOG_TAG = "Schedule adapter"
    }
}