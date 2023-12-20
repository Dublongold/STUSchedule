package com.helpful.stuSchedule.ui.main.views.custom

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.Html
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.allViews
import com.helpful.stuSchedule.data.models.Lesson
import com.helpful.stuSchedule.data.models.WordTime
import com.helpful.stuSchedule.tools.objects.ScheduleItemPassChecker
import com.helpful.stuSchedule.tools.objects.TimeConverter
import com.helpful.stuSchedule.R
import com.helpful.stuSchedule.settings.SettingsOfUser

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

    var selectedColorOnOrange = -1
    var selectedLanguage = -1
    var lessonTypeTextType: Int? = null

    constructor(context: Context) :
            super(context)
    constructor(context: Context, attrs: AttributeSet? = null) :
            super (context, attrs)
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int) :
            super (context, attrs, defStyleAttr)


    fun changeBackgroundColor(lesson: Lesson) {
        makeTextColorWhite()
        val lessonType = lesson.periods.firstOrNull()?.type ?: 0
        if (lessonType != 0 && possibleLessonTypeValues(lessonType)) {
            changeBackgroundTintList(lessonType)
        }
        foreground = null
    }
    fun changeBackgroundColorWithCheck(
        selectedDate: String?,
        wordTime: WordTime,
        lesson: Lesson
    ) {
        makeTextColorWhite()
        lesson.run {
            val lessonType = periods.firstOrNull()?.type ?: 0

            val timeStart = TimeConverter.convertToMinutes(periods.firstOrNull()?.timeStart)
            val timeEnd = TimeConverter.convertToMinutes(periods.firstOrNull()?.timeEnd)
            val timeNow = TimeConverter.convertToMinutes(wordTime.time)

            val checkIfLessonIsPass = ScheduleItemPassChecker.lessonIsPass(
                wordTime, timeEnd, selectedDate
            )
            val condition = lessonType != 0 && possibleLessonTypeValues(lessonType)
                    && !checkIfLessonIsPass
            changeBackgroundTintList(if(condition) lessonType else null)
            makeForegroundAsSelectedIfTrue(timeNow in timeStart..<timeEnd)

            if(lessonType == 4) {
                // Not depend only if time out of range from time start to time end and is bigger
                // than time start.
                setTextColorsDependOnTheme(
                    timeNow !in timeStart..<timeEnd && timeNow > timeStart
                )
            }
        }
    }

    private fun changeBackgroundTintList(lessonType: Int?) {
        backgroundTintList = if(lessonType != null) ColorStateList.valueOf(resources.getColor(
                when (lessonType) {
                    LESSON_TYPE_LECTURE -> R.color.lecture_lesson
                    LESSON_TYPE_PRACTICE -> R.color.practical_lesson
                    LESSON_TYPE_SESSION -> R.color.seminar_lesson
                    LESSON_TYPE_LABORATORY -> R.color.laboratory_lesson
                    LESSON_TYPE_CONS_EXAM -> R.color.lecture_lesson
                    LESSON_TYPE_EXAM -> R.color.seminar_lesson
                    LESSON_TYPE_INTERACTIVE_LECTURE -> R.color.lecture_lesson
                    LESSON_TYPE_OFFSET -> R.color.seminar_lesson
                    else -> android.R.color.transparent
                },
                null
            )
        )
        else null

        if(lessonType != null && lessonType == 4) {
            setTextColorsDependOnTheme()
        }
    }

    private fun setTextColorsDependOnTheme(notDepend: Boolean = false) {
        allViews.filter {
            it is TextView
        }.map {
            it as TextView
        }.forEach {
            it.setTextColor(
                if (notDepend)
                    Color.WHITE
                else {
                    ResourcesCompat.getColor(
                        resources,
                        if (selectedColorOnOrange == SettingsOfUser.SELECTED_COLOR_ON_ORANGE_BLACK)
                            R.color.black
                        else
                            R.color.white,
                        null
                    )
                }
            )
        }
    }

    fun setViewsByLesson(lesson: Lesson) {
        lessonNumber?.text = resources.getString(R.string.lesson_number, lesson.number)
        val isNotEnglish = selectedLanguage != SettingsOfUser.SELECTED_LANGUAGE_ENGLISH
        lesson.periods.firstOrNull()?.run {
            lessonStart?.text = timeStart
            lessonEnd?.text = timeEnd
            val lessonNameText = (if (isNotEnglish) disciplineShortName
            else disciplineFullName)
            lessonName?.text = lessonNameText.let {
                if (it.length > 50) it.substring(0, 50) + "..." else it
            }
            lessonClassrooms?.text = Html.fromHtml(
                resources.getQuantityString(
                    R.plurals.classrooms,
                    classroom.count { it == ',' } + 1,
                    classroom
                ),
                Html.FROM_HTML_MODE_LEGACY
            )
            lessonType?.text = lessonTypeToString(type).replace(" ", "\n")
            lessonTeacher?.text = resources.getQuantityString(
                R.plurals.teachers,
                teachersNameFull.count { it == ',' } + 1,
                teachersNameFull
            )

        }
    }

    private fun possibleLessonTypeValues(lessonType: Int) =
        lessonType in 1..6 || lessonType == LESSON_TYPE_CONS_EXAM ||
                lessonType == LESSON_TYPE_INTERACTIVE_LECTURE

    private fun lessonTypeToString(type: Int): String {
        val types = resources.getStringArray(when(lessonTypeTextType) {
            0 -> R.array.full_lessons_type
            1 -> R.array.short_lessons_type
            2 -> R.array.very_short_lesson_types
            else -> R.array.full_lessons_type
        })
        val typeToIndex = when (type) {
            LESSON_TYPE_LECTURE -> 0
            LESSON_TYPE_PRACTICE -> 1
            LESSON_TYPE_SESSION -> 2
            LESSON_TYPE_LABORATORY -> 3
            LESSON_TYPE_EXAM -> 4
            LESSON_TYPE_OFFSET -> 5
            LESSON_TYPE_INTERACTIVE_LECTURE -> 6
            LESSON_TYPE_CONS_EXAM -> 7
            else -> Int.MAX_VALUE
        }
        return types.getOrElse(typeToIndex) {
            resources.getString(R.string.error)
        }
    }

    companion object {
        @Suppress("unused")
        private const val LOG_TAG = "Lesson view"
        const val LESSON_TYPE_LECTURE = 1
        const val LESSON_TYPE_PRACTICE = 2
        const val LESSON_TYPE_SESSION = 3
        const val LESSON_TYPE_LABORATORY = 4
        const val LESSON_TYPE_EXAM = 5
        const val LESSON_TYPE_OFFSET = 6
        const val LESSON_TYPE_INTERACTIVE_LECTURE = 9
        const val LESSON_TYPE_CONS_EXAM = 20
    }
}