package com.helpful.stuSchedule

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.helpful.stuSchedule.models.WordTime.Companion.DEFAULT_DATE


class MainActivity : AppCompatActivity() {
    private val dp
        get() = resources.displayMetrics.density

    private val screenHeightDp
        get() = resources.displayMetrics.heightPixels / dp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        Clear all:
//        getSharedPreferences(DEFAULT_SHARED_PREFERENCES, MODE_PRIVATE)
//            .edit().clear().apply()

//        Clear last check:
//        getSharedPreferences(DEFAULT_SHARED_PREFERENCES, MODE_PRIVATE)
//            .edit {
//                remove(SHARED_PREFERENCES_LAST_CHECK)
//            }

//        Invalid data:
//        getSharedPreferences(DEFAULT_SHARED_PREFERENCES, MODE_PRIVATE)
//            .edit {
//                putInt(LoadingFragment.SHARED_PREFERENCES_GROUP_ID, 3000)
//            }

    }

    override fun onResume() {
        super.onResume()
        if(screenHeightDp < 500) {
            @SuppressLint("SourceLockedOrientationActivity")
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
        }
    }

    companion object {
        const val DEFAULT_SHARED_PREFERENCES = "data_info"

        const val SHARED_PREFERENCES_LAST_CHECK = "last_check"
        const val SHARED_PREFERENCES_GROUP_ID = "group_id"
        const val SHARED_PREFERENCES_GROUP_NAME = "group_name"
        const val SHARED_PREFERENCES_FACULTY_ID = "faculty_id"
        const val SHARED_PREFERENCES_COURSE = "course"

        @Suppress("unused")
        const val SHARED_PREFERENCES_THEME = "theme"
        @Suppress("unused")
        const val SHARED_PREFERENCES_LANGUAGE = "language"
        /** 0 - full. 1 - short. 2 - very short. */
        const val SHARED_PREFERENCES_LESSON_TEXT_TYPE = "lesson_text_type"
        const val SHARED_PREFERENCES_WEEKDAY_TEXT_TYPE = "weekday_text_type"

        const val LAST_CHECK_DEFAULT = DEFAULT_DATE
        const val GROUP_ID_DEFAULT = -1
        const val GROUP_NAME_DEFAULT = ""
        const val FACULTY_ID_DEFAULT = -1
        const val COURSE_DEFAULT = -1

        @Suppress("unused")
        const val THEME_DEFAULT = ""
        @Suppress("unused")
        const val LANGUAGE_DEFAULT = "ukr"
        const val LESSON_TEXT_TYPE_DEFAULT = 0
        const val WEEKDAY_TEXT_TYPE_DEFAULT = 0
    }
}