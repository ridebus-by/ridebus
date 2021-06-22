package org.xtimms.ridebus.util.lang

import kotlinx.coroutines.*

fun launchNow(block: suspend CoroutineScope.() -> Unit): Job =
    GlobalScope.launch(Dispatchers.Main, CoroutineStart.UNDISPATCHED, block)