package com.helpful.stuSchedule.views.receivingData.wantChangeGroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.helpful.stuSchedule.R

class WantChangeFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_want_change_group, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<AppCompatButton>(R.id.startButton).setOnClickListener {
            findNavController().navigate(R.id.fromWantChangeFragmentToStructureFragment)
        }
        view.findViewById<AppCompatButton>(R.id.cancelButton).setOnClickListener {
            findNavController().popBackStack()
        }
    }
}