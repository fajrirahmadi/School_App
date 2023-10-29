package com.jhy.project.schoollibrary.constanta

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteConfigHelper @Inject constructor() {

    companion object {
        const val currentSchoolYear = "current_school_year"
        const val kepalaPustaka = "kepala_pustaka"
    }

    private val config = Firebase.remoteConfig

    var kepalaPustaka: String = config.getString(RemoteConfigHelper.kepalaPustaka)

    fun initialize() {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 60
        }
        config.setConfigSettingsAsync(configSettings)
        config.setDefaultsAsync(
            mapOf(
                currentSchoolYear to "2023/2024",
                kepalaPustaka to "Irni, S.Pd."
            )
        )
        fetchAndActivate()
    }

    fun fetchAndActivate() {
        config.fetchAndActivate()
    }

    fun currentSchoolYear(): String {
        return config.getString(currentSchoolYear)
    }
}