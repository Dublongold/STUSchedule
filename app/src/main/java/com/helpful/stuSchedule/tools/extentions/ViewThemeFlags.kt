package com.helpful.stuSchedule.tools.extentions

import android.util.TypedValue
import android.view.View
import com.helpful.stuSchedule.R

val View.themeFlags: List<String>
    get() {
        val themeFlagsValue = TypedValue()
        context.theme.resolveAttribute(R.attr.themeFlags, themeFlagsValue, true)
        return themeFlagsValue.string?.toString()?.split("|", ",") ?: emptyList()
    }