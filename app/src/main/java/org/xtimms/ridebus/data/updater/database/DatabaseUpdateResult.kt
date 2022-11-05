package org.xtimms.ridebus.data.updater.database

sealed class DatabaseUpdateResult {
    class NewUpdate(val update: GithubDatabase) : DatabaseUpdateResult()
    object NoNewUpdate : DatabaseUpdateResult()
}
