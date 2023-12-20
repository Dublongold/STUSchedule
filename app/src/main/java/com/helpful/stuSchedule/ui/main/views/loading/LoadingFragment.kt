package com.helpful.stuSchedule.ui.main.views.loading

import android.util.Log
import androidx.core.content.edit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.helpful.stuSchedule.MainActivity.Companion.COURSE_DEFAULT
import com.helpful.stuSchedule.MainActivity.Companion.FACULTY_ID_DEFAULT
import com.helpful.stuSchedule.MainActivity.Companion.GROUP_ID_DEFAULT
import com.helpful.stuSchedule.MainActivity.Companion.GROUP_NAME_DEFAULT
import com.helpful.stuSchedule.MainActivity.Companion.LAST_CHECK_DEFAULT
import com.helpful.stuSchedule.MainActivity.Companion.LAST_OPEN_VERSION_DEFAULT
import com.helpful.stuSchedule.MainActivity.Companion.LAST_OPEN_VERSION_REQUIRED
import com.helpful.stuSchedule.MainActivity.Companion.SHARED_PREFERENCES_COURSE
import com.helpful.stuSchedule.MainActivity.Companion.SHARED_PREFERENCES_FACULTY_ID
import com.helpful.stuSchedule.MainActivity.Companion.SHARED_PREFERENCES_GROUP_ID
import com.helpful.stuSchedule.MainActivity.Companion.SHARED_PREFERENCES_GROUP_NAME
import com.helpful.stuSchedule.MainActivity.Companion.SHARED_PREFERENCES_LAST_CHECK
import com.helpful.stuSchedule.MainActivity.Companion.SHARED_PREFERENCES_LAST_OPEN_VERSION
import com.helpful.stuSchedule.R
import com.helpful.stuSchedule.tools.extentions.date
import com.helpful.stuSchedule.views.ModifiedFragment
import kotlinx.coroutines.launch
import java.util.Calendar

class LoadingFragment: ModifiedFragment() {

    override val viewModel: LoadingViewModel by viewModels()
    override val fragmentLayoutId = R.layout.fragment_loading


    override fun onStart() {
        super.onStart()
        val lastOpenVersion = defaultSharedPreferences.getString(
            SHARED_PREFERENCES_LAST_OPEN_VERSION,
            LAST_OPEN_VERSION_DEFAULT)
        defaultSharedPreferences.run {
            if (lastOpenVersion != LAST_OPEN_VERSION_REQUIRED) {
                val groupId = getInt(SHARED_PREFERENCES_GROUP_ID, GROUP_ID_DEFAULT)
                val groupName = getString(SHARED_PREFERENCES_GROUP_NAME, GROUP_NAME_DEFAULT)
                val facultyId = getInt(SHARED_PREFERENCES_FACULTY_ID, FACULTY_ID_DEFAULT)
                val course = getInt(SHARED_PREFERENCES_COURSE, COURSE_DEFAULT)

                edit().clear().apply()

                edit {
                    putString(SHARED_PREFERENCES_LAST_OPEN_VERSION, LAST_OPEN_VERSION_REQUIRED)

                    putInt(SHARED_PREFERENCES_GROUP_ID, groupId)
                    putString(SHARED_PREFERENCES_GROUP_NAME, groupName)
                    putInt(SHARED_PREFERENCES_FACULTY_ID, facultyId)
                    putInt(SHARED_PREFERENCES_COURSE, course)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            val lastCheck = try {
                defaultSharedPreferences.getString(
                    SHARED_PREFERENCES_LAST_CHECK,
                    LAST_CHECK_DEFAULT
                )
            }
            catch (e: Exception) {
                LAST_CHECK_DEFAULT
            }
            val groupId = getGroupId()
            val calendarNow = Calendar.getInstance().date
            if(groupId == GROUP_ID_DEFAULT) {
                findNavController().navigate(R.id.toWelcomeFragment)
            }
            else if(calendarNow != lastCheck) {
                defaultSharedPreferences.run {
                    val groupName = getString(SHARED_PREFERENCES_GROUP_NAME, GROUP_NAME_DEFAULT)
                    val facultyId = getInt(SHARED_PREFERENCES_FACULTY_ID, FACULTY_ID_DEFAULT)
                    val course = getInt(SHARED_PREFERENCES_COURSE, COURSE_DEFAULT)

                    if (groupName != null && facultyId != -1 && course != -1 &&
                        viewModel.checkGroupId(groupId, groupName, facultyId, course)
                    ) {
                        edit {
                            putString(SHARED_PREFERENCES_LAST_CHECK, calendarNow)
                        }
                        findNavController()
                            .navigate(R.id.from_loadingFragment_to_selectWeekdayFragment)
                    } else {
                        findNavController().navigate(R.id.from_loadingFragment_to_dataChangedFragment)
                    }
                }
            }
            else {
                findNavController().navigate(R.id.from_loadingFragment_to_selectWeekdayFragment)
            }
        }
    }

    private fun getGroupId(): Int = defaultSharedPreferences.run {
        try {
            getInt(
                SHARED_PREFERENCES_GROUP_ID,
                GROUP_ID_DEFAULT
            ).also {
                Log.i(LOG_TAG, "Group id good ($it).")
            }
        }
        catch (e: Exception) {
            val groupIdFromString = try {
                getString(SHARED_PREFERENCES_GROUP_ID, null)?.toIntOrNull()
                    ?: GROUP_ID_DEFAULT
            }
            catch (e: Exception) {
                GROUP_ID_DEFAULT
            }
            edit {
                remove(SHARED_PREFERENCES_GROUP_ID)
                if(groupIdFromString != -1) {
                    Log.i(LOG_TAG, "Group id not bad (${groupIdFromString}).")
                    putInt(SHARED_PREFERENCES_GROUP_ID, groupIdFromString)
                }
            }
            groupIdFromString
        }
    }
    companion object {
        const val LOG_TAG = "Loading fragment"
    }
}