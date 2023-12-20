package com.helpful.stuSchedule.ui.others.settings

import com.helpful.stuSchedule.settings.SettingsOfUser
import com.helpful.stuSchedule.views.ModifiedViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel: ModifiedViewModel() {
    private val mutableSelectedTheme =
        MutableStateFlow(selectedTheme.value)
    private val mutableSelectedTextColor =
        MutableStateFlow(selectedColorOnOrange.value)
    private val mutableSelectedLanguage =
        MutableStateFlow(selectedLanguage.value)
    private val mutableSelectedWeekdayTextType =
        MutableStateFlow(selectedWeekdayTextType.value)
    private val mutableSelectedLessonTypeTextType =
        MutableStateFlow(selectedLessonTypeTextType.value)
    private val mutableSelectedDateFormat =
        MutableStateFlow(selectedDateFormat.value)

    private val mutableSame = MutableStateFlow(true)

    val same: StateFlow<Boolean>
        get() = mutableSame

    val currentSelectedTheme: StateFlow<Int>
        get() = mutableSelectedTheme

    private fun checkIfSame() {
        mutableSame.value = listOf(
            mutableSelectedTheme.value == selectedTheme.value,
            mutableSelectedTextColor.value == selectedColorOnOrange.value,
            mutableSelectedLanguage.value == selectedLanguage.value,
            mutableSelectedWeekdayTextType.value == selectedWeekdayTextType.value,
            mutableSelectedLessonTypeTextType.value == selectedLessonTypeTextType.value,
            mutableSelectedDateFormat.value == selectedDateFormat.value)
            .all { it }
    }
    fun selectTheme(position: Int) {
        checkAndDoActionIfTrue(validData(SettingsOfUser.THEME_VALIDATION_NAME, position)) {
            mutableSelectedTheme.value = position
        }
    }
    fun selectTextColor(position: Int) {
        checkAndDoActionIfTrue(validData(SettingsOfUser.COLOR_ON_ORANGE_VALIDATION_NAME, position)) {
            mutableSelectedTextColor.value = position
        }
    }
    fun selectLanguage(position: Int) {
        checkAndDoActionIfTrue(validData(SettingsOfUser.LANGUAGE_VALIDATION_NAME, position)) {
            mutableSelectedLanguage.value = position
        }
    }
    fun selectWeekdayTextType(position: Int) {
        checkAndDoActionIfTrue(
            validData(
                SettingsOfUser.WEEKDAY_TEXT_TYPE_VALIDATION_NAME,
                position
            )
        ) {
            mutableSelectedWeekdayTextType.value = position
        }
    }
    fun selectLessonTypeTextType(position: Int) {
        checkAndDoActionIfTrue(
            validData(
                SettingsOfUser.LESSON_TYPE_TEXT_TYPE_VALIDATION_NAME,
                position
            )
        ) {
            mutableSelectedLessonTypeTextType.value = position
        }
    }
    fun selectDateFormat(position: Int) {
        checkAndDoActionIfTrue(
            validData(
                SettingsOfUser.DATE_FORMAT_VALIDATION_NAME,
                position
            )
        ) {
            mutableSelectedDateFormat.value = position
        }
    }

    private fun checkAndDoActionIfTrue(check: Boolean, action: () -> Unit) {
        if (check) {
            action()
            checkIfSame()
        }
    }

    fun save() {
        mutableSame.value = true
        setUp(
            mutableSelectedTheme.value,
            mutableSelectedTextColor.value,
            mutableSelectedLessonTypeTextType.value,
            mutableSelectedWeekdayTextType.value,
            mutableSelectedLanguage.value,
            mutableSelectedDateFormat.value,
            true
        )
    }

    fun reset() {
        mutableSelectedTheme.value = selectedTheme.value
        mutableSelectedTextColor.value = selectedColorOnOrange.value
        mutableSelectedLanguage.value = selectedLanguage.value
        mutableSelectedWeekdayTextType.value = selectedWeekdayTextType.value
        mutableSelectedLessonTypeTextType.value = selectedLessonTypeTextType.value
        mutableSelectedDateFormat.value = selectedDateFormat.value
        checkIfSame()
    }


    companion object {
        @Suppress("unused")
        private const val LOG_TAG = "Settings view model"
    }
}