package org.xtimms.ridebus.util.system

import android.content.Context
import android.view.ViewPropertyAnimator

/** Scale the duration of this [ViewPropertyAnimator] by [Context.animatorDurationScale] */
fun ViewPropertyAnimator.applySystemAnimatorScale(context: Context): ViewPropertyAnimator = apply {
    this.duration = (this.duration * context.animatorDurationScale).toLong()
}
