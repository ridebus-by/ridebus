package org.xtimms.ridebus.data.updater.database

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubDatabase(
    @SerialName("name") val version: String,
    @SerialName("body") val info: String,
    @SerialName("assets") private val assets: List<Assets>
) {

    fun getDownloadLink(): String {
        return assets[0].downloadLink
    }

    @Serializable
    data class Assets(@SerialName("download_url") val downloadLink: String)
}
