package com.helpful.stuSchedule.data.network

import com.helpful.stuSchedule.data.models.other.BellScheduleItem
import com.helpful.stuSchedule.data.models.other.Student
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface OtherAPI {
    @POST("time-table/call-schedule")
    suspend fun getBellSchedule(): Response<List<BellScheduleItem>>

    @POST("list/students-by-group")
    @FormUrlEncoded
    suspend fun getStudentsByGroup(@Field("groupId") groupId: Int): Response<List<Student>>
}