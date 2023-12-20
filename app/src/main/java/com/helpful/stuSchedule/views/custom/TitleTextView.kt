package com.helpful.stuSchedule.views.custom

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
import com.helpful.stuSchedule.R

class TitleTextView : AppCompatTextView {

    var defaultTitleTextSize = true
        set(value) {
            field = value
            checkIfIsDefaultTitleTextSize()
        }
    constructor(context: Context) : super(context) {
        initialization(null)
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialization(attrs)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        initialization(attrs)
    }

    private fun initialization(attrs: AttributeSet?) {
        val titleBackgroundColor = TypedValue()
        val titleTextColor = TypedValue()
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.TitleTextView)
        defaultTitleTextSize  = attributes.getBoolean(
            R.styleable.TitleTextView_defaultTitleTextSize,
            true
        )

        checkIfIsDefaultTitleTextSize()

        context.theme.resolveAttribute(R.attr.appPrimaryColor, titleBackgroundColor, true)
        context.theme.resolveAttribute(R.attr.appTextColor, titleTextColor, true)

        setBackgroundColor(titleBackgroundColor.data)
        setTextColor(titleTextColor.data)

        attributes.recycle()
    }

    fun refreshBackground() {
        val titleBackgroundColor = TypedValue()

        checkIfIsDefaultTitleTextSize()

        context.theme.resolveAttribute(R.attr.appPrimaryColor, titleBackgroundColor, true)

        setBackgroundColor(titleBackgroundColor.data)
    }

    private fun checkIfIsDefaultTitleTextSize() {
        if (defaultTitleTextSize) {
            setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                24f
            )
        }
    }
}