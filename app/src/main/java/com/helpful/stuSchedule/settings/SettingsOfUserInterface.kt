package com.helpful.stuSchedule.settings

import kotlinx.coroutines.flow.StateFlow

interface SettingsOfUserInterface {
    val selectedTheme: StateFlow<Int>
    val selectedColorOnOrange: StateFlow<Int>
    val selectedLessonTypeTextType: StateFlow<Int>
    val selectedWeekdayTextType: StateFlow<Int>
    val selectedLanguage: StateFlow<Int>
    val selectedDateFormat: StateFlow<Int>

    val appThemeId: Int

    fun setUp(
        theme: Int,
        textColor: Int,
        lessonTypeTextType: Int,
        weekdayTypeTextType: Int,
        language: Int,
        dateFormat: Int,
        saveInStorage: Boolean
    )

    fun setUpFromStorage()

    fun validData(name: String, value: Int): Boolean
}