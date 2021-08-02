package org.xtimms.ridebus.ui.main

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.*
import androidx.lifecycle.lifecycleScope
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.Router
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
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
import org.xtimms.ridebus.util.system.InternalResourceHelper
import org.xtimms.ridebus.util.system.getResourceColor
import org.xtimms.ridebus.util.system.toast
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

    override fun onCreate(savedInstanceState: Bundle?) {
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
        binding.bottomNav.applyInsetter {
            type(navigationBars = true) {
                padding()
            }
        }

        // Make sure navigation bar is on bottom before we modify it
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            if (insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom > 0) {
                window.navigationBarColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                    !InternalResourceHelper.getBoolean(this, "config_navBarNeedsScrim", true)
                ) {
                    Color.TRANSPARENT
                } else {
                    // Set navbar scrim 70% of navigationBarColor
                    getResourceColor(android.R.attr.navigationBarColor, 0.7F)
                }
            }
            insets
        }

        tabAnimator = ViewHeightAnimator(binding.tabs)
        bottomNavAnimator = ViewHeightAnimator(binding.bottomNav)

        // Set behavior of bottom nav
        preferences.hideBottomBar()
            .asImmediateFlow { setBottomNavBehaviorOnScroll() }
            .launchIn(lifecycleScope)

        binding.bottomNav.setOnNavigationItemSelectedListener { item ->
            val id = item.itemId

            val currentRoot = router.backstack.firstOrNull()
            if (currentRoot?.tag()?.toIntOrNull() != id) {
                when (id) {
                    R.id.nav_routes -> setRoot(RoutesController(), id)
                    R.id.nav_stops -> setRoot(StopsController(), id)
                    R.id.nav_favorite -> setRoot(FavoriteController(), id)
                    R.id.nav_more -> setRoot(MoreController(), id)
                }
            } else {
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

        syncActivityViewWithController(router.backstack.lastOrNull()?.controller())

    }

    override fun onDestroy() {
        super.onDestroy()

        // Binding sometimes isn't actually instantiated yet somehow
        binding?.bottomNav.setOnNavigationItemSelectedListener(null)
        binding?.toolbar.setNavigationOnClickListener(null)
    }

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

    fun setSelectedNavItem(itemId: Int) {
        if (!isFinishing) {
            binding.bottomNav.selectedItemId = itemId
        }
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

    private fun setRoot(controller: Controller, id: Int) {
        router.setRoot(controller.withFadeTransaction().tag(id.toString()))
    }

    private fun syncActivityViewWithController(to: Controller?, from: Controller? = null) {

        supportActionBar?.setDisplayHomeAsUpEnabled(router.backstackSize != 1)

        // Always show appbar again when changing controllers
        binding.appbar.setExpanded(true)

        if ((from == null || from is RootController) && to !is RootController) {
            showBottomNav(visible = false, expand = true)
        }
        if (to is RootController) {
            // Always show bottom nav again when returning to a RootController
            showBottomNav(visible = true, expand = from !is RootController)
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

    /**
     * Used to manually offset a view within the activity's child views that might be cut off due to
     * the collapsing AppBarLayout.
     */
    fun fixViewToBottom(view: View) {
        val listener = AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val maxAbsOffset = appBarLayout.measuredHeight - binding.tabs.measuredHeight
            view.translationY = -maxAbsOffset - verticalOffset.toFloat() + appBarLayout.marginTop
        }
        binding.appbar.addOnOffsetChangedListener(listener)
        fixedViewsToBottom[view] = listener
    }

    fun clearFixViewToBottom(view: View) {
        val listener = fixedViewsToBottom.remove(view)
        binding.appbar.removeOnOffsetChangedListener(listener)
    }

    private fun setBottomNavBehaviorOnScroll() {
        showBottomNav(visible = true)

        binding.bottomNav.updateLayoutParams<CoordinatorLayout.LayoutParams> {
            behavior = when {
                preferences.hideBottomBar().get() -> HideBottomNavigationOnScrollBehavior()
                else -> null
            }
        }
        binding.bottomNav?.translationY = 0F
    }

}