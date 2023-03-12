package org.xtimms.ridebus.ui.main.welcome

import android.app.Dialog
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import org.xtimms.ridebus.databinding.WelcomeDialogControllerBinding
import org.xtimms.ridebus.ui.base.controller.DialogController
import org.xtimms.ridebus.util.view.setNavigationBarTransparentCompat
import org.xtimms.ridebus.widget.RideBusFullscreenDialog

class WelcomeDialogController :
    DialogController(),
    CityAdapter.OnItemClickListener {

    private var adapter: CityAdapter? = null
    private var cities: List<CityItem> = emptyList()

    private var binding: WelcomeDialogControllerBinding? = null

    override fun onCreateDialog(savedViewState: Bundle?): Dialog {
        binding = WelcomeDialogControllerBinding.inflate(activity!!.layoutInflater)

        adapter = CityAdapter(this)

        binding?.recyclerView?.layoutManager = LinearLayoutManager(view?.context)
        binding?.recyclerView?.adapter = adapter

        return RideBusFullscreenDialog(activity!!, binding!!.root).apply {
            val typedValue = TypedValue()
            val theme = context.theme
            theme.resolveAttribute(android.R.attr.colorBackground, typedValue, true)
        }
    }

    override fun onDestroyView(view: View) {
        adapter = null
        super.onDestroyView(view)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        dialog?.window?.let { window ->
            window.setNavigationBarTransparentCompat(window.context)
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }

    fun setList(cities: List<CityItem>) {
        this.cities = cities
        adapter?.updateDataSet(cities)
    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }
}
