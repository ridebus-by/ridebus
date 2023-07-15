package org.xtimms.ridebus.ui.routes

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.bluelinelabs.conductor.Router
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.preference.PreferencesHelper
import org.xtimms.ridebus.widget.ExtendedNavigationView
import org.xtimms.ridebus.widget.sheet.TabbedBottomSheetDialog
import uy.kohesive.injekt.injectLazy

class RoutesSettingsSheet(
    router: Router,
    onGroupClickListener: (ExtendedNavigationView.Group) -> Unit
) : TabbedBottomSheetDialog(checkNotNull(router.activity)) {

    private val display: Display

    init {
        display = Display(checkNotNull(router.activity))
        display.onGroupClicked = onGroupClickListener
    }

    override fun getTabViews(): List<View> = listOf(
        display
    )

    override fun getTabTitles(): List<Int> = listOf(
        R.string.action_display
    )

    inner class Display @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
        Settings(context, attrs) {

        private val tabsGroup: TabsGroup

        init {
            tabsGroup = TabsGroup()
            setGroups(listOf(tabsGroup))
        }

        inner class TabsGroup : Group {
            private val showTabs = Item.CheckboxGroup(R.string.action_display_show_tabs, this)
            private val showNumberOfItems = Item.CheckboxGroup(R.string.action_display_show_number_of_items, this)

            override val header = Item.Header(R.string.tabs_header)
            override val items = listOf(showTabs, showNumberOfItems)
            override val footer = null

            override fun initModels() {
                showTabs.checked = preferences.categoryTabs().get()
                showNumberOfItems.checked = preferences.categoryNumberOfItems().get()
            }

            override fun onItemClicked(item: Item) {
                item as Item.CheckboxGroup
                item.checked = !item.checked
                when (item) {
                    showTabs -> preferences.categoryTabs().set(item.checked)
                    showNumberOfItems -> preferences.categoryNumberOfItems().set(item.checked)
                    else -> {}
                }
                adapter.notifyItemChanged(item)
            }
        }
    }

    open inner class Settings(context: Context, attrs: AttributeSet?) :
        ExtendedNavigationView(context, attrs) {

        val preferences: PreferencesHelper by injectLazy()
        lateinit var adapter: Adapter

        /**
         * Click listener to notify the parent fragment when an item from a group is clicked.
         */
        var onGroupClicked: (Group) -> Unit = {}

        fun setGroups(groups: List<Group>) {
            adapter = Adapter(groups.map { it.createItems() }.flatten())
            recycler.adapter = adapter

            groups.forEach { it.initModels() }
            addView(recycler)
        }

        /**
         * Adapter of the recycler view.
         */
        inner class Adapter(items: List<Item>) : ExtendedNavigationView.Adapter(items) {

            override fun onItemClicked(item: Item) {
                if (item is GroupedItem) {
                    item.group.onItemClicked(item)
                    onGroupClicked(item.group)
                }
            }
        }
    }
}
