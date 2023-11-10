package com.helpful.dpuschedule.views.loading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.helpful.dpuschedule.MainActivity
import com.helpful.dpuschedule.MainActivity.Companion.GROUP_ID_DEFAULT
import com.helpful.dpuschedule.MainActivity.Companion.LAST_CHECK_DEFAULT
import com.helpful.dpuschedule.MainActivity.Companion.SHARED_PREFERENCES_GROUP_ID
import com.helpful.dpuschedule.MainActivity.Companion.SHARED_PREFERENCES_LAST_CHECK
import com.helpful.dpuschedule.R
import com.helpful.dpuschedule.helpful.extentions.date
import com.helpful.dpuschedule.helpful.extentions.getSharedPreferences
import kotlinx.coroutines.launch
import java.util.Calendar

class LoadingFragment: Fragment() {

    private val viewModel: LoadingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_loading, container, false)
    }

    private val defaultShared
        get() = getSharedPreferences(
            MainActivity.DEFAULT_SHARED_PREFERENCES,
            AppCompatActivity.MODE_PRIVATE
        )

    override fun onStart() {
        super.onStart()
        viewLifecycleOwner.lifecycleScope.launch {
            val lastCheck = try {
                defaultShared.getString(
                    SHARED_PREFERENCES_LAST_CHECK,
                    LAST_CHECK_DEFAULT
                )
            }
            catch (e: Exception) {
                LAST_CHECK_DEFAULT
            }
            val groupId = getGroupId()
            val calendarNow = Calendar.getInstance().date
            if(groupId == -1) {
                findNavController().navigate(R.id.toWelcomeFragment)
            }
            else if(calendarNow != lastCheck) {
                val groupName = defaultShared.getString(
                    MainActivity.SHARED_PREFERENCES_GROUP_NAME,
                    ""
                )
                val facultyId = defaultShared.getInt(
                    MainActivity.SHARED_PREFERENCES_FACULTY_ID,
                    -1
                )
                val course = defaultShared.getInt(
                    MainActivity.SHARED_PREFERENCES_FACULTY_ID,
                    -1
                )
                if( groupName != null && facultyId != -1 && course != -1 &&
                    viewModel.checkGroupId(groupId, groupName, facultyId, course)) {
                    defaultShared.edit {
                        putString(SHARED_PREFERENCES_LAST_CHECK, calendarNow)
                    }
                    findNavController().navigate(R.id.fromLoadingFragmentToSelectWeekdayFragment)
                }
                else {
                    findNavController().navigate(R.id.fromLoadingFragmentToDataChangedFragment)
                }
            }
            else {
                findNavController().navigate(R.id.fromLoadingFragmentToSelectWeekdayFragment)
            }
        }
    }

    private fun getGroupId(): Int = try {
            defaultShared.getInt(
                SHARED_PREFERENCES_GROUP_ID,
                GROUP_ID_DEFAULT
            )
        }
        catch (e: Exception) {
            val groupIdFromString = try {
                defaultShared
                    .getString(SHARED_PREFERENCES_GROUP_ID, null)
                    ?.toIntOrNull() ?: GROUP_ID_DEFAULT
            }
            catch (e: Exception) {
                GROUP_ID_DEFAULT
            }
            defaultShared.edit {
                remove(SHARED_PREFERENCES_GROUP_ID)
                if(groupIdFromString != -1)
                    putInt(SHARED_PREFERENCES_GROUP_ID, groupIdFromString)
            }
            groupIdFromString
        }
}