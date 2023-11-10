package com.helpful.dpuschedule.views.receivingData.course

import androidx.lifecycle.ViewModel
import com.helpful.dpuschedule.models.receivingData.Course
import com.helpful.dpuschedule.models.receivingData.StudentDataContainer
import com.helpful.dpuschedule.network.AsuClient
import kotlinx.coroutines.flow.MutableStateFlow

class CourseViewModel: ViewModel() {
    private val client = AsuClient()

    private val mutableSelectedCourse =
        MutableStateFlow<Course?>(null)
    private val courses =
        MutableStateFlow<List<Course>?>(null)
    val studentDataContainer =
        MutableStateFlow<StudentDataContainer?>(null)

    val selectedCourse
        get() = mutableSelectedCourse.value!!

    suspend fun getCoursesByFacultyId(facultyId: Int): List<Course> {
        return client.getCoursesByFacultyId(facultyId).also {
            courses.value = it
        }
    }


    fun selectCourse(position: Int) {
        courses.value?.let {
            mutableSelectedCourse.value = it[position]
        }
    }
}