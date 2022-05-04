package org.xtimms.ridebus.util.system

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.provider.Settings

fun Context.getAvailableMemory(): Long {
    val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
    val outInfo = ActivityManager.MemoryInfo()
    am.getMemoryInfo(outInfo)
    return outInfo.availMem
}

fun Context.getTotalMemory(): Long {
    val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
    val outInfo = ActivityManager.MemoryInfo()
    am.getMemoryInfo(outInfo)
    return outInfo.totalMem
}

fun Context.getAndroidId(): String {
    return Settings.System.getString(contentResolver, Settings.Secure.ANDROID_ID)
}
