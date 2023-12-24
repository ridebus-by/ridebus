package org.xtimms.ridebus.ui.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceScreen
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.database.RideBusDatabase
import org.xtimms.ridebus.data.updater.database.DatabaseUpdateJob
import org.xtimms.ridebus.ui.base.controller.RootController
import org.xtimms.ridebus.ui.base.controller.withFadeTransaction
import org.xtimms.ridebus.ui.more.map.MapStopsDialog
import org.xtimms.ridebus.ui.setting.SettingsController
import org.xtimms.ridebus.ui.setting.SettingsMainController
import org.xtimms.ridebus.ui.stub.StubController
import org.xtimms.ridebus.util.preference.*
import org.xtimms.ridebus.util.system.getResourceColor
import rx.subscriptions.CompositeSubscription
import uy.kohesive.injekt.injectLazy

class MoreController :
    SettingsController(),
    RootController {

    private val database: RideBusDatabase by injectLazy()
    private var untilDestroySubscriptions = CompositeSubscription()

    override fun setupPreferenceScreen(screen: PreferenceScreen) = screen.apply {
        titleRes = R.string.label_more

        val tintColor = context.getResourceColor(androidx.appcompat.R.attr.colorAccent)

        add(MoreHeaderPreference(context))

        switchPreference {
            bindTo(preferences.autoUpdateSchedule())
            titleRes = R.string.automatic_schedule_updates
            summaryRes = R.string.automatic_schedule_updates_summary
            iconRes = R.drawable.ic_database_update
            iconTint = tintColor

            onChange { newValue ->
                val checked = newValue as Boolean
                DatabaseUpdateJob.setupTask(checkNotNull(activity), checked)
                true
            }
        }

        listPreference {
            bindTo(preferences.city())
            iconRes = R.drawable.ic_city
            iconTint = tintColor
            titleRes = R.string.city
            entriesName = database.cityDao().getCitiesNames().map { it }.toTypedArray()
            entryValues = database.cityDao().getCitiesIds().map { it.toString() }.toTypedArray()
            summary = "%s"
        }

        preferenceCategory {
            preference {
                titleRes = R.string.near_me
                iconRes = R.drawable.ic_near_me
                iconTint = tintColor
                onClick {
                    router.pushController(StubController().withFadeTransaction())
                }
            }
            preference {
                titleRes = R.string.map
                iconRes = R.drawable.ic_map
                iconTint = tintColor
                onClick {
                    MapStopsDialog().showDialog(router)
                }
            }
            preference {
                titleRes = R.string.backup
                iconRes = R.drawable.ic_backup
                iconTint = tintColor
                onClick {
                    router.pushController(StubController().withFadeTransaction())
                }
            }
        }

        preferenceCategory {
            preference {
                titleRes = R.string.label_settings
                iconRes = R.drawable.ic_settings
                iconTint = tintColor
                onClick { router.pushController(SettingsMainController().withFadeTransaction()) }
            }
            /*preference {
                iconRes = R.drawable.ic_help
                iconTint = tintColor
                titleRes = R.string.help
                onClick { router.pushController(StubController().withFadeTransaction()) }
            }*/
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View {
        if (untilDestroySubscriptions.isUnsubscribed) {
            untilDestroySubscriptions = CompositeSubscription()
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)
        untilDestroySubscriptions.unsubscribe()
    }
}
