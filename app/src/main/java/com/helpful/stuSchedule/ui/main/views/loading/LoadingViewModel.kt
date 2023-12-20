package com.helpful.stuSchedule.ui.main.views.loading

import com.helpful.stuSchedule.data.network.AsuClient
import com.helpful.stuSchedule.views.ModifiedViewModel

class LoadingViewModel: ModifiedViewModel() {

    suspend fun checkGroupId(groupId: Int, groupName: String, facultyId: Int, course: Int): Boolean {
        val client = AsuClient()
        val groups = client.getGroupsByCourseAndFacultyId(facultyId, course)
        return groups.any {
            it.id == groupId && it.name == groupName
        }
    }
}