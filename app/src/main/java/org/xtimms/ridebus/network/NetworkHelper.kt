package org.xtimms.ridebus.network

import android.content.Context
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.xtimms.ridebus.data.preference.PreferencesHelper
import uy.kohesive.injekt.injectLazy
import java.io.File
import java.util.concurrent.TimeUnit

class NetworkHelper(context: Context) {

    private val preferences: PreferencesHelper by injectLazy()

    private val cacheDir = File(context.cacheDir, "network_cache")

    private val cacheSize = 5L * 1024 * 1024 // 5 MiB

    private val baseClientBuilder: OkHttpClient.Builder
        get() {
            val builder = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .callTimeout(2, TimeUnit.MINUTES)

            if (preferences.verboseLogging()) {
                val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.HEADERS
                }
                builder.addInterceptor(httpLoggingInterceptor)
            }

            return builder
        }

    val client by lazy { baseClientBuilder.cache(Cache(cacheDir, cacheSize)).build() }
}
