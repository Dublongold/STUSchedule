package com.helpful.dpuschedule.views.custom.schedule

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.LinearLayoutCompat

abstract class ScheduleItemView : LinearLayoutCompat {
    constructor(context: Context) :
            super(context)
    constructor(context: Context, attrs: AttributeSet? = null) :
            super (context, attrs)
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int) :
            super (context, attrs, defStyleAttr)
}