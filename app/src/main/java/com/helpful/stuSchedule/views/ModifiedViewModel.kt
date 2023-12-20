package com.helpful.stuSchedule.views

import androidx.lifecycle.ViewModel
import com.helpful.stuSchedule.settings.SettingsOfUser
import com.helpful.stuSchedule.settings.SettingsOfUserInterface
import org.koin.core.context.GlobalContext

abstract class ModifiedViewModel(
    private val settingsOfUser: SettingsOfUser = GlobalContext.get().get(SettingsOfUser::class)
): ViewModel(), SettingsOfUserInterface by settingsOfUser {

    val isOrangeTheme
        get() = settingsOfUser.selectedTheme.value == SettingsOfUser.SELECTED_THEME_ORANGE

    val textColorIsBlack
        get() = settingsOfUser.selectedColorOnOrange.value == SettingsOfUser.SELECTED_COLOR_ON_ORANGE_BLACK

    val isEnglish
        get() = settingsOfUser.selectedLanguage.value == SettingsOfUser.SELECTED_LANGUAGE_ENGLISH

    val dateFormatIsReversed
        get() = settingsOfUser.selectedDateFormat.value == SettingsOfUser
                    .SELECTED_DATE_FORMAT_REVERSED
                || settingsOfUser.selectedDateFormat.value == SettingsOfUser
                    .SELECTED_DATE_FORMAT_REVERSED_AMERICA
}