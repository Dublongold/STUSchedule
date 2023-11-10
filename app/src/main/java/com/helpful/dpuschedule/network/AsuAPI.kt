package com.helpful.dpuschedule.network

import com.helpful.dpuschedule.models.receivingData.Course
import com.helpful.dpuschedule.models.receivingData.Faculty
import com.helpful.dpuschedule.models.receivingData.Structure
import com.helpful.dpuschedule.models.DataReceiver
import com.helpful.dpuschedule.models.receivingData.Group
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface AsuAPI {
    @POST("time-table/group")
    @FormUrlEncoded
    suspend fun getData(
        @Field("groupId") groupId: Int,
        @Field("dateStart") dateStart: String,
        @Field("dateEnd") dateEnd: String
    ): Response<List<DataReceiver>>

    @GET("list/structures")
    suspend fun getStructures(): Response<List<Structure>>

    @POST("list/faculties")
    @FormUrlEncoded
    suspend fun getFacultiesByStructureId(
        @Field("structureId") structureId: Int
    ): Response<List<Faculty>>

    @POST("list/courses")
    @FormUrlEncoded
    suspend fun getCoursesByFacultyId(
        @Field("facultyId") facultyId: Int
    ): Response<List<Course>>

    @POST("list/groups")
    @FormUrlEncoded
    suspend fun getGroupsByCourseAndFacultyId(
        @Field("course") course: Int,
        @Field("facultyId") facultyId: Int
    ): Response<List<Group>>
}