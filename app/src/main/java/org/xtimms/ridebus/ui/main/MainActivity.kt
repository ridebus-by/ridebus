package org.xtimms.ridebus.ui.main

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.*
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceDialogController
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.Router
import com.google.android.material.navigation.NavigationBarView
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.notification.NotificationReceiver
import org.xtimms.ridebus.databinding.MainActivityBinding
import org.xtimms.ridebus.ui.base.activity.BaseActivity
import org.xtimms.ridebus.ui.base.controller.*
import org.xtimms.ridebus.ui.favourite.FavouritesController
import org.xtimms.ridebus.ui.more.MoreController
import org.xtimms.ridebus.ui.routes.RoutesTabbedController
import org.xtimms.ridebus.ui.routes.details.RouteDetailsController
import org.xtimms.ridebus.ui.schedule.ScheduleTabbedController
import org.xtimms.ridebus.ui.setting.SettingsMainController
import org.xtimms.ridebus.ui.stops.StopsController
import org.xtimms.ridebus.util.lang.launchUI
import org.xtimms.ridebus.util.preference.asImmediateFlow
import org.xtimms.ridebus.util.system.dpToPx
import org.xtimms.ridebus.util.system.isTablet
import org.xtimms.ridebus.util.system.toast
import org.xtimms.ridebus.util.view.setNavigationBarTransparentCompat

class MainActivity : BaseActivity() {

    lateinit var binding: MainActivityBinding

    private lateinit var router: Router

    private val startScreenId by lazy {
        when (preferences.startScreen()) {
            2 -> R.id.nav_stops
            3 -> R.id.nav_favorite
            else -> R.id.nav_routes
        }
    }

    private var isConfirmingExit: Boolean = false
    private var isHandlingShortcut: Boolean = false

    /**
     * App bar lift state for backstack
     */
    private val backstackLiftState = mutableMapOf<String, Boolean>()

    // To be checked by splash screen. If true then splash screen will be removed.
    var ready = false

    override fun onCreate(savedInstanceState: Bundle?) {
        // Prevent splash screen showing up on configuration changes
        val splashScreen = if (savedInstanceState == null) installSplashScreen() else null

        super.onCreate(savedInstanceState)

        binding = MainActivityBinding.inflate(layoutInflater)

        // Do not let the launcher create a new activity http://stackoverflow.com/questions/16283079
        if (!isTaskRoot) {
            finish()
            return
        }

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // Draw edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding.bottomNav?.applyInsetter {
            type(navigationBars = true) {
                padding()
            }
        }

