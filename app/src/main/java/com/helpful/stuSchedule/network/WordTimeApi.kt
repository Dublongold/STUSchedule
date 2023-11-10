package com.helpful.stuSchedule.network

import com.helpful.stuSchedule.models.WordTime
import retrofit2.Response
import retrofit2.http.GET

interface WordTimeApi {
    @GET("timezone/Europe/Kyiv")
    suspend fun getKyivTime(): Response<WordTime>
}