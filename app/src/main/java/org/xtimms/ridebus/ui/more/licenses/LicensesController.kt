package org.xtimms.ridebus.ui.more.licenses

import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.aboutlibraries.Libs
import dev.chrisbanes.insetter.applyInsetter
import eu.davidea.flexibleadapter.FlexibleAdapter
import org.xtimms.ridebus.R
import org.xtimms.ridebus.databinding.LicensesControllerBinding
import org.xtimms.ridebus.ui.base.controller.BaseController
import org.xtimms.ridebus.util.system.openInBrowser
import java.util.*

class LicensesController :
    BaseController<LicensesControllerBinding>(),
    FlexibleAdapter.OnItemClickListener {

    private var adapter: LicensesAdapter? = null

    override fun getTitle(): String? {
        return resources?.getString(R.string.licenses)
    }

    override fun createBinding(inflater: LayoutInflater) = LicensesControllerBinding.inflate(inflater)

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        binding.recycler.applyInsetter {
            type(navigationBars = true) {
                padding()
            }
        }

        binding.recycler.layoutManager = LinearLayoutManager(view.context)
        adapter = LicensesAdapter(this)
        binding.recycler.adapter = adapter

        val licenseItems = Libs(view.context).libraries
            .sortedBy { it.libraryName.toLowerCase(Locale.ROOT) }
            .map { LicensesItem(it) }
        adapter?.updateDataSet(licenseItems)
    }

    override fun onDestroyView(view: View) {
        adapter = null
        super.onDestroyView(view)
    }

    override fun onItemClick(view: View, position: Int): Boolean {
        val adapter = adapter ?: return false

        val item = adapter.getItem(position) ?: return false
        openLicenseWebsite(item)
        return true
    }

    private fun openLicenseWebsite(item: LicensesItem) {
        val website = item.library.libraryWebsite
        if (website.isNotEmpty()) {
            activity?.openInBrowser(website)
        }
    }
}