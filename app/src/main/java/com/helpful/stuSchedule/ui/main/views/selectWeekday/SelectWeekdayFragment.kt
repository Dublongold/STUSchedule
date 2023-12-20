package com.helpful.stuSchedule.ui.main.views.selectWeekday

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.helpful.stuSchedule.MainActivity
import com.helpful.stuSchedule.R
import com.helpful.stuSchedule.data.models.DateContainer
import com.helpful.stuSchedule.tools.objects.DateContainerBuilder
import com.helpful.stuSchedule.views.ModifiedFragment
import com.helpful.stuSchedule.views.custom.KebabMenuView

class SelectWeekdayFragment : ModifiedFragment() {

    override val viewModel: SelectWeekdayViewModel by viewModels()
    override val fragmentLayoutId = R.layout.fragment_select_weekday

    private lateinit var scheduleDateEditText: EditText


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.mutableWeekdays.value = resources.getStringArray(R.array.select_weekday_options)

        scheduleDateEditText = view.findViewById(R.id.scheduleDateEditText)

        viewModel.setCurrentDateFormat(getString(R.string.date_format_string))
        setUpSpinner(view)

        view.findViewById<TextView>(R.id.yourGroup).text =
            getString(R.string.your_group,
                defaultSharedPreferences.getString(
                    MainActivity.SHARED_PREFERENCES_GROUP_NAME,
                    getString(R.string.error)
                )
            )

//        This an idea is dead because API it shit. There are some reasons:
//        • Student's data can be a English or a Ukrainian;
//        • secondName can be empty BUT at the same time exist.
//        • API doesn't provide either a Ukrainian or a English data.
//        Therefore, it will only be a comment until the API becomes at least normal.
//        view.findViewById<KebabMenuView>(R.id.kebabMenu).onMenuItemSelected = PopupMenu
//            .OnMenuItemClickListener {
//            when (it.itemId) {
//                R.id.settings -> {
//                    findNavController().navigate(R.id.to_settingsFragment)
//                    true
//                }
//                R.id.infoAboutApp -> {
//                    findNavController().navigate(R.id.to_infoAboutAppFragment)
//                    true
//                }
//                R.id.bellSchedule -> {
//                    findNavController().navigate(R.id.to_bellScheduleFragment)
//                    true
//                }
//                R.id.studentsList -> {
//                    findNavController().navigate(R.id.to_listOfStudentsFragment)
//                    true
//                }
//                else -> false
//            }
//        }
//        For now, it will be as default implementation:
        view.run {
            findViewById<KebabMenuView>(R.id.kebabMenu)
                .setDefaultOnMenuItemSelected(this@SelectWeekdayFragment)
            findViewById<AppCompatButton>(R.id.okayButton).setOnClickListener {
                okayButtonListener()
            }
            findViewById<TextView>(R.id.changeGroup).setOnClickListener {
                findNavController().navigate(R.id.from_selectWeekdayFragment_to_wantChangeFragment)
            }
        }
        setDateFormats(view)
    }

    override fun onResume() {
        super.onResume()
        view?.also { view ->
            if (!viewModel.sameDateFormats()) {
                viewModel.saveDateFormat()
                Log.i(LOG_TAG, "[Same date formats] Not same.")
                scheduleDateEditText.text.clear()
                if (viewModel.selectedItem.value == 0) {
                    view.findViewById<Spinner>(R.id.weekdaysSpinner).setSelection(0)
                    viewModel.selectItem(0)
                }
            } else Log.i(LOG_TAG, "[Same date formats] Same.")
        }
    }

    private fun okayButtonListener() {
        lateinit var dateContainer: DateContainer
        if(viewModel.selectedItem.value != 0) {
            scheduleDateEditText.text?.clear()
        }
        val selectedDate: String = scheduleDateEditText.text.toString()

        if(selectedDate.isNotEmpty()) {
            dateContainer = DateContainerBuilder.checkUserDate(
                selectedDate,
                viewModel.selectedDateFormat.value
            )
        }

        if(viewModel.selectedItem.value == 0 && dateContainer.errorType != -1) {
            viewModel.showDateErrorMessage(requireContext(), dateContainer)
        }
        else {
            findNavController().navigate(
                R.id.from_selectWeekdayFragment_to_scheduleFragment,
                viewModel.getScheduleBundle(selectedDate)
            )
        }
    }

    private fun setUpSpinner(view: View) {
        val spinner = view.findViewById<Spinner>(R.id.weekdaysSpinner)
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.select_weekday_options,
            R.layout.app_spinner_item
        )
        adapter.setDropDownViewResource(R.layout.app_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = OnItemSelectedListener()
    }

    private fun setDateFormats(view: View) {
        val dateFormats = resources.getStringArray(R.array.date_formats)
        val currentDateFormat = dateFormats[viewModel.selectedDateFormat.value]
        view.run {
            findViewById<TextView>(R.id.dateFormatHelper).text = getString(R.string.date_format,
                currentDateFormat
            )
            findViewById<EditText>(R.id.scheduleDateEditText).hint = currentDateFormat
        }
    }

    private inner class OnItemSelectedListener  : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            val weekdays = viewModel.mutableWeekdays.value
            if(position == weekdays.lastIndex) {
                if(scheduleDateEditText.text.isNullOrEmpty()) {
                    scheduleDateEditText.setText(
                        viewModel.getDateNow()
                    )
                }
            }
            scheduleDateEditText.visibility =  if(position == weekdays.lastIndex) {
                View.VISIBLE
            }
            else {
                hideKeyboard(requireActivity())
                View.GONE
            }
            view?.clearFocus()
            viewModel.selectItem(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
    }

    fun hideKeyboard(activity: Activity) {
        val view = activity.findViewById<View>(android.R.id.content)
        if (view != null) {
            val inputMethodManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    companion object {
        private const val LOG_TAG = "Select weekday fragment"
    }
}