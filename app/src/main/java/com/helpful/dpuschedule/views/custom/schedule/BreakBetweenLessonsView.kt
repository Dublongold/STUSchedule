package com.helpful.dpuschedule.views.custom.schedule

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.helpful.dpuschedule.R
import com.helpful.dpuschedule.helpful.objects.ScheduleItemPassChecker
import com.helpful.dpuschedule.helpful.objects.TimeConverter
import com.helpful.dpuschedule.models.WordTime

class BreakBetweenLessonsView : ScheduleItemView {

    private val breakText: TextView?
        get() = findViewById(R.id.breakText)

    constructor(context: Context) :
            super(context)
    constructor(context: Context, attrs: AttributeSet? = null) :
            super (context, attrs)
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int) :
            super (context, attrs, defStyleAttr)

    private fun convertTimesAndSetUp(previousLessonTimeEnd: String, nextLessonTimeStart: String): Pair<Int, Int> {
        val timeStart = TimeConverter.convertToMinutes(previousLessonTimeEnd)
        val timeEnd = TimeConverter.convertToMinutes(nextLessonTimeStart)

        breakText?.text = resources.getString(
            R.string.break_between_lessons,
            timeEnd - timeStart
        )

        return  timeStart to timeEnd
    }

    fun setUpBreak(previousLessonTimeEnd: String, nextLessonTimeStart: String) {
        convertTimesAndSetUp(previousLessonTimeEnd, nextLessonTimeStart)
        setDefaultBackgroundColor()
        foreground = null
    }

    fun setUpBreakWithCheck(
        selectedDate: String?,
        previousLessonTimeEnd: String,
        nextLessonTimeStart: String,
        wordTime: WordTime
    ) {
        val (timeStart, timeEnd) = convertTimesAndSetUp(previousLessonTimeEnd, nextLessonTimeStart)
        val timeNow = TimeConverter.convertToMinutes(wordTime.time)

        val breakIsPass = ScheduleItemPassChecker.breakIsPass(wordTime, timeEnd, selectedDate)

        setDefaultBackgroundColor(breakIsPass)
        foreground = if (timeNow in timeStart..<timeEnd && !breakIsPass) {
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.current_item_foreground,
                null
            )
        } else null
    }
    private fun setDefaultBackgroundColor(breakIsPass: Boolean = false) {
        backgroundTintList = if(!breakIsPass) ColorStateList.valueOf(
                resources.getColor(
                    R.color.break_between_lessons,
                    null
                )
            )
        else null
    }

    companion object {
        private const val LOG_TAG = "Break BL View"
    }
}