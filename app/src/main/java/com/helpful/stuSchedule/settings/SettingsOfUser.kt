package com.helpful.stuSchedule.settings

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.helpful.stuSchedule.MainActivity
import com.helpful.stuSchedule.MainActivity.Companion.DATE_FORMAT_DEFAULT
import com.helpful.stuSchedule.MainActivity.Companion.LANGUAGE_DEFAULT
import com.helpful.stuSchedule.MainActivity.Companion.LESSON_TYPE_TEXT_SIZE_DEFAULT
import com.helpful.stuSchedule.MainActivity.Companion.SHARED_PREFERENCES_COLOR_ON_ORANGE
import com.helpful.stuSchedule.MainActivity.Companion.SHARED_PREFERENCES_DATE_FORMAT
import com.helpful.stuSchedule.MainActivity.Companion.SHARED_PREFERENCES_LANGUAGE
import com.helpful.stuSchedule.MainActivity.Companion.SHARED_PREFERENCES_LESSON_TYPE_TEXT_SIZE
import com.helpful.stuSchedule.MainActivity.Companion.SHARED_PREFERENCES_THEME
import com.helpful.stuSchedule.MainActivity.Companion.SHARED_PREFERENCES_WEEKDAY_TEXT_SIZE
import com.helpful.stuSchedule.MainActivity.Companion.WEEKDAY_TEXT_SIZE_DEFAULT
import com.helpful.stuSchedule.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class SettingsOfUser (private val getStorage: () -> SharedPreferences) : SettingsOfUserInterface {
    private val mutableSelectedTheme =
        MutableStateFlow(SELECTED_THEME_ORANGE)
    private val mutableSelectedColorOnOrange =
        MutableStateFlow(SELECTED_THEME_ORANGE)
    private val mutableSelectedLessonTypeTextType =
        MutableStateFlow(SELECTED_LESSON_TYPE_TEXT_TYPE_FULL)
    private val mutableSelectedWeekdayTextType =
        MutableStateFlow(SELECTED_WEEKDAY_TEXT_TYPE_FULL)
    private val mutableSelectedLanguage =
        MutableStateFlow(SELECTED_LANGUAGE_UKRAINIAN)
    private val mutableSelectedDateFormat =
        MutableStateFlow(SELECTED_DATE_FORMAT_EUROPA)

    init {
        setUpFromStorage()
    }

    override val selectedTheme: StateFlow<Int>
        get() = mutableSelectedTheme

    override val selectedColorOnOrange: StateFlow<Int>
        get() = mutableSelectedColorOnOrange

    override val selectedLessonTypeTextType: StateFlow<Int>
        get() = mutableSelectedLessonTypeTextType

    override val selectedWeekdayTextType: StateFlow<Int>
        get() = mutableSelectedWeekdayTextType

    override val selectedLanguage: StateFlow<Int>
        get() = mutableSelectedLanguage

    override val selectedDateFormat: StateFlow<Int>
        get() = mutableSelectedDateFormat

    override val appThemeId: Int
        get() = when (selectedTheme.value) {
            SELECTED_THEME_ORANGE -> R.style.Orange_Theme_DPUSchedule
            SELECTED_THEME_PURPLE -> R.style.Purple_Theme_DPUSchedule
            SELECTED_THEME_BLACK -> R.style.Black_Theme_DPUSchedule
            else -> throw IllegalStateException()
        }

    override fun setUp(
        theme: Int,
        textColor: Int,
        lessonTypeTextType: Int,
        weekdayTypeTextType: Int,
        language: Int,
        dateFormat: Int,
        saveInStorage: Boolean
    ) {
        mutableSelectedTheme.value = theme
        mutableSelectedColorOnOrange.value = textColor
        mutableSelectedLessonTypeTextType.value = lessonTypeTextType
        mutableSelectedWeekdayTextType.value = weekdayTypeTextType
        mutableSelectedLanguage.value = language
        mutableSelectedDateFormat.value = dateFormat
        if (saveInStorage) {
            getStorage().edit {
                putInt(SHARED_PREFERENCES_THEME, mutableSelectedTheme.value)
                putInt(SHARED_PREFERENCES_COLOR_ON_ORANGE, mutableSelectedColorOnOrange.value)
                putInt(SHARED_PREFERENCES_LANGUAGE, mutableSelectedLanguage.value)
                putInt(SHARED_PREFERENCES_LESSON_TYPE_TEXT_SIZE,
                    mutableSelectedLessonTypeTextType.value)
                putInt(SHARED_PREFERENCES_WEEKDAY_TEXT_SIZE, mutableSelectedWeekdayTextType.value)
                putInt(SHARED_PREFERENCES_DATE_FORMAT, mutableSelectedDateFormat.value)
            }
        }
    }

    override fun setUpFromStorage() {
        val storage = getStorage()
        val selectedTheme = storage
            .getInt(SHARED_PREFERENCES_THEME, MainActivity.THEME_DEFAULT)
        val selectedTextColor = storage
            .getInt(SHARED_PREFERENCES_COLOR_ON_ORANGE, MainActivity.COLOR_ON_ORANGE_DEFAULT)
        val selectedLessonTypeTextType = storage
            .getInt(
                SHARED_PREFERENCES_LESSON_TYPE_TEXT_SIZE,
                LESSON_TYPE_TEXT_SIZE_DEFAULT
            )
        val selectedWeekdayTextType = storage
            .getInt(
                SHARED_PREFERENCES_WEEKDAY_TEXT_SIZE,
                WEEKDAY_TEXT_SIZE_DEFAULT
            )
        val selectedLanguage = storage
            .getInt(SHARED_PREFERENCES_LANGUAGE, LANGUAGE_DEFAULT)
        val selectedDateFormat = storage
            .getInt(SHARED_PREFERENCES_DATE_FORMAT, DATE_FORMAT_DEFAULT)

        setUp(
            selectedTheme,
            selectedTextColor,
            selectedLessonTypeTextType,
            selectedWeekdayTextType,
            selectedLanguage,
            selectedDateFormat,
            false
        )
    }

    override fun validData(name: String, value: Int): Boolean {
        val dateValuesRange = when (name) {
            THEME_VALIDATION_NAME, LESSON_TYPE_TEXT_TYPE_VALIDATION_NAME,
                LANGUAGE_VALIDATION_NAME -> 0..2
            COLOR_ON_ORANGE_VALIDATION_NAME, WEEKDAY_TEXT_TYPE_VALIDATION_NAME -> 0..1
            DATE_FORMAT_VALIDATION_NAME -> 0..3
            else -> {
                Log.w(LOG_TAG, "[validData] Invalid validation name: $name. False returned.")
                0..0
            }
        }
        return if (dateValuesRange != 0..0) {
            value in dateValuesRange
        }
        else {
            false
        }
    }

    companion object {
        private const val LOG_TAG = "Settings of user"

        const val THEME_VALIDATION_NAME = "theme"
        const val SELECTED_THEME_ORANGE = 0
        const val SELECTED_THEME_PURPLE = 1
        const val SELECTED_THEME_BLACK = 2

        const val COLOR_ON_ORANGE_VALIDATION_NAME = "color_on_orange"
        const val SELECTED_COLOR_ON_ORANGE_WHITE = 0
        const val SELECTED_COLOR_ON_ORANGE_BLACK = 1

        const val LESSON_TYPE_TEXT_TYPE_VALIDATION_NAME = "lesson_type_text_type"
        const val SELECTED_LESSON_TYPE_TEXT_TYPE_FULL = 0
        @Suppress("unused")
        const val SELECTED_LESSON_TYPE_TEXT_TYPE_SHORT = 1
        @Suppress("unused")
        const val SELECTED_LESSON_TYPE_TEXT_TYPE_VERY_SHORT = 2

        const val WEEKDAY_TEXT_TYPE_VALIDATION_NAME = "weekday_text_type"
        const val SELECTED_WEEKDAY_TEXT_TYPE_FULL = 0
        @Suppress("unused")
        const val SELECTED_WEEKDAY_TEXT_TYPE_SHORT = 1

        const val LANGUAGE_VALIDATION_NAME = "language"
        const val SELECTED_LANGUAGE_UKRAINIAN = 0
        const val SELECTED_LANGUAGE_ENGLISH = 1
        const val SELECTED_LANGUAGE_RUSSIAN = 2

        const val DATE_FORMAT_VALIDATION_NAME = "date_format"
        const val SELECTED_DATE_FORMAT_EUROPA = 0
        const val SELECTED_DATE_FORMAT_REVERSED = 1
        const val SELECTED_DATE_FORMAT_AMERICA = 2
        const val SELECTED_DATE_FORMAT_REVERSED_AMERICA = 3
    }
}