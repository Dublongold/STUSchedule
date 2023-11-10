package com.helpful.dpuschedule.views.receivingData.group

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
import com.helpful.dpuschedule.R
import com.helpful.dpuschedule.helpful.extentions.getParcelableNormally
import com.helpful.dpuschedule.models.receivingData.StudentDataContainer
import kotlinx.coroutines.launch

class GroupFragment: Fragment() {
    private val viewModel: GroupViewModel by viewModels()
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
                getString(R.string.group_title)
            val structure = viewModel.studentDataContainer.value?.structure?.shortName
                ?: getString(R.string.error)
            val faculty = viewModel.studentDataContainer.value?.faculty?.shortName ?: getString(R.string.error)
            val course = viewModel.studentDataContainer.value?.course?.course ?: getString(R.string.error)
            findViewById<TextView>(R.id.collectingDataText).text =
                Html.fromHtml(getString(R.string.group_text, structure, faculty, course), Html.FROM_HTML_MODE_LEGACY)
            findViewById<TextView>(R.id.collectingDataLabel).text =
                getString(R.string.group_label)
            spinner = findViewById(R.id.collectingDataSpinner)
            findViewById<AppCompatButton>(R.id.backButton).setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewLifecycleOwner.lifecycleScope.launch {
            val groups = viewModel.getGroupsByCourseAndFacultyId(
                viewModel.studentDataContainer.value?.course?.course ?: 0,
                viewModel.studentDataContainer.value?.faculty?.id ?: 0
            ).map {
                it.name
            }
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                groups
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spinner.adapter = adapter
            spinner.onItemSelectedListener = OnItemSelectedListener()
            view?.run {
                findViewById<AppCompatButton>(R.id.nextButton)?.setOnClickListener {
                    findNavController().navigate(
                        R.id.fromGroupFragmentToConfirmDataFragment,
                        bundleOf(
                            StudentDataContainer.BUNDLE_KEY to StudentDataContainer(
                                viewModel.studentDataContainer.value?.structure,
                                viewModel.studentDataContainer.value?.faculty,
                                viewModel.studentDataContainer.value?.course,
                                viewModel.selectedGroup
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
            viewModel.selectGroup(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
    }
}