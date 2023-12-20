package com.helpful.stuSchedule.ui.main.views.custom

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.allViews
import com.helpful.stuSchedule.R

abstract class ScheduleItemView : LinearLayoutCompat {

    constructor(context: Context) :
            super(context)
    constructor(context: Context, attrs: AttributeSet? = null) :
            super (context, attrs)
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int) :
            super (context, attrs, defStyleAttr)

    protected fun makeForegroundAsSelectedIfTrue(condition: Boolean) {
        val strokeDrawable = TypedValue()
        context.theme.resolveAttribute(R.attr.lessonStrokeDrawable, strokeDrawable, true)
        foreground = if (condition) {
            ResourcesCompat.getDrawable(
                resources,
                strokeDrawable.resourceId,
                null
            )
        } else null
    }

    protected fun makeTextColorWhite() {
        allViews.filter { it is TextView }
            .map { it as TextView }
            .forEach {
                it.setTextColor(Color.WHITE)
            }
    }
}