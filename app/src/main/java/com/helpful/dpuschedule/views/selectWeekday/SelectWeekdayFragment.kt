package com.helpful.dpuschedule.views.selectWeekday

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.helpful.dpuschedule.MainActivity
import com.helpful.dpuschedule.R
import com.helpful.dpuschedule.helpful.extentions.getSharedPreferences
import com.helpful.dpuschedule.helpful.objects.TimeConverter
import com.helpful.dpuschedule.models.DateContainer
import com.helpful.dpuschedule.views.schedule.ScheduleFragment
import java.util.Calendar


class SelectWeekdayFragment : Fragment() {

    private val viewModel: SelectWeekdayViewModel by viewModels()

    private lateinit var scheduleDateEditText: EditText

    private var weekdays: Array<String> = emptyArray()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_select_weekday, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        weekdays = resources.getStringArray(R.array.select_weekday_options)
        view.run {
            scheduleDateEditText = findViewById(R.id.scheduleDateEditText)
        }

        setUpSpinner(view)

        view.findViewById<TextView>(R.id.yourGroup).text =
            getString(R.string.your_group,
                getSharedPreferences(
                    MainActivity.DEFAULT_SHARED_PREFERENCES,
                    AppCompatActivity.MODE_PRIVATE
                ).getString(
                    MainActivity.SHARED_PREFERENCES_GROUP_NAME,
                    getString(R.string.error)
                )
            )

        view.findViewById<AppCompatButton>(R.id.okayButton).setOnClickListener {
            val currentYear = Calendar.getInstance()[Calendar.YEAR]
            lateinit var dateContainer: DateContainer
            if(viewModel.selectedItem.value != 0) {
                scheduleDateEditText.text?.clear()
            }
            val selectedDate: String = scheduleDateEditText.text.toString()

            val errorType: Int = if(selectedDate.isEmpty()) 0 else {
                dateContainer = DateContainer.checkUserDate(selectedDate)
                dateContainer.errorType
            }

            if(viewModel.selectedItem.value == 0 && errorType != -1) {
                val errorMessages = resources.getStringArray(R.array.date_error_messages)
                val errorMessage = when(errorType) {
                    0, 1, 2, 4 -> errorMessages[errorType]
                    3 -> errorMessages[errorType].format(currentYear - 3, currentYear)
                    5 -> errorMessages[errorType].format(
                        DateContainer.lastDayOfMonth(dateContainer.month, dateContainer.year)
                    )
                    else -> errorMessages.last()
                }
                Toast.makeText(
                    requireContext(),
                    getString(
                        R.string.date_error_message_format_string,
                        getString(R.string.invalid_date),
                        errorMessage
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            }
            else {
                findNavController().navigate(
                    R.id.fromSelectWeekdayFragmentToScheduleFragment,
                    bundleOf(
                        ScheduleFragment.ARGUMENT_WEEKDAY to viewModel.selectedItem.value,
                        ScheduleFragment.ARGUMENT_SELECTED_DATE to
                                when(viewModel.selectedItem.value) {
                                    0 -> selectedDate
                                    -1 -> viewModel.getDateNow(
                                        getString(
                                            R.string.schedule_date_format_string
                                        )
                                    )
                                    else -> null
                                }
                    )
                )
            }
        }
        view.findViewById<TextView>(R.id.changeGroup).setOnClickListener {
            findNavController().navigate(R.id.fromSelectWeekdayFragmentToWantChangeGroupFragment)
        }
    }

    private fun setUpSpinner(view: View) {
        val spinner = view.findViewById<Spinner>(R.id.weekdaysSpinner)
        val adapter = ArrayAdapter.createFromResource(requireContext(), R.array.select_weekday_options,
            android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = OnItemSelectedListener()
    }

    inner class OnItemSelectedListener  : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            if(position == weekdays.lastIndex) {
                if(scheduleDateEditText.text.isNullOrEmpty()) {
                    scheduleDateEditText.setText(
                        viewModel.getDateNow(getString(R.string.schedule_date_format_string))
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
}