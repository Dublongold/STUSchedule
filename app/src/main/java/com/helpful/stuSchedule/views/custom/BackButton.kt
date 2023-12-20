package com.helpful.stuSchedule.views.custom

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.res.ResourcesCompat
import com.helpful.stuSchedule.R

class BackButton : AppCompatImageButton {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    init {
        val buttonColor = TypedValue()
        id = R.id.backButton
        context.theme.resolveAttribute(R.attr.appTextColor, buttonColor, true)
        imageTintList = ColorStateList.valueOf(buttonColor.data)
        setBackgroundResource(android.R.color.transparent)
        foreground = ResourcesCompat.getDrawable(
            resources,
            R.drawable.round_button_foreground,
            null
        )
        scaleType = ScaleType.CENTER_CROP
        setImageResource(R.drawable.back_icon)
        contentDescription = resources.getString(R.string.back_button_description)
    }
}