package org.xtimms.ridebus.ui.more.sources

import androidx.preference.PreferenceScreen
import org.xtimms.ridebus.R
import org.xtimms.ridebus.ui.base.controller.openInBrowser
import org.xtimms.ridebus.ui.setting.SettingsController
import org.xtimms.ridebus.util.preference.onClick
import org.xtimms.ridebus.util.preference.preference
import org.xtimms.ridebus.util.preference.preferenceCategory
import org.xtimms.ridebus.util.preference.titleRes

class UsedSourcesController : SettingsController() {

    override fun setupPreferenceScreen(screen: PreferenceScreen) = screen.apply {
        titleRes = R.string.pref_sources_used

        preferenceCategory {
            titleRes = R.string.polotsk_novopolotsk

            preference {
                title = "Филиал «Автобусный парк №2 г.Полоцка»"
                summary = "http://ap2polotsk.of.by/"
                onClick {
                    openInBrowser("http://ap2polotsk.of.by/")
                }
            }

            preference {
                title = "Автотранспортное предприятие №6 г.Новополоцка"
                summary = "http://atp6voat.by/"
                onClick {
                    openInBrowser("http://atp6voat.by/")
                }
            }

            preference {
                title = "ВОКТУП «Оператор перевозок»"
                summary = "http://vitoperator.by/raspisanie/"
                onClick {
                    openInBrowser("http://vitoperator.by/raspisanie/")
                }
            }
        }
    }
}
