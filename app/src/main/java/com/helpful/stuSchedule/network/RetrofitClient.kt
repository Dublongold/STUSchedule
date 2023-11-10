package com.helpful.stuSchedule.network

import android.util.Log
import kotlinx.coroutines.delay
import java.net.SocketTimeoutException
import java.net.UnknownHostException

abstract class RetrofitClient {
    protected suspend fun<T> doWhileNetworkErrors(action: suspend () -> T): T {
        while(true) {
            try {
                return action()
            } catch (e: UnknownHostException) {
                Log.e("AsuAPI client", e.stackTraceToString())
                delay(500)
            }
            catch (e: SocketTimeoutException) {
                Log.e("AsuAPI client", e.stackTraceToString())
                delay(500)
            }
        }
    }
}