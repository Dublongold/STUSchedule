package com.helpful.stuSchedule.views.receivingData.structure

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
import com.helpful.stuSchedule.models.receivingData.StudentDataContainer
import kotlinx.coroutines.launch

class StructureFragment: Fragment() {
    private val viewModel: StructureViewModel by viewModels()
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
        view.apply {
            findViewById<TextView>(R.id.collectingDataTitle).text =
                getString(R.string.structure_title)
            findViewById<TextView>(R.id.collectingDataText).text =
                Html.fromHtml(getString(R.string.structure_text), Html.FROM_HTML_MODE_LEGACY)
            findViewById<TextView>(R.id.collectingDataLabel).text =
                getString(R.string.structure_label)
            spinner = findViewById(R.id.collectingDataSpinner)
            findViewById<AppCompatButton>(R.id.backButton).setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewLifecycleOwner.lifecycleScope.launch {
            val structures = viewModel.getStructures().map {
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
                        R.id.fromStructureFragmentToFacultyFragment,
                        bundleOf(
                            StudentDataContainer.BUNDLE_KEY to StudentDataContainer(
                                viewModel.selectedStructure,
                                null,
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
            viewModel.selectStructure(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
    }
}