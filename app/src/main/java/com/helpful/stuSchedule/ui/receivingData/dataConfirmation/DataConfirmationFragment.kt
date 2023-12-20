package com.helpful.stuSchedule.ui.receivingData.dataConfirmation

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.edit
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.helpful.stuSchedule.MainActivity.Companion.COURSE_DEFAULT
import com.helpful.stuSchedule.MainActivity.Companion.FACULTY_ID_DEFAULT
import com.helpful.stuSchedule.MainActivity.Companion.GROUP_ID_DEFAULT
import com.helpful.stuSchedule.MainActivity.Companion.GROUP_NAME_DEFAULT
import com.helpful.stuSchedule.MainActivity.Companion.SHARED_PREFERENCES_COURSE
import com.helpful.stuSchedule.MainActivity.Companion.SHARED_PREFERENCES_FACULTY_ID
import com.helpful.stuSchedule.MainActivity.Companion.SHARED_PREFERENCES_GROUP_ID
import com.helpful.stuSchedule.MainActivity.Companion.SHARED_PREFERENCES_GROUP_NAME
import com.helpful.stuSchedule.MainActivity.Companion.SHARED_PREFERENCES_LAST_CHECK
import com.helpful.stuSchedule.data.models.receivingData.StudentDataContainer
import com.helpful.stuSchedule.tools.extentions.date
import com.helpful.stuSchedule.tools.extentions.getParcelableNormally
import com.helpful.stuSchedule.R
import com.helpful.stuSchedule.ui.receivingData.dataChanged.DataChangedFragment
import com.helpful.stuSchedule.ui.receivingData.receivingData.ReceivingDataFragment
import com.helpful.stuSchedule.ui.receivingData.wantChangeGroup.WantChangeFragment
import com.helpful.stuSchedule.ui.receivingData.welcome.WelcomeFragment
import com.helpful.stuSchedule.views.DefaultImplementationViewModel
import com.helpful.stuSchedule.views.custom.KebabMenuView
import com.helpful.stuSchedule.views.ModifiedFragment
import java.util.Calendar

class DataConfirmationFragment : ModifiedFragment() {
    override val viewModel: DefaultImplementationViewModel by viewModels()
    override val fragmentLayoutId = R.layout.fragment_data_confirmation

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val studentDataContainer = arguments
            ?.getParcelableNormally<StudentDataContainer>(StudentDataContainer.BUNDLE_KEY)
        val whereStartsReceiving = arguments?.getInt(ReceivingDataFragment.WHERE_STARTS_CHANGING)

        view.run {
            findViewById<TextView>(R.id.confirmDataText).text =
                Html.fromHtml(getString(
                        R.string.data_confirmation_text,
                        studentDataContainer?.group?.name
                    ),
                    Html.FROM_HTML_MODE_LEGACY
            )
            findViewById<AppCompatButton>(R.id.confirmButton).setOnClickListener {
                defaultSharedPreferences.edit {
                    putString(
                        SHARED_PREFERENCES_LAST_CHECK,
                        Calendar.getInstance().date
                    )
                    putInt(
                        SHARED_PREFERENCES_GROUP_ID,
                        studentDataContainer?.group?.id ?: GROUP_ID_DEFAULT
                    )
                    putString(
                        SHARED_PREFERENCES_GROUP_NAME,
                        studentDataContainer?.group?.name ?: GROUP_NAME_DEFAULT
                    )
                    putInt(
                        SHARED_PREFERENCES_COURSE,
                        studentDataContainer?.course?.course ?: COURSE_DEFAULT
                    )
                    putInt(
                        SHARED_PREFERENCES_FACULTY_ID,
                        studentDataContainer?.faculty?.id ?: FACULTY_ID_DEFAULT
                    )
                }
                findNavController().run {
                    when(whereStartsReceiving) {
                        WelcomeFragment.STARTS_FROM_WELCOME ->
                            navigate(R.id.from_dataConfirmationFragment_after_welcomeFragment)
                        WantChangeFragment.STARTS_FROM_WANT_CHANGE ->
                            navigate(R.id.from_dataConfirmationFragment_after_wantChangeFragment)
                        DataChangedFragment.STARTS_FROM_DATA_CHANGED ->
                            navigate(R.id.from_dataConfirmationFragment_after_dataChangedFragment)
                        else -> Log.e(LOG_TAG, "Invalid where starts receiving value " +
                                "(${whereStartsReceiving})")
                    }
                }
            }
            findViewById<AppCompatButton>(R.id.backButton).setOnClickListener {
                findNavController().popBackStack()
            }
            findViewById<KebabMenuView>(R.id.kebabMenu)
                .setDefaultOnMenuItemSelected(this@DataConfirmationFragment)
        }
    }
    companion object {
        private const val LOG_TAG = "Data confirmin. nav"
    }
}