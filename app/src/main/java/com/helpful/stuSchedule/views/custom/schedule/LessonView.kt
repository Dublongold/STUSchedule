package com.helpful.stuSchedule.views.custom.schedule

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.res.ColorStateList
import android.text.Html
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.helpful.stuSchedule.MainActivity.Companion.DEFAULT_SHARED_PREFERENCES
import com.helpful.stuSchedule.MainActivity.Companion.LESSON_TEXT_TYPE_DEFAULT
import com.helpful.stuSchedule.MainActivity.Companion.SHARED_PREFERENCES_LESSON_TEXT_TYPE
import com.helpful.stuSchedule.R
import com.helpful.stuSchedule.helpful.objects.ScheduleItemPassChecker
import com.helpful.stuSchedule.helpful.objects.TimeConverter
import com.helpful.stuSchedule.models.Lesson
import com.helpful.stuSchedule.models.WordTime

class LessonView : ScheduleItemView {

    private val lessonNumber: TextView?
        get() = findViewById(R.id.lessonNumber)
    private val lessonType: TextView?
        get() = findViewById(R.id.lessonType)
    private val lessonStart: TextView?
        get() = findViewById(R.id.lessonStart)
    private val lessonEnd: TextView?
        get() = findViewById(R.id.lessonEnd)
    private val lessonName: TextView?
        get() = findViewById(R.id.lessonName)
    private val lessonTeacher: TextView?
        get() = findViewById(R.id.lessonTeacher)
    private val lessonClassrooms: TextView?
        get() = findViewById(R.id.lessonClassrooms)

    constructor(context: Context) :
            super(context)
    constructor(context: Context, attrs: AttributeSet? = null) :
            super (context, attrs)
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int) :
            super (context, attrs, defStyleAttr)

    fun changeBackgroundColor(lesson: Lesson) {
        val lessonType = lesson.periods.firstOrNull()?.type ?: 0
        if (lessonType != 0 && lessonType in 1..4) {
            changeBackgroundTintList(lessonType)
        }
        foreground = null
    }
    fun changeBackgroundColorWithCheck(
        selectedDate: String?,
        wordTime: WordTime,
        lesson: Lesson
    ) {
        lesson.run {
            val lessonType = periods.firstOrNull()?.type ?: 0

            val timeStart = TimeConverter.convertToMinutes(periods.firstOrNull()?.timeStart)
            val timeEnd = TimeConverter.convertToMinutes(periods.firstOrNull()?.timeEnd)
            val timeNow = TimeConverter.convertToMinutes(wordTime.time)

            val checkIfLessonIsPass = ScheduleItemPassChecker.lessonIsPass(
                wordTime, timeEnd, selectedDate
            )
            val condition = lessonType != 0 && lessonType in 1..4 && !checkIfLessonIsPass
            changeBackgroundTintList(if(condition) lessonType else null)
            foreground = if (timeNow in timeStart..<timeEnd) {
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.current_item_foreground,
                    null
                )
            }
            else null
        }
    }

    private fun changeBackgroundTintList(lessonType: Int?) {
        backgroundTintList = if(lessonType != null) ColorStateList.valueOf(resources.getColor(
                when (lessonType) {
                    1 -> R.color.lecture_lesson
                    2 -> R.color.practical_lesson
                    3 -> R.color.seminar_lesson
                    4 -> R.color.laboratory_lesson
                    else -> android.R.color.transparent
                },
                null
            )
        )
        else null
    }

    fun setViewsByLesson(lesson: Lesson) {
        lessonNumber?.text = resources.getString(R.string.lesson_number, lesson.number)

        lesson.periods.firstOrNull()?.run {
            lessonStart?.text = timeStart
            lessonEnd?.text = timeEnd
            lessonName?.text = disciplineShortName
            lessonClassrooms?.text = Html.fromHtml(
                resources.getQuantityString(
                    R.plurals.classrooms,
                    classroom.count { it == ',' } + 1,
                    classroom
                ),
                Html.FROM_HTML_MODE_LEGACY
            )
            lessonType?.text = resources.getString(
                R.string.lesson_parentheses,
                lessonTypeToString(type)
            )
            lessonTeacher?.text = resources.getQuantityString(
                R.plurals.teachers,
                teachersNameFull.count { it == ',' } + 1,
                teachersNameFull
            )

        }
    }

    private fun lessonTypeToString(type: Int): String {
        val lessonTextType = context
            .getSharedPreferences(DEFAULT_SHARED_PREFERENCES, MODE_PRIVATE)
            .getInt(SHARED_PREFERENCES_LESSON_TEXT_TYPE, LESSON_TEXT_TYPE_DEFAULT)
        val types = resources.getStringArray(when(lessonTextType) {
            0 -> R.array.full_lessons_type
            1 -> R.array.short_lessons_type
            2 -> R.array.very_short_lesson_types
            else -> R.array.full_lessons_type
        })
        return types.getOrElse(type - 1) {
            resources.getString(R.string.error)
        }
    }

    companion object {
        @Suppress("unused")
        private const val LOG_TAG = "Lesson view"
    }
}