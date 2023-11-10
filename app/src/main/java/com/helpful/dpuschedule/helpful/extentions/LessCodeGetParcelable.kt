package com.helpful.dpuschedule.helpful.extentions

import android.os.Build
import android.os.Bundle
import android.os.Parcelable

inline fun<reified T> Bundle.getParcelableNormally(key: String): T? where T : Parcelable {
    @Suppress("DEPRECATION")
    return if(Build.VERSION.SDK_INT >= 33)
        getParcelable(key, T::class.java)
    else
        getParcelable<T>(key)
}