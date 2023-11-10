package com.helpful.stuSchedule.views.receivingData.group

import androidx.lifecycle.ViewModel
import com.helpful.stuSchedule.models.receivingData.Group
import com.helpful.stuSchedule.models.receivingData.StudentDataContainer
import com.helpful.stuSchedule.network.AsuClient
import kotlinx.coroutines.flow.MutableStateFlow

class GroupViewModel: ViewModel() {
    private val client = AsuClient()

    private val mutableSelectedGroup =
        MutableStateFlow<Group?>(null)
    private val groups =
        MutableStateFlow<List<Group>?>(null)
    val studentDataContainer =
        MutableStateFlow<StudentDataContainer?>(null)

    val selectedGroup
        get() = mutableSelectedGroup.value!!

    suspend fun getGroupsByCourseAndFacultyId(course: Int, facultyId: Int): List<Group> {
        return client.getGroupsByCourseAndFacultyId(course, facultyId).also {
            groups.value = it
        }
    }

    fun selectGroup(position: Int) {
        groups.value?.let {
            mutableSelectedGroup.value = it[position]
        }
    }
}