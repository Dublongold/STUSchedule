package com.helpful.stuSchedule.ui.others.settings

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ArrayRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.helpful.stuSchedule.R
import com.helpful.stuSchedule.ui.others.custom.ThemeItemView
import com.helpful.stuSchedule.settings.SettingsOfUser
import com.helpful.stuSchedule.views.ModifiedFragment
import com.helpful.stuSchedule.views.custom.TitleTextView
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsFragment : ModifiedFragment() {
    override val viewModel: SettingsViewModel by viewModels()
    override val fragmentLayoutId = R.layout.fragment_settings
    private lateinit var saveButton: AppCompatButton
    private lateinit var cancelButton: AppCompatButton

    private lateinit var languageSpinner: Spinner
    private lateinit var weekdayTextTypeSpinner: Spinner
    private lateinit var lessonTypeTextTypeSpinner: Spinner
    private lateinit var dateFormatSpinner: Spinner

    private lateinit var themeItemViews: List<ThemeItemView>

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (viewModel.same.value) {
                remove()
            }
            onWantLeave()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Theme item views setting on click listener.
        themeItemViews = getAllViews<ThemeItemView>(view).toList()

        themeItemViews.forEachIndexed { index, themeItemView ->
            themeItemView.setOnClickListener {
                viewModel.selectTheme(index)
            }
        }

        view.run {
            // Assigning late init variables.
            saveButton = findViewById(R.id.saveButton)
            cancelButton = findViewById(R.id.cancelButton)

            languageSpinner = findViewById(R.id.languageSpinner)
            weekdayTextTypeSpinner = findViewById(R.id.weekdayTextTypeSpinner)
            lessonTypeTextTypeSpinner = findViewById(R.id.lessonTypeTextTypeSpinner)
            dateFormatSpinner = findViewById(R.id.dateFormatSpinner)
            // Back button
            findViewById<ImageButton>(R.id.backButton).run {
                setOnClickListener {
                    onWantLeave()
                }
            }
            // Save button.
            findViewById<AppCompatButton>(R.id.saveButton).setOnClickListener {
                viewModel.save()
                onViewCreated(view, savedInstanceState)
            }
            // Cancel button.
            findViewById<AppCompatButton>(R.id.cancelButton).run {
                setOnClickListener {
                    cancelChanges()
                }
            }
            // Question button.
            findViewById<AppCompatButton>(R.id.questionButton).setOnClickListener {
                AlertDialog.Builder(context, R.style.App_AlertDialog)
                    .setMessage(R.string.some_help_information)
                    .setNeutralButton(R.string.close) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
            // Spinner setting.
            viewModel.run {
                setUpSpinner(
                    languageSpinner,
                    R.array.languages,
                    viewModel.selectedLanguage.value,
                    ::selectLanguage
                )
                setUpSpinner(
                    weekdayTextTypeSpinner,
                    R.array.weekday_text_types,
                    viewModel.selectedWeekdayTextType.value,
                    ::selectWeekdayTextType
                )
                setUpSpinner(
                    lessonTypeTextTypeSpinner,
                    R.array.lesson_type_text_types,
                    viewModel.selectedLessonTypeTextType.value,
                    ::selectLessonTypeTextType
                )
                setUpSpinner(
                    dateFormatSpinner,
                    R.array.date_formats,
                    viewModel.selectedDateFormat.value,
                    ::selectDateFormat
                )
            }
            // Observing if settings are same.
            viewModel.same.observe {
                saveButton.setActiveOrInactiveState(!it)
                cancelButton.setActiveOrInactiveState(!it)
            }
            // Observing selected theme.
            viewModel.currentSelectedTheme.observe {
                themeItemViews.forEachIndexed { index, themeItemView ->
                    themeItemView.isSelectedTheme = it == index
                }
            }
            // Radio button check.
            checkTextColorRadioButton()
            activity?.onBackPressedDispatcher?.addCallback(onBackPressedCallback)
        }
        applyStyleChanges()
    }

    private fun applyStyleChanges() {
        val primaryColor = TypedValue()
        val textColor = TypedValue()

        context?.theme?.resolveAttribute(R.attr.appPrimaryColor, primaryColor, true)
        context?.theme?.resolveAttribute(R.attr.appTextColor, textColor, true)

        cancelButton.backgroundTintList = ColorStateList.valueOf(primaryColor.data)
        saveButton.backgroundTintList = ColorStateList.valueOf(primaryColor.data)

        view?.run {
            findViewById<TitleTextView>(R.id.titleText).refreshBackground()
            getAllViews<TextView>(this){
                it !is TitleTextView && it !is AppCompatButton
            }.forEach {
                it.setTextColor(textColor.data)
            }
            getAllViews<RadioButton>(this).forEach {
                it.setTextColor(textColor.data)
            }
        }
    }

    private fun AppCompatButton.setActiveOrInactiveState(isActive: Boolean) {
        visibility = if (isActive) View.VISIBLE else View.GONE
        isEnabled = isActive
    }

    private fun<T> StateFlow<T>.observe(collector: (T) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                collect(collector)
            }
        }
    }

    private fun cancelChanges() {
        viewModel.reset()
        view.run {
            languageSpinner.setSelection(viewModel.selectedLanguage.value)
            weekdayTextTypeSpinner.setSelection(viewModel.selectedWeekdayTextType.value)
            lessonTypeTextTypeSpinner.setSelection(viewModel.selectedLessonTypeTextType.value)
            dateFormatSpinner.setSelection(viewModel.selectedDateFormat.value)
            checkTextColorRadioButton()
        }
    }

    private fun checkTextColorRadioButton() {
        view?.findViewById<RadioGroup>(R.id.selectTextColor)?.run{
            if (viewModel.selectedColorOnOrange.value == SettingsOfUser.SELECTED_COLOR_ON_ORANGE_BLACK) {
                check(R.id.selectTextColorBlack)
            }
            getAllViews<RadioButton>(this).forEach {
                it.setOnClickListener {_ ->
                    viewModel.selectTextColor(
                        if (it.id == R.id.selectTextColorBlack) {
                            SettingsOfUser.SELECTED_COLOR_ON_ORANGE_BLACK
                        }
                        else {
                            SettingsOfUser.SELECTED_COLOR_ON_ORANGE_WHITE
                        }
                    )
                }
            }
        }
    }
    private fun setUpSpinner(
        spinner: Spinner,
        @ArrayRes
        stringArrayId: Int,
        selectedItem: Int = 0,
        onSelected: (Int) -> Unit) {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            stringArrayId,
            R.layout.app_spinner_item
        ).apply {
            setDropDownViewResource(R.layout.app_spinner_dropdown_item)
        }

        spinner.adapter = adapter
        spinner.setSelection(selectedItem)
        spinner.backgroundTintList = ColorStateList.valueOf(TypedValue().also {
            context?.theme?.resolveAttribute(R.attr.appTextColor, it, true)
        }.data)
        spinner.onItemSelectedListener = OnItemSelected(onSelected)
    }

    private fun onWantLeave() {
        view?.run {
            if (viewModel.same.value) {
                findNavController().popBackStack()
                onBackPressedCallback.remove()
            } else {
                val alertDialog = AlertDialog.Builder(context, R.style.App_AlertDialog)
                    .setTitle(R.string.are_you_sure)
                    .setMessage(R.string.unsaved_changes)
                    .setPositiveButton(R.string.save) { _, _ ->
                        viewModel.save()
                        onBackPressedCallback.remove()
                        findNavController().popBackStack()
                    }
                    .setNegativeButton(R.string.look_again) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                alertDialog.show()
            }
        }
    }

    private inner class OnItemSelected(private val action: (Int) -> Unit): OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            action(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) { }
    }

    companion object {
        @Suppress("unused")
        private const val LOG_TAG = "Settings fragment"
    }
}