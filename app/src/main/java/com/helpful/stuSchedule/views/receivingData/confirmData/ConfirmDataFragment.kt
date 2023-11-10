package com.helpful.stuSchedule.views.receivingData.confirmData

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.helpful.stuSchedule.MainActivity.Companion.COURSE_DEFAULT
import com.helpful.stuSchedule.MainActivity.Companion.DEFAULT_SHARED_PREFERENCES
import com.helpful.stuSchedule.MainActivity.Companion.FACULTY_ID_DEFAULT
import com.helpful.stuSchedule.MainActivity.Companion.GROUP_ID_DEFAULT
import com.helpful.stuSchedule.MainActivity.Companion.GROUP_NAME_DEFAULT
import com.helpful.stuSchedule.MainActivity.Companion.SHARED_PREFERENCES_COURSE
import com.helpful.stuSchedule.MainActivity.Companion.SHARED_PREFERENCES_FACULTY_ID
import com.helpful.stuSchedule.MainActivity.Companion.SHARED_PREFERENCES_GROUP_ID
import com.helpful.stuSchedule.MainActivity.Companion.SHARED_PREFERENCES_GROUP_NAME
import com.helpful.stuSchedule.MainActivity.Companion.SHARED_PREFERENCES_LAST_CHECK
import com.helpful.stuSchedule.R
import com.helpful.stuSchedule.helpful.extentions.date
import com.helpful.stuSchedule.helpful.extentions.getParcelableNormally
import com.helpful.stuSchedule.helpful.extentions.getSharedPreferences
import com.helpful.stuSchedule.models.receivingData.StudentDataContainer
import java.util.Calendar

class ConfirmDataFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_confirm_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val studentDataContainer = arguments
            ?.getParcelableNormally<StudentDataContainer>(StudentDataContainer.BUNDLE_KEY)

        view.run {
            findViewById<TextView>(R.id.confirmDataText).text =
                Html.fromHtml(getString(
                        R.string.confirm_data_text,
                        studentDataContainer?.group?.name
                    ),
                    Html.FROM_HTML_MODE_LEGACY
            )
            findViewById<AppCompatButton>(R.id.confirmButton).setOnClickListener {
                getSharedPreferences(DEFAULT_SHARED_PREFERENCES, AppCompatActivity.MODE_PRIVATE)
                    .edit {
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
                    when(val destinationId = currentBackStack.value
                        .getOrNull(1)?.destination?.id) {
                        R.id.selectWeekdayFragment ->
                            navigate(R.id.fromConfirmDataFragmentAfterWantChangeFragment)
                        R.id.dataChangedFragment ->
                            navigate(R.id.fromConfirmDataFragmentAfterDataChangedFragment)
                        R.id.welcomeFragment ->
                            navigate(R.id.fromConfirmDataFragmentAfterWelcomeFragment)
                        else -> Log.e("Confirm data nav", "Invalid destination id " +
                                "(${destinationId})")
                    }
                }
            }
            findViewById<AppCompatButton>(R.id.backButton).setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }
}