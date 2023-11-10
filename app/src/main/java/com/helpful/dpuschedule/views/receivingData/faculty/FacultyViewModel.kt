package com.helpful.dpuschedule.views.receivingData.faculty

import androidx.lifecycle.ViewModel
import com.helpful.dpuschedule.models.receivingData.Faculty
import com.helpful.dpuschedule.models.receivingData.StudentDataContainer
import com.helpful.dpuschedule.network.AsuClient
import kotlinx.coroutines.flow.MutableStateFlow

class FacultyViewModel: ViewModel() {
    private val client =
        AsuClient()

    private val mutableSelectedFaculty =
        MutableStateFlow<Faculty?>(null)
    private val faculties =
        MutableStateFlow<List<Faculty>?>(null)
    val studentDataContainer =
        MutableStateFlow<StudentDataContainer?>(null)

    val selectedFaculty
        get() = mutableSelectedFaculty.value!!

    suspend fun getFacultiesByStructureId(structureId: Int): List<Faculty> {
        return client.getFacultiesByStructureId(structureId).also {
            faculties.value = it
        }
    }


    fun selectFaculty(position: Int) {
        faculties.value?.let {
            mutableSelectedFaculty.value = it[position]
        }
    }
}