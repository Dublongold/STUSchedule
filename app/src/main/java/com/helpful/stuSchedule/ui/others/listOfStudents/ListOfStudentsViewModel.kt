package com.helpful.stuSchedule.ui.others.listOfStudents

import androidx.lifecycle.viewModelScope
import com.helpful.stuSchedule.data.models.other.Student
import com.helpful.stuSchedule.data.network.OtherClient
import com.helpful.stuSchedule.views.ModifiedViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ListOfStudentsViewModel : ModifiedViewModel() {
    private val client = MutableStateFlow(OtherClient())
    private val mutableStudents: MutableStateFlow<List<Student>> = MutableStateFlow(emptyList())
    val students: StateFlow<List<Student>>
        get() = mutableStudents
    var groupId: Int = -1

    fun loadStudents() {
        viewModelScope.launch {
            val students: MutableList<Student> = client.value.getStudentsByGroup(groupId)
                .toMutableList()
            mutableStudents.value = students
        }
    }
}