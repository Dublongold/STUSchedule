package com.helpful.stuSchedule.network

import android.util.Log
import com.helpful.stuSchedule.models.receivingData.Course
import com.helpful.stuSchedule.models.receivingData.Faculty
import com.helpful.stuSchedule.models.receivingData.Structure
import com.helpful.stuSchedule.models.Lesson
import com.helpful.stuSchedule.models.receivingData.Group
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AsuClient : RetrofitClient() {
    private val client = Retrofit.Builder()
        .baseUrl(baseUrl)
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


    companion object {
        const val baseUrl = "https://api.asu.dpu.edu.ua/"
    }
}