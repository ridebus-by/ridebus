package org.xtimms.ridebus.ui.main

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.Router
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import dev.chrisbanes.insetter.applyInsetter
import org.xtimms.ridebus.R
import org.xtimms.ridebus.databinding.MainActivityBinding
import org.xtimms.ridebus.ui.base.BaseViewBindingActivity
import org.xtimms.ridebus.ui.base.controller.withFadeTransaction
import org.xtimms.ridebus.util.system.InternalResourceHelper
import org.xtimms.ridebus.util.system.getResourceColor

class MainActivity : BaseViewBindingActivity<MainActivityBinding>() {

    private lateinit var router: Router

    lateinit var tabAnimator: ViewHeightAnimator
    private var bottomNavAnimator: ViewHeightAnimator? = null

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

        tabAnimator = ViewHeightAnimator(binding.tabs, 0L)
        bottomNavAnimator = ViewHeightAnimator(binding.bottomNav)

        // If bottom nav is hidden, make it visible again when the app bar is expanded
        binding.appbar.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                if (verticalOffset == 0) {
                    showBottomNav(true)
                }
            }
        )

        binding.bottomNav.setOnNavigationItemSelectedListener { item ->
            val id = item.itemId

            val currentRoot = router.backstack.firstOrNull()
            if (currentRoot?.tag()?.toIntOrNull() != id) {
                /*when (id) {
                    R.id.nav_routes -> setRoot(RoutesController(), id)
                    R.id.nav_stops -> setRoot(StopsController(), id)
                    R.id.nav_favorite -> setRoot(FavoriteController(), id)
                    R.id.nav_more -> setRoot(MoreController(), id)
                }*/
            }
            true
        }

        val container: ViewGroup = binding.controllerContainer
        router = Conductor.attachRouter(this, container, savedInstanceState)

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

    fun showBottomNav(visible: Boolean, collapse: Boolean = false) {
        binding.bottomNav.let {
            val layoutParams = it.layoutParams as CoordinatorLayout.LayoutParams
            val bottomViewNavigationBehavior =
                layoutParams.behavior as? HideBottomViewOnScrollBehavior
            if (visible) {
                if (collapse) {
                    bottomNavAnimator?.expand()
                }
                bottomViewNavigationBehavior?.slideUp(it)
            } else {
                if (collapse) {
                    bottomNavAnimator?.collapse()
                }

                bottomViewNavigationBehavior?.slideDown(it)
            }
        }
    }

    private fun setRoot(controller: Controller, id: Int) {
        router.setRoot(controller.withFadeTransaction().tag(id.toString()))
    }

    private fun syncActivityViewWithController(to: Controller?, from: Controller? = null) {
        supportActionBar?.setDisplayHomeAsUpEnabled(router.backstackSize != 1)

        // Always show appbar again when changing controllers
        binding.appbar.setExpanded(true)

    }

}