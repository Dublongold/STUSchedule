package com.helpful.stuSchedule.network

import android.util.Log
import com.helpful.stuSchedule.models.WordTime
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WordTimeClient: RetrofitClient() {
    private val client = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WordTimeApi::class.java)

    suspend fun getKyivTime(): WordTime = doWhileNetworkErrors {
        val response = client.getKyivTime()
        if(response.isSuccessful) {
            Log.i("Get Kyiv time", "Body: ${response.body()!!}")
            response.body()!!
        }
        else {
            Log.e(
                "Get Kyiv time",
                "Error. Status: ${response.code()}. error body: ${response.errorBody()}"
            )
            WordTime.DEFAULT
        }
    }

    companion object {
        private const val BASE_URL = "http://worldtimeapi.org/api/"
    }
}