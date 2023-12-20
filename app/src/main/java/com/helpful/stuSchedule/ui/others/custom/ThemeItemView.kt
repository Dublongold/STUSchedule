package com.helpful.stuSchedule.ui.others.custom

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.setMargins
import com.helpful.stuSchedule.R

class ThemeItemView: FrameLayout {
    var themeDrawable: Int = 0
        set(value) {
            field = value
            setBackgroundResource(value)
        }
    var isSelectedTheme: Boolean = false
        set(value) {
            field = value
            changeIsSelectedTheme()
        }

    private val Int.dp
        get() = (this * resources.displayMetrics.density).toInt()
    constructor(context: Context) : super(context) {
        initialization()
    }
    constructor(context: Context, attrs: AttributeSet?) :
            super(context, attrs) {
        initialization(attrs)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        initialization(attrs)
    }
    @Suppress("unused")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
            super(context, attrs, defStyleAttr, defStyleRes) {
        initialization(attrs)
    }


    private fun initialization(attrs: AttributeSet? = null) {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.ThemeItemView)
        themeDrawable = attributes.getResourceId(
            R.styleable.ThemeItemView_themeDrawable,
            0
        )
        addView(ImageView(context).apply {
            id = R.id.isSelectedThemeIndicator
            layoutParams = LayoutParams(15.dp, 15.dp).apply {
                gravity = Gravity.BOTTOM or Gravity.END
                setMargins(5.dp)
            }
        })
        isSelectedTheme = attributes.getBoolean(
            R.styleable.ThemeItemView_isSelectedTheme, false
        )

        attributes.recycle()
    }

    private fun changeIsSelectedTheme() {
        val indicatorDrawableResource = if (isSelectedTheme) {
            R.drawable.selected_item
        } else {
            R.drawable.not_selected_item
        }
        findViewById<ImageView>(R.id.isSelectedThemeIndicator)
            .setBackgroundResource(indicatorDrawableResource)
    }
}
/*
<FrameLayout
    android:layout_width="100dp"
    android:layout_height="162.5dp"
    android:background="@drawable/orange_theme_selector">
    <ImageView
        android:layout_width="15dp"
        android:layout_height="15dp"
        android:layout_gravity="bottom|end"
        android:layout_margin="5dp"
        app:srcCompat="@drawable/not_selected_theme_shape"/>
</FrameLayout>
 */