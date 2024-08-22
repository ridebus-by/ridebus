package org.xtimms.ridebus.data.updater

import android.os.Build
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubRelease(
    @SerialName("tag_name") val version: String,
    @SerialName("body") val info: String,
    @SerialName("html_url") val releaseLink: String,
    @SerialName("assets") private val assets: List<Assets>
) {

    fun getDownloadLink(): String {
        val apkVariant = when (Build.SUPPORTED_ABIS[0]) {
            "arm64-v8a" -> "-arm64-v8a"
            "armeabi-v7a" -> "-armeabi-v7a"
            "x86", "x86_64" -> "-x86"
            else -> ""
        }

        return assets.find { it.downloadLink.contains("ridebus$apkVariant-") }?.downloadLink
            ?: assets[0].downloadLink
    }

    @Serializable
    data class Assets(@SerialName("browser_download_url") val downloadLink: String)
}
