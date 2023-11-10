package com.helpful.stuSchedule.views.loading

import androidx.lifecycle.ViewModel
import com.helpful.stuSchedule.network.AsuClient

class LoadingViewModel: ViewModel() {

    suspend fun checkGroupId(groupId: Int, groupName: String, facultyId: Int, course: Int): Boolean {
        val client = AsuClient()
        val groups = client.getGroupsByCourseAndFacultyId(facultyId, course)
        return groups.any {
            it.id == groupId && it.name == groupName
        }
    }
}