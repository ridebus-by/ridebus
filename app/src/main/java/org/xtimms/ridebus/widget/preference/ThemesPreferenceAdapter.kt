package org.xtimms.ridebus.widget.preference

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.recyclerview.widget.RecyclerView
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.preference.PreferenceValues
import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.databinding.PrefThemeItemBinding
import org.xtimms.ridebus.ui.base.activity.ThemingDelegate
import org.xtimms.ridebus.util.system.getResourceColor
import uy.kohesive.injekt.injectLazy

//
// Created by Xtimms on 27.08.2021.
//
class ThemesPreferenceAdapter(private val clickListener: OnItemClickListener) :
    RecyclerView.Adapter<ThemesPreferenceAdapter.ThemeViewHolder>() {

    private val preferences: PreferencesHelper by injectLazy()

    private var themes = emptyList<PreferenceValues.AppTheme>()

    private lateinit var binding: PrefThemeItemBinding

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ThemeViewHolder {
        val themeResIds = ThemingDelegate.getThemeResIds(themes[viewType], preferences.themeDarkAmoled().get())
        val themedContext = themeResIds.fold(parent.context) {
            context, themeResId ->
            ContextThemeWrapper(context, themeResId)
        }

        binding = PrefThemeItemBinding.inflate(LayoutInflater.from(themedContext), parent, false)
        return ThemeViewHolder(binding.root)
    }

    override fun getItemViewType(position: Int): Int = position

    override fun onBindViewHolder(holder: ThemeViewHolder, position: Int) {
        holder.bind(themes[position])
    }

    override fun getItemCount(): Int = themes.size

    fun setItems(themes: List<PreferenceValues.AppTheme>) {
        this.themes = themes
        notifyDataSetChanged()
    }

    inner class ThemeViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private val selectedColor = view.context.getResourceColor(androidx.appcompat.R.attr.colorAccent)
        private val unselectedColor = view.context.getResourceColor(android.R.attr.textColorHint)

        fun bind(appTheme: PreferenceValues.AppTheme) {
            binding.name.text = view.context.getString(appTheme.titleResId!!)

            val isSelected = preferences.appTheme().get() == appTheme
            binding.themeCard.isChecked = isSelected
            binding.themeCard.strokeColor = if (isSelected) selectedColor else unselectedColor

            listOf(binding.root, binding.themeCard).forEach {
                it.setOnClickListener {
                    clickListener.onItemClick(bindingAdapterPosition)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}
