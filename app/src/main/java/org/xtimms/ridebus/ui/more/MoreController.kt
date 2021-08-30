package org.xtimms.ridebus.ui.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceScreen
import org.xtimms.ridebus.R
import org.xtimms.ridebus.ui.base.controller.NoToolbarElevationController
import org.xtimms.ridebus.ui.base.controller.RootController
import org.xtimms.ridebus.ui.base.controller.withFadeTransaction
import org.xtimms.ridebus.ui.setting.SettingsController
import org.xtimms.ridebus.ui.setting.SettingsMainController
import org.xtimms.ridebus.ui.stub.StubController
import org.xtimms.ridebus.util.preference.*
import org.xtimms.ridebus.util.system.getResourceColor
import rx.Observable
import rx.Subscription
import rx.subscriptions.CompositeSubscription

class MoreController :
    SettingsController(),
    RootController,
    NoToolbarElevationController {

    private var untilDestroySubscriptions = CompositeSubscription()
        private set

    override fun setupPreferenceScreen(screen: PreferenceScreen) = screen.apply {
        titleRes = R.string.label_more

        val tintColor = context.getResourceColor(R.attr.colorAccent)

        add(MoreHeaderPreference(context))

        switchPreference {
            titleRes = R.string.automatic_schedule_updates
            summaryRes = R.string.automatic_schedule_updates_summary
            iconRes = R.drawable.ic_update
            iconTint = tintColor
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

    private fun <T> Observable<T>.subscribeUntilDestroy(onNext: (T) -> Unit): Subscription {
        return subscribe(onNext).also { untilDestroySubscriptions.add(it) }
    }
}
