package org.xtimms.ridebus.ui.routes.details.stop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.xtimms.ridebus.R
import org.xtimms.ridebus.databinding.RouteDetailStopsHeaderBinding
import org.xtimms.ridebus.ui.routes.details.RouteDetailsController

class RouteStopsHeaderAdapter(
    private val controller: RouteDetailsController
) :
    RecyclerView.Adapter<RouteStopsHeaderAdapter.HeaderViewHolder>() {

    private var numChapters: Int? = null

    private lateinit var binding: RouteDetailStopsHeaderBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        binding = RouteDetailStopsHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HeaderViewHolder(binding.root)
    }

    override fun getItemCount(): Int = 1

    override fun getItemId(position: Int): Long = hashCode().toLong()

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.bind()
    }

    fun setNumStops(numChapters: Int) {
        this.numChapters = numChapters
        notifyItemChanged(0, this)
    }

    inner class HeaderViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            binding.stopsLabel.text = if (numChapters == null) {
                view.context.getString(R.string.information_no_stops_on_route)
            } else {
                view.context.resources.getQuantityString(R.plurals.route_num_stops, numChapters!!, numChapters)
            }
        }
    }
}
