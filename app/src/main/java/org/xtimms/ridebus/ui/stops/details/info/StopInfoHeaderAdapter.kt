package org.xtimms.ridebus.ui.stops.details.info

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class StopInfoHeaderAdapter : RecyclerView.Adapter<StopInfoHeaderAdapter.HeaderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopInfoHeaderAdapter.HeaderViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: StopInfoHeaderAdapter.HeaderViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    inner class HeaderViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            setStopInfo()
        }

        private fun setStopInfo() {
        }
    }
}
