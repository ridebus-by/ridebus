package org.xtimms.ridebus.util

object SemanticVersioning {

    fun isNewVersion(versionTag: String, oldVersionTag: String): Boolean {
        val newVersion = versionTag.replace("[^\\d.]".toRegex(), "")
        val oldVersion = oldVersionTag.replace("[^\\d.]".toRegex(), "")

        val newSemVer = newVersion.split(".").map { it.toInt() }
        val oldSemVer = oldVersion.split(".").map { it.toInt() }

        oldSemVer.mapIndexed { index, i ->
            if (newSemVer[index] > i) {
                return true
            }
        }
        return false
    }
}
