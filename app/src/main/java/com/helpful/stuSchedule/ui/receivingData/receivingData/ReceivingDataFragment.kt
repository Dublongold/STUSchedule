package com.helpful.stuSchedule.ui.receivingData.receivingData

import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.helpful.stuSchedule.R
import com.helpful.stuSchedule.data.models.receivingData.StudentDataContainer
import com.helpful.stuSchedule.tools.extentions.getParcelableNormally
import com.helpful.stuSchedule.views.ModifiedFragment
import com.helpful.stuSchedule.views.custom.KebabMenuView
import kotlinx.coroutines.launch

abstract class ReceivingDataFragment<T, P>: ModifiedFragment() {
    abstract override val viewModel: ReceivingDataViewModel<T, P>
    override val fragmentLayoutId = R.layout.fragment_receiving_data
    private lateinit var spinner: Spinner

    protected abstract val pageTitleId: Int
    protected abstract val pageTextId: Int
    protected abstract val pageLabelId: Int
    protected abstract val pageTextArgs: Array<Any>?
    protected abstract val nextDestinationId: Int

    private val receivingDataText
        get() = pageTextArgs?.let {getString(pageTextId, *it)} ?: getString(pageTextId)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.studentDataContainer.value = arguments?.getParcelableNormally(
            StudentDataContainer.BUNDLE_KEY
        )
        view.apply {
            findViewById<TextView>(R.id.titleText).text =
                getString(pageTitleId)

            findViewById<TextView>(R.id.collectingDataText).text =
                Html.fromHtml(receivingDataText, Html.FROM_HTML_MODE_LEGACY)

            findViewById<TextView>(R.id.collectingDataLabel).text =
                getString(pageLabelId)

            spinner = findViewById(R.id.collectingDataSpinner)
            findViewById<AppCompatButton>(R.id.backButton).setOnClickListener {
                findNavController().popBackStack()
            }
            findViewById<KebabMenuView>(R.id.kebabMenu)
                .setDefaultOnMenuItemSelected(this@ReceivingDataFragment)
        }
    }

    override fun onStart() {
        super.onStart()
        processData()
    }

    private fun getAdapter(data: List<String>): ArrayAdapter<String> {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.app_spinner_item,
            data
        )
        adapter.setDropDownViewResource(R.layout.app_spinner_dropdown_item)
        return adapter
    }

    abstract fun getDataParameters(): P
    abstract fun mapData(data: T): String
    private fun processData() {
        viewLifecycleOwner.lifecycleScope.launch {
            val data = viewModel.getData(getDataParameters()).map {
                mapData(it)
            }

            spinner.adapter = getAdapter(data)
            spinner.onItemSelectedListener = OnItemSelectedListener()
            view?.run {
                findViewById<AppCompatButton>(R.id.nextButton)?.setOnClickListener {
                    findNavController().navigate(
                        nextDestinationId,
                        viewModel.makeStudentDataContainerBundle(
                            arguments?.getInt(WHERE_STARTS_CHANGING) ?: -1
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
            viewModel.selectData(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) { }
    }

    companion object {
        const val WHERE_STARTS_CHANGING = "where_starts_changing"
    }
}