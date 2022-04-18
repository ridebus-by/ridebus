package org.xtimms.ridebus.ui.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceScreen
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.preference.PreferenceValues
import org.xtimms.ridebus.ui.base.controller.RootController
import org.xtimms.ridebus.ui.base.controller.withFadeTransaction
import org.xtimms.ridebus.ui.nearby.NearbyController
import org.xtimms.ridebus.ui.setting.SettingsController
import org.xtimms.ridebus.ui.setting.SettingsMainController
import org.xtimms.ridebus.ui.stub.StubController
import org.xtimms.ridebus.util.preference.*
import org.xtimms.ridebus.util.system.getResourceColor
import rx.subscriptions.CompositeSubscription

class MoreController :
    SettingsController(),
    RootController {

    private var untilDestroySubscriptions = CompositeSubscription()

    override fun setupPreferenceScreen(screen: PreferenceScreen) = screen.apply {
        titleRes = R.string.label_more

        val tintColor = context.getResourceColor(R.attr.colorAccent)

        switchPreference {
            bindTo(preferences.autoUpdateSchedule())
            titleRes = R.string.automatic_schedule_updates
            summaryRes = R.string.automatic_schedule_updates_summary
            iconRes = R.drawable.ic_update
            iconTint = tintColor

            preferences.autoUpdateSchedule().asFlow()
                .onEach { isChecked = it }
                .launchIn(viewScope)
        }

        listPreference {
            bindTo(preferences.city())
            iconRes = R.drawable.ic_city
            iconTint = tintColor
            titleRes = R.string.city
            entriesRes = arrayOf(
                R.string.city_polotsk,
                R.string.city_novopolotsk,
                R.string.city_ushachi
            )
            entryValues = arrayOf(
                PreferenceValues.City.POLOTSK.name,
                PreferenceValues.City.NOVOPOLOTSK.name,
                PreferenceValues.City.USHACHI.name
            )
            summary = "%s"
        }

        preferenceCategory {
            preference {
                titleRes = R.string.near_me
                iconRes = R.drawable.ic_near_me
                iconTint = tintColor
                onClick {
                    router.pushController(NearbyController().withFadeTransaction())
                }
            }
            preference {
                titleRes = R.string.map
                iconRes = R.drawable.ic_map
                iconTint = tintColor
                onClick {
                    router.pushController(StubController().withFadeTransaction())
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
            preference {
                iconRes = R.drawable.ic_info
                iconTint = tintColor
                titleRes = R.string.pref_category_about
                onClick { router.pushController(AboutController().withFadeTransaction()) }
            }
            preference {
                iconRes = R.drawable.ic_help
                iconTint = tintColor
                titleRes = R.string.help
                onClick { router.pushController(StubController().withFadeTransaction()) }
            }
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
