package org.xtimms.ridebus.data.updater

sealed class AppUpdateResult {
    class NewUpdate(val release: GithubRelease) : AppUpdateResult()
    object NoNewUpdate : AppUpdateResult()
}
