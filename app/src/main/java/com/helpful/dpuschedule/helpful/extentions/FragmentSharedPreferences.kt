package com.helpful.dpuschedule.helpful.extentions

import android.content.SharedPreferences
import androidx.fragment.app.Fragment

fun Fragment.getSharedPreferences(name: String, type: Int): SharedPreferences {
    return requireActivity().getSharedPreferences(name, type)
}