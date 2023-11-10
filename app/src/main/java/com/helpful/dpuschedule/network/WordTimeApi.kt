package com.helpful.dpuschedule.network

import com.helpful.dpuschedule.models.WordTime
import retrofit2.Response
import retrofit2.http.GET

interface WordTimeApi {
    @GET("timezone/Europe/Kyiv")
    suspend fun getKyivTime(): Response<WordTime>
}