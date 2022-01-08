package org.xtimms.ridebus.ui.stops

import android.view.View
import eu.davidea.viewholders.FlexibleViewHolder
import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.databinding.StopsItemBinding
import org.xtimms.ridebus.util.lang.transliterate
import uy.kohesive.injekt.injectLazy

//
// Created by Xtimms on 01.09.2021.
//
class StopsHolder(view: View, val adapter: StopsAdapter) :
    FlexibleViewHolder(view, adapter) {

    private val preferences: PreferencesHelper by injectLazy()

    private val binding = StopsItemBinding.bind(view)

    init {
        binding.holder.setOnClickListener {
            adapter.itemClickListener.onItemClick(bindingAdapterPosition)
        }
    }

    fun bind(item: StopsItem) {
        val stop = item.stop
        if (preferences.transliterate().get()) {
            binding.stopTitle.text = stop.name.transliterate()
            binding.stopDescription.text = stop.direction.transliterate()
        } else {
            binding.stopTitle.text = stop.name
            binding.stopDescription.text = stop.direction
        }
    }
}
