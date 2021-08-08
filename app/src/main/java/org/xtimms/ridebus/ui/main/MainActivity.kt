package org.xtimms.ridebus.ui.main

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.*
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.lifecycle.lifecycleScope
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.Router
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.navigation.NavigationBarView
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import org.xtimms.ridebus.R
import org.xtimms.ridebus.data.preference.asImmediateFlow
import org.xtimms.ridebus.databinding.MainActivityBinding
import org.xtimms.ridebus.ui.base.activity.BaseViewBindingActivity
import org.xtimms.ridebus.ui.base.controller.*
import org.xtimms.ridebus.ui.favorite.FavoriteController
import org.xtimms.ridebus.ui.more.MoreController
import org.xtimms.ridebus.ui.routes.RoutesController
import org.xtimms.ridebus.ui.setting.SettingsMainController
import org.xtimms.ridebus.ui.stops.StopsController
import org.xtimms.ridebus.util.lang.launchUI
import org.xtimms.ridebus.util.system.dpToPx
import org.xtimms.ridebus.util.system.toast
import org.xtimms.ridebus.util.view.setNavigationBarTransparentCompat
import org.xtimms.ridebus.widget.HideBottomNavigationOnScrollBehavior

class MainActivity : BaseViewBindingActivity<MainActivityBinding>() {

    private lateinit var router: Router

    private val startScreenId by lazy {
        when (preferences.startScreen()) {
            2 -> R.id.nav_stops
            3 -> R.id.nav_favorite
            else -> R.id.nav_routes
        }
    }

    lateinit var tabAnimator: ViewHeightAnimator
    private var bottomNavAnimator: ViewHeightAnimator? = null

    private var isConfirmingExit: Boolean = false
    private var isHandlingShortcut: Boolean = false

    private var fixedViewsToBottom = mutableMapOf<View, AppBarLayout.OnOffsetChangedListener>()

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
        binding.appbar.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                padding(left = true, top = true, right = true)
            }
        }
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
        setSplashScreenExitAnimation(splashScreen)

        tabAnimator = ViewHeightAnimator(binding.tabs)

        if (binding.bottomNav != null) {
            bottomNavAnimator = ViewHeightAnimator(binding.bottomNav!!)

            // Set behavior of bottom nav
            preferences.hideBottomBarOnScroll()
                .asImmediateFlow { setBottomNavBehaviorOnScroll() }
                .launchIn(lifecycleScope)
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
        }

        nav.setOnItemSelectedListener { item ->
            val id = item.itemId

            val currentRoot = router.backstack.firstOrNull()
            if (currentRoot?.tag()?.toIntOrNull() != id) {
                when (id) {
                    R.id.nav_routes -> setRoot(RoutesController(), id)
                    R.id.nav_stops -> setRoot(StopsController(), id)
                    R.id.nav_favorite -> setRoot(FavoriteController(), id)
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
            setSelectedNavItem(startScreenId)
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
                    syncActivityViewWithController(to, from)
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

        syncActivityViewWithController(router.backstack.lastOrNull()?.controller)

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
                    window.setNavigationBarTransparentCompat(this@MainActivity)
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

    private fun syncActivityViewWithController(to: Controller?, from: Controller? = null) {
        supportActionBar?.setDisplayHomeAsUpEnabled(router.backstackSize != 1)

        // Always show appbar again when changing controllers
        binding.appbar.setExpanded(true)

        if ((from == null || from is RootController) && to !is RootController) {
            showNav(visible = false, expand = true)
        }
        if (to is RootController) {
            // Always show bottom nav again when returning to a RootController
            showNav(visible = true, expand = from !is RootController)
        }

        if (from is TabbedController) {
            from.cleanupTabs(binding.tabs)
        }
        if (to is TabbedController) {
            tabAnimator.expand()
            to.configureTabs(binding.tabs)
        } else {
            tabAnimator.collapse()
            binding.tabs.setupWithViewPager(null)
        }

        when (to) {
            is NoToolbarElevationController -> {
                binding.appbar.disableElevation()
            }
            is ToolbarLiftOnScrollController -> {
                binding.appbar.enableElevation(true)
            }
            else -> {
                binding.appbar.enableElevation(false)
            }
        }
    }

    private fun showNav(visible: Boolean, expand: Boolean = false) {
        showBottomNav(visible, expand)
        showSideNav(visible)
    }

    // Also used from some controllers to swap bottom nav with action toolbar
    fun showBottomNav(visible: Boolean, expand: Boolean = false) {
        if (visible) {
            binding.bottomNav?.translationY = 0F
            if (expand) {
                bottomNavAnimator?.expand()
            }
        } else {
            bottomNavAnimator?.collapse()
        }
    }

    private fun showSideNav(visible: Boolean) {
        binding.sideNav?.let {
            it.isVisible = visible
        }
    }

    /**
     * Used to manually offset a view within the activity's child views that might be cut off due to
     * the collapsing AppBarLayout.
     */
    fun fixViewToBottom(view: View) {
        val listener = AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val maxAbsOffset = appBarLayout.measuredHeight - binding.tabs.measuredHeight
            view.translationY = -maxAbsOffset - verticalOffset.toFloat() + appBarLayout.paddingTop
        }
        binding.appbar.addOnOffsetChangedListener(listener)
        fixedViewsToBottom[view] = listener
    }

    fun clearFixViewToBottom(view: View) {
        val listener = fixedViewsToBottom.remove(view)
        binding.appbar.removeOnOffsetChangedListener(listener)
    }

    private fun setBottomNavBehaviorOnScroll() {
        showNav(visible = true)

        binding.bottomNav?.updateLayoutParams<CoordinatorLayout.LayoutParams> {
            behavior = when {
                preferences.hideBottomBarOnScroll().get() -> HideBottomNavigationOnScrollBehavior()
                else -> null
            }
        }
        binding.bottomNav?.translationY = 0F
    }

    private val nav: NavigationBarView
        get() = binding.bottomNav ?: binding.sideNav!!

    companion object {
        // Splash screen
        private const val SPLASH_MIN_DURATION = 500 // ms
        private const val SPLASH_MAX_DURATION = 5000 // ms
        private const val SPLASH_EXIT_ANIM_DURATION = 400L // ms
    }
}