package com.helpful.stuSchedule.views.receivingData.faculty

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.helpful.stuSchedule.R
import com.helpful.stuSchedule.helpful.extentions.getParcelableNormally
import com.helpful.stuSchedule.models.receivingData.StudentDataContainer
import kotlinx.coroutines.launch

class FacultyFragment: Fragment() {
    private val viewModel: FacultyViewModel by viewModels()
    private lateinit var spinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_collecting_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.studentDataContainer.value =
            arguments?.getParcelableNormally(StudentDataContainer.BUNDLE_KEY)

        view.apply {
            findViewById<TextView>(R.id.collectingDataTitle).text =
                getString(R.string.faculty_title)

            findViewById<TextView>(R.id.collectingDataText).text =
                Html.fromHtml(
                    getString(R.string.faculty_text,
                        viewModel.studentDataContainer.value?.structure?.shortName
                        ?: getString(R.string.error)
                    ),
                    Html.FROM_HTML_MODE_LEGACY
                )
            findViewById<TextView>(R.id.collectingDataLabel).text =
                getString(R.string.faculty_label)
            spinner = findViewById(R.id.collectingDataSpinner)
            findViewById<AppCompatButton>(R.id.backButton).setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewLifecycleOwner.lifecycleScope.launch {
            val structures = viewModel.getFacultiesByStructureId(
                viewModel.studentDataContainer.value?.structure?.id ?: 0
            ).map {
                it.shortName
            }
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                structures
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spinner.adapter = adapter
            spinner.onItemSelectedListener = OnItemSelectedListener()
            view?.run {
                findViewById<AppCompatButton>(R.id.nextButton)?.setOnClickListener {
                    findNavController().navigate(
                        R.id.fromFacultyFragmentToCourseFragment,
                        bundleOf(
                            StudentDataContainer.BUNDLE_KEY to StudentDataContainer(
                                viewModel.studentDataContainer.value?.structure,
                                viewModel.selectedFaculty,
                                null,
                                null
                            )
                        )
                    )
                }
                findViewById<ProgressBar>(R.id.collectingDataProgressBar).visibility = View.GONE
            }
        }
    }

    inner class OnItemSelectedListener : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            viewModel.selectFaculty(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
    }
}