@file:Suppress("MemberVisibilityCanBePrivate")

package com.helpful.stuSchedule.views.custom

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.helpful.stuSchedule.R

class KebabMenuView : AppCompatImageButton {
    var menuId: Int? = null
    var onMenuItemSelected: PopupMenu.OnMenuItemClickListener? = null
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
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.KebabMenuView)
        val imageColor = TypedValue()

        menuId = attributes.getResourceId(R.styleable.KebabMenuView_menuId, -1)
        context.theme.resolveAttribute(R.attr.appTextColor, imageColor, true)

        setBackgroundColor(Color.TRANSPARENT)
        setImageResource(R.drawable.kebab_menu)
        foreground = ResourcesCompat.getDrawable(
            resources,
            R.drawable.round_button_foreground,
            null
        )
        imageTintList = ColorStateList.valueOf(imageColor.data)

        setOnClickListener {
            menuId?.let {
                if (it != -1) {
                    val wrappedContext = ContextThemeWrapper(context, R.style.App_Popup)
                    PopupMenu(wrappedContext, this).apply {
                        inflate(it)
                        setOnMenuItemClickListener(onMenuItemSelected)
                        show()
                    }
                }
            }
        }

        attributes.recycle()
    }

    fun setDefaultOnMenuItemSelected(fragment: Fragment) {
        onMenuItemSelected = PopupMenu
            .OnMenuItemClickListener {
                when (it.itemId) {
                    R.id.settings -> {
                         fragment.findNavController().navigate(R.id.to_settingsFragment)
                        true
                    }
                    R.id.infoAboutApp -> {
                        fragment.findNavController().navigate(R.id.to_infoAboutAppFragment)
                        true
                    }
                    R.id.bellSchedule -> {
                        fragment.findNavController().navigate(R.id.to_bellScheduleFragment)
                        true
                    }
                    else -> false
                }
            }
    }
}