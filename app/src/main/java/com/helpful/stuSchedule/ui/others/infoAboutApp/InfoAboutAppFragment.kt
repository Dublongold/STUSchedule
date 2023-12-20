package com.helpful.stuSchedule.ui.others.infoAboutApp

import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.helpful.stuSchedule.R
import com.helpful.stuSchedule.views.DefaultImplementationViewModel
import com.helpful.stuSchedule.views.ModifiedFragment

class InfoAboutAppFragment: ModifiedFragment() {
    override val viewModel: DefaultImplementationViewModel by viewModels()
    override val fragmentLayoutId = R.layout.fragment_info_about_app

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.run{
            findViewById<ImageButton>(R.id.backButton).run {
                setOnClickListener {
                    findNavController().popBackStack()
                }
            }
            findViewById<TextView>(R.id.infoAboutAppContent).text = Html.fromHtml(
                getString(R.string.info_about_app_content),
                Html.FROM_HTML_MODE_LEGACY
            )
        }
    }
}