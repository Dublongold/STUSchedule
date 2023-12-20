package com.helpful.stuSchedule.views

import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.allViews
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.helpful.stuSchedule.MainActivity
import com.helpful.stuSchedule.R
import com.helpful.stuSchedule.settings.SettingsOfUser
import com.helpful.stuSchedule.tools.extentions.themeFlags
import com.helpful.stuSchedule.views.custom.BackButton
import com.helpful.stuSchedule.views.custom.KebabMenuView
import com.helpful.stuSchedule.views.custom.TitleTextView
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


abstract class ModifiedFragment : Fragment() {

    protected abstract val viewModel: ModifiedViewModel

    protected abstract val fragmentLayoutId: Int
    @Suppress("MemberVisibilityCanBePrivate")
    protected var attachFragmentToRoot = false

    protected val Int.dp
        get() = (this * resources.displayMetrics.density).toInt()

    @Suppress("MemberVisibilityCanBePrivate", "SameParameterValue")
    protected fun getSharedPreferences(name: String, type: Int): SharedPreferences {
        return requireActivity().getSharedPreferences(name, type)
    }

    protected val defaultSharedPreferences
        get() = getSharedPreferences(
            MainActivity.DEFAULT_SHARED_PREFERENCES,
            AppCompatActivity.MODE_PRIVATE
        )


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(fragmentLayoutId, container, attachFragmentToRoot)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View?>(R.id.backButton).let {
            if (it != null && it is BackButton) {
                it.setOnClickListener {
                    findNavController().popBackStack()
                }
            }
        }
        getAllViews<TextView>(view) {
            it.themeFlags.contains("color_on_orange")
        }.forEach {
            it.setTextColor(getColorOnOrangeOrWhite())
        }
        getAllViews<TitleTextView>(view).forEach {
            it.setTextColor(getColorOnOrangeOrWhite())
        }
        getAllViews<ProgressBar>(view) {
            it.themeFlags.contains("color_on_orange")
        }.forEach {
            it.indeterminateTintList = ColorStateList.valueOf(getColorOnOrangeOrWhite())
        }
        getAllViews<KebabMenuView>(view).forEach {
            it.imageTintList = ColorStateList.valueOf(getColorOnOrangeOrWhite())
        }
        getAllViews<BackButton>(view).forEach {
            it.imageTintList = ColorStateList.valueOf(getColorOnOrangeOrWhite())
        }
        getAllViews<AppCompatButton>(view).forEach {
            it.setTextColor(getColorOnOrangeOrWhite())
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected fun getColorOnOrange() = if (viewModel.selectedColorOnOrange.value ==
            SettingsOfUser.SELECTED_COLOR_ON_ORANGE_BLACK)
        Color.BLACK
    else
        Color.WHITE

    @Suppress("MemberVisibilityCanBePrivate")
    protected fun getColorOnOrangeOrWhite() =
        if (viewModel.selectedTheme.value == SettingsOfUser.SELECTED_THEME_ORANGE) {
            getColorOnOrange()
        }
    else {
        TypedValue().also {
            context?.theme?.resolveAttribute(R.attr.appTextColor, it, true)
        }.data
    }

    protected inline fun<reified T> getAllViews(
        rootView: View,
        crossinline additionalCondition: (T) -> Boolean = { true }
    ): Sequence<T> where T: View {
        return rootView.allViews.filter {
            it is T && additionalCondition(it)
        }.map {
            it as T
        }
    }

    protected fun<T> observeStateFlow(stateFlow: StateFlow<T>, collect: (T) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                stateFlow.collect(collect)
            }
        }
    }

    companion object {
        @Suppress("unused")
        private const val LOG_TAG = "Modified fragment"
    }
}