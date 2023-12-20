package com.helpful.stuSchedule

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.helpful.stuSchedule.settings.SettingsOfUser
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.dsl.module


class MainActivity : AppCompatActivity() {
    private val  dp
        get() = resources.displayMetrics.density

    private val screenHeightDp
        get() = resources.displayMetrics.heightPixels / dp

    private val sharedPreferences
        get() = getSharedPreferences(DEFAULT_SHARED_PREFERENCES, MODE_PRIVATE)
    override fun onCreate(savedInstanceState: Bundle?) {
        val settingsOfUser = if (GlobalContext.getKoinApplicationOrNull() == null) {
            SettingsOfUser {
                sharedPreferences
            }
        }
        else {
            GlobalContext.get().get(SettingsOfUser::class)
        }
        setTheme(settingsOfUser.appThemeId)

//        Clear all:
//        sharedPreferences
//            .edit().clear().apply()

//        Clear last check:
//        sharedPreferences.edit {
//                remove(SHARED_PREFERENCES_LAST_CHECK)
//            }

//        Invalid data:
//        sharedPreferences.edit {
//                putInt(LoadingFragment.SHARED_PREFERENCES_GROUP_ID, 30000)
//            }

        if (GlobalContext.getKoinApplicationOrNull() == null) {
            startKoin {
                modules(
                    module {
                        single {
                            settingsOfUser
                        }
                    }
                )
            }
        }
        settingsOfUser.selectedTheme.observe {
            theme.applyStyle(settingsOfUser.appThemeId, true)

            val primaryColor = TypedValue()
            val backgroundColor = TypedValue()

            theme.resolveAttribute(R.attr.appPrimaryColor, primaryColor, true)
            theme.resolveAttribute(R.attr.appBackground, backgroundColor, true)
            window.statusBarColor = primaryColor.data
            findViewById<View>(R.id.mainActivity).setBackgroundColor(backgroundColor.data)
        }
        settingsOfUser.selectedColorOnOrange.observe {
            if (settingsOfUser.selectedTheme.value == SettingsOfUser.SELECTED_THEME_ORANGE) {
                theme.applyStyle(settingsOfUser.appThemeId, true)
            }
        }
        settingsOfUser.selectedLanguage.observe {
            setLanguage(it)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun<T> StateFlow<T>.observe(collect: (T) -> Unit) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                this@observe.collect(collect)
            }
        }
    }

    private fun setLanguage(selectedLanguage: Int) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(
            when (selectedLanguage) {
                SettingsOfUser.SELECTED_LANGUAGE_ENGLISH -> "en"
                SettingsOfUser.SELECTED_LANGUAGE_UKRAINIAN -> "uk"
                SettingsOfUser.SELECTED_LANGUAGE_RUSSIAN -> "ru"
                else -> throw IllegalStateException()
            }
        ))
    }

    override fun onResume() {
        super.onResume()
        if(screenHeightDp < 600) {
            @SuppressLint("SourceLockedOrientationActivity")
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
        }
    }

    companion object {
        @Suppress("unused")
        private const val LOG_TAG = "Main activity"

        @Suppress("MemberVisibilityCanBePrivate")
        const val DEFAULT_DATE = "0000-00-00"

        const val DEFAULT_SHARED_PREFERENCES = "data_info"

        const val SHARED_PREFERENCES_LAST_OPEN_VERSION = "is_first_open"
        const val SHARED_PREFERENCES_LAST_CHECK = "last_check"
        const val SHARED_PREFERENCES_GROUP_ID = "group_id"
        const val SHARED_PREFERENCES_GROUP_NAME = "group_name"
        const val SHARED_PREFERENCES_FACULTY_ID = "faculty_id"
        const val SHARED_PREFERENCES_COURSE = "course"

        const val LAST_OPEN_VERSION_DEFAULT = "1.0"
        const val LAST_OPEN_VERSION_REQUIRED = "1.2.0"

        const val SHARED_PREFERENCES_THEME = "theme"
        const val SHARED_PREFERENCES_COLOR_ON_ORANGE = "color_on_orange"
        const val SHARED_PREFERENCES_LANGUAGE = "language"
        /** 0 - full. 1 - short. 2 - very short. */
        const val SHARED_PREFERENCES_LESSON_TYPE_TEXT_SIZE = "lesson_type_text_type"
        const val SHARED_PREFERENCES_WEEKDAY_TEXT_SIZE = "weekday_text_type"
        const val SHARED_PREFERENCES_DATE_FORMAT = "date_format"

        const val LAST_CHECK_DEFAULT = DEFAULT_DATE
        const val GROUP_ID_DEFAULT = -1
        const val GROUP_NAME_DEFAULT = ""
        const val FACULTY_ID_DEFAULT = -1
        const val COURSE_DEFAULT = -1

        const val THEME_DEFAULT =
            SettingsOfUser.SELECTED_THEME_ORANGE
        const val COLOR_ON_ORANGE_DEFAULT =
            SettingsOfUser.SELECTED_COLOR_ON_ORANGE_WHITE
        const val LANGUAGE_DEFAULT =
            SettingsOfUser.SELECTED_LANGUAGE_UKRAINIAN
        const val LESSON_TYPE_TEXT_SIZE_DEFAULT =
            SettingsOfUser.SELECTED_LESSON_TYPE_TEXT_TYPE_FULL
        const val WEEKDAY_TEXT_SIZE_DEFAULT =
            SettingsOfUser.SELECTED_WEEKDAY_TEXT_TYPE_FULL
        const val DATE_FORMAT_DEFAULT =
            SettingsOfUser.SELECTED_DATE_FORMAT_EUROPA
    }
}