package com.helpful.stuSchedule.data.network

import android.util.Log
import com.helpful.stuSchedule.data.models.receivingData.Course
import com.helpful.stuSchedule.data.models.receivingData.Faculty
import com.helpful.stuSchedule.data.models.receivingData.Group
import com.helpful.stuSchedule.data.models.receivingData.Structure
import com.helpful.stuSchedule.data.models.Lesson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AsuClient : RetrofitClient() {
    private val client = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AsuAPI::class.java)

    suspend fun getData(
        selectedDate: String,
        groupId: Int
    ): List<Lesson?> = doWhileNetworkErrors {
        val response = client.getData(groupId, selectedDate, selectedDate)
        if(response.isSuccessful) {
            val body = response.body()?.firstOrNull()
            body?.lessons ?: emptyList()
        }
        else {
            if(response.code() == 422 || response.code() == 500) {
                listOf(null)
            }
            else {
                Log.w("Response", "Get data error: ${response.code()}")
                emptyList()
            }
        }
    }

    suspend fun getGroupsByCourseAndFacultyId(
        course: Int,
        facultyId: Int
    ): List<Group> = doWhileNetworkErrors {
        val response = client.getGroupsByCourseAndFacultyId(course, facultyId)
        if(response.isSuccessful) {
            response.body()!!
        }
        else {
            emptyList()
        }
    }
    suspend fun getStructures(): List<Structure> = doWhileNetworkErrors {
        val response = client.getStructures()
        if (response.isSuccessful) {
            response.body()!!
        } else {
            emptyList()
        }
    }

    suspend fun getFacultiesByStructureId(structureId: Int): List<Faculty> = doWhileNetworkErrors {
        val response = client.getFacultiesByStructureId(structureId)
        if (response.isSuccessful) {
            response.body()!!
        } else {
            emptyList()
        }
    }

    suspend fun getCoursesByFacultyId(facultyId: Int): List<Course> = doWhileNetworkErrors {
        val response = client.getCoursesByFacultyId(facultyId)
        if(response.isSuccessful) {
            response.body()!!
        }
        else {
            emptyList()
        }
    }


}