        val startTime = System.currentTimeMillis()
        splashScreen?.setKeepVisibleCondition {
            val elapsed = System.currentTimeMillis() - startTime
            elapsed <= SPLASH_MIN_DURATION || (!ready && elapsed <= SPLASH_MAX_DURATION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSplashScreenExitAnimation(splashScreen)
        }

        if (binding.sideNav != null) {
            preferences.sideNavIconAlignment()
                .asImmediateFlow {
                    binding.sideNav?.menuGravity = when (it) {
                        1 -> Gravity.CENTER
                        2 -> Gravity.BOTTOM
                        else -> Gravity.TOP
                    }
                }
                .launchIn(lifecycleScope)
            preferences.sideNavLabels()
                .asImmediateFlow {
                    binding.sideNav?.labelVisibilityMode = when (it) {
                        1 -> NavigationBarView.LABEL_VISIBILITY_SELECTED
                        2 -> NavigationBarView.LABEL_VISIBILITY_LABELED
                        3 -> NavigationBarView.LABEL_VISIBILITY_UNLABELED
                        else -> NavigationBarView.LABEL_VISIBILITY_AUTO
                    }
                }
                .launchIn(lifecycleScope)
        }

        nav.setOnItemSelectedListener { item ->
            val id = item.itemId

            val currentRoot = router.backstack.firstOrNull()
            if (currentRoot?.tag()?.toIntOrNull() != id) {
                when (id) {
                    R.id.nav_routes -> setRoot(RoutesTabbedController(), id)
                    R.id.nav_stops -> setRoot(StopsController(), id)
                    R.id.nav_favorite -> setRoot(FavouritesController(), id)
                    R.id.nav_more -> setRoot(MoreController(), id)
                }
            } else if (!isHandlingShortcut) {
                when (id) {
                    R.id.nav_more -> {
                        if (router.backstackSize == 1) {
                            router.pushController(SettingsMainController().withFadeTransaction())
                        }
                    }
                }
            }
            true
        }

        val container: ViewGroup = binding.controllerContainer
        router = Conductor.attachRouter(this, container, savedInstanceState)
        if (!router.hasRootController()) {
            // Set start screen
            if (!handleIntentAction(intent)) {
                setSelectedNavItem(startScreenId)
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        router.addChangeListener(
            object : ControllerChangeHandler.ControllerChangeListener {
                override fun onChangeStarted(
                    to: Controller?,
                    from: Controller?,
                    isPush: Boolean,
                    container: ViewGroup,
                    handler: ControllerChangeHandler
                ) {
                    syncActivityViewWithController(to, from, isPush)
                }

                override fun onChangeCompleted(
                    to: Controller?,
                    from: Controller?,
                    isPush: Boolean,
                    container: ViewGroup,
                    handler: ControllerChangeHandler
                ) {
                }
            }
        )

        preferences.bottomBarLabels()
            .asImmediateFlow { setNavLabelVisibility() }
            .launchIn(lifecycleScope)
    }

    /**
     * Sets custom splash screen exit animation on devices prior to Android 12.
     *
     * When custom animation is used, status and navigation bar color will be set to transparent and will be restored
     * after the animation is finished.
     */
    private fun setSplashScreenExitAnimation(splashScreen: SplashScreen?) {
        val setNavbarScrim = {
            // Make sure navigation bar is on bottom before we modify it
            ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
                if (insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom > 0) {
                    val elevation = binding.bottomNav?.elevation ?: 0F
                    window.setNavigationBarTransparentCompat(this@MainActivity, elevation)
                }
                insets
            }
            ViewCompat.requestApplyInsets(binding.root)
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S && splashScreen != null) {
            val oldStatusColor = window.statusBarColor
            val oldNavigationColor = window.navigationBarColor
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT

            splashScreen.setOnExitAnimationListener { splashProvider ->
                // For some reason the SplashScreen applies (incorrect) Y translation to the iconView
                splashProvider.iconView.translationY = 0F

                val activityAnim = ValueAnimator.ofFloat(1F, 0F).apply {
                    interpolator = LinearOutSlowInInterpolator()
                    duration = SPLASH_EXIT_ANIM_DURATION
                    addUpdateListener { va ->
                        val value = va.animatedValue as Float
                        binding.root.translationY = value * 16.dpToPx
                    }
                }

                val splashAnim = ValueAnimator.ofFloat(1F, 0F).apply {
                    interpolator = FastOutSlowInInterpolator()
                    duration = SPLASH_EXIT_ANIM_DURATION
                    addUpdateListener { va ->
                        val value = va.animatedValue as Float
                        splashProvider.view.alpha = value
                    }
                    doOnEnd {
                        splashProvider.remove()
                        window.statusBarColor = oldStatusColor
                        window.navigationBarColor = oldNavigationColor
                        setNavbarScrim()
                    }
                }

                activityAnim.start()
                splashAnim.start()
            }
        } else {
            setNavbarScrim()
        }
    }

    override fun onNewIntent(intent: Intent) {
        if (!handleIntentAction(intent)) {
            super.onNewIntent(intent)
        }
    }

    private fun handleIntentAction(intent: Intent): Boolean {
        val notificationId = intent.getIntExtra("notificationId", -1)
        if (notificationId > -1) {
            NotificationReceiver.dismissNotification(applicationContext, notificationId, intent.getIntExtra("groupId", 0))
        }

        isHandlingShortcut = true

        when (intent.action) {
            SHORTCUT_ROUTE -> setSelectedNavItem(R.id.nav_routes)
            SHORTCUT_STOP -> setSelectedNavItem(R.id.nav_stops)
            SHORTCUT_FAVORITE -> setSelectedNavItem(R.id.nav_favorite)
            else -> {
                isHandlingShortcut = false
                return false
            }
        }

        ready = true
        isHandlingShortcut = false
        return true
    }

    override fun onResume() {
        super.onResume()
        syncActivityViewWithController()
    }

    @Suppress("UNNECESSARY_SAFE_CALL")
    override fun onDestroy() {
        super.onDestroy()

        // Binding sometimes isn't actually instantiated yet somehow
        nav?.setOnItemSelectedListener(null)
        binding?.toolbar.setNavigationOnClickListener(null)
    }

    override fun onBackPressed() {
        val backstackSize = router.backstackSize
        if (backstackSize == 1 && router.getControllerWithTag("$startScreenId") == null) {
            // Return to start screen
            setSelectedNavItem(startScreenId)
        } else if (shouldHandleExitConfirmation()) {
            // Exit confirmation (resets after 2 seconds)
            lifecycleScope.launchUI { resetExitConfirmation() }
        } else if (backstackSize == 1 || !router.handleBack()) {
            // Regular back
            super.onBackPressed()
        }
    }

    private suspend fun resetExitConfirmation() {
        isConfirmingExit = true
        val toast = toast(R.string.confirm_exit, Toast.LENGTH_LONG)
        delay(2000)
        toast.cancel()
        isConfirmingExit = false
    }

    private fun shouldHandleExitConfirmation(): Boolean {
        return router.backstackSize == 1 &&
            router.getControllerWithTag("$startScreenId") != null &&
            preferences.confirmExit() &&
            !isConfirmingExit
    }

    fun setSelectedNavItem(itemId: Int) {
        if (!isFinishing) {
            nav.selectedItemId = itemId
        }
    }

    private fun setRoot(controller: Controller, id: Int) {
        router.setRoot(controller.withFadeTransaction().tag(id.toString()))
    }

    private fun syncActivityViewWithController(
        to: Controller? = router.backstack.lastOrNull()?.controller,
        from: Controller? = null,
        isPush: Boolean = true
    ) {
        if (from is DialogController || to is DialogController) {
            return
        }
        if (from is PreferenceDialogController || to is PreferenceDialogController) {
            return
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(router.backstackSize != 1)

        // Always show appbar again when changing controllers
        binding.appbar.setExpanded(true)

        if ((from == null || from is RootController) && to !is RootController) {
            showNav(false)
        }
        if (to is RootController) {
            // Always show bottom nav again when returning to a RootController
            showNav(true)
        }

        if (from is TabbedController) {
            from.cleanupTabs(binding.tabs)
        }
        if (to is TabbedController) {
            to.configureTabs(binding.tabs)
        } else {
            binding.tabs.setupWithViewPager(null)
        }
        binding.tabs.isVisible = to is TabbedController && to !is ScheduleTabbedController

        if (!isTablet()) {
            // Save lift state
            if (isPush) {
                if (router.backstackSize > 1) {
                    // Save lift state
                    from?.let {
                        backstackLiftState[it.instanceId] = binding.appbar.isLifted
                    }
                } else {
                    backstackLiftState.clear()
                }
                binding.appbar.isLifted = false
            } else {
                to?.let {
                    binding.appbar.isLifted = backstackLiftState.getOrElse(it.instanceId) { false }
                }
                from?.let {
                    backstackLiftState.remove(it.instanceId)
                }
            }

            binding.root.isLiftAppBarOnScroll = to !is NoAppBarElevationController

            binding.appbar.isTransparentWhenNotLifted = to is RouteDetailsController
            binding.controllerContainer.overlapHeader = to is RouteDetailsController
        }
    }

    private fun showNav(visible: Boolean) {
        showBottomNav(visible)
        showSideNav(visible)
    }

    // Also used from some controllers to swap bottom nav with action toolbar
    fun showBottomNav(visible: Boolean) {
        if (visible) {
            binding.bottomNav?.slideUp()
        } else {
            binding.bottomNav?.slideDown()
        }
    }

    private fun showSideNav(visible: Boolean) {
        binding.sideNav?.isVisible = visible
    }

    private val nav: NavigationBarView
        get() = binding.bottomNav ?: binding.sideNav!!

    private fun setNavLabelVisibility() {
        if (preferences.bottomBarLabels().get()) {
            binding.bottomNav?.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED
        } else {
            binding.bottomNav?.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_SELECTED
        }
    }

    companion object {
        // Splash screen
        private const val SPLASH_MIN_DURATION = 500 // ms
        private const val SPLASH_MAX_DURATION = 5000 // ms
        private const val SPLASH_EXIT_ANIM_DURATION = 400L // ms

        // Shortcut actions
        const val SHORTCUT_ROUTE = "org.xtimms.ridebus.SHOW_ROUTE"
        const val SHORTCUT_STOP = "org.xtimms.ridebus.SHOW_STOP"
        const val SHORTCUT_FAVORITE = "org.xtimms.ridebus.SHOW_FAVORITE"

        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    }
}
