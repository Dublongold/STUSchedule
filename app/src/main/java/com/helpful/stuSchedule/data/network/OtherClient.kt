package com.helpful.stuSchedule.data.network

import android.util.Log
import com.helpful.stuSchedule.data.models.other.BellScheduleItem
import com.helpful.stuSchedule.data.models.other.Student
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OtherClient : RetrofitClient() {
    private val client = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OtherAPI::class.java)

    private fun<T> getEntityListIfSuccessful(response: Response<List<T>>): List<T>{
        return if (response.isSuccessful) {
            val body = response.body()
            if (!body.isNullOrEmpty()) {
                body
            }
            else {
                Log.i(LOG_TAG, "Body is ${if (body == null) "null" else "empty"}.")
                emptyList()
            }
        }
        else {
            Log.e(LOG_TAG, "Response is bad. Code: ${response.code()}.")
            emptyList()
        }
    }
    suspend fun getBellSchedule(): List<BellScheduleItem> = doWhileNetworkErrors {
        getEntityListIfSuccessful(
            client.getBellSchedule()
        )
    }

    suspend fun getStudentsByGroup(groupId: Int): List<Student> = doWhileNetworkErrors {
        getEntityListIfSuccessful(
            client.getStudentsByGroup(groupId)
        )
    }

    companion object {
        private const val LOG_TAG = "Other client"
    }
}