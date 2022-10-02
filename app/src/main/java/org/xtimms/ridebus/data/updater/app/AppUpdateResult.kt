package org.xtimms.ridebus.data.updater.app

sealed class AppUpdateResult {
    class NewUpdate(val release: GithubRelease) : AppUpdateResult()
    object NoNewUpdate : AppUpdateResult()
}
