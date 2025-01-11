package com.palettex.palettewall.data

import android.annotation.SuppressLint
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object PaletteRemoteConfig {
    @SuppressLint("StaticFieldLeak")
    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

    // Remote config keys
    private const val KEY_DOWNLOAD_REWARD_MODE = "mode_DownloadReward"
    private const val KEY_DOWNLOAD_REWARD_UNIT_ID = "AdUnitID_DownloadReward"
    private const val KEY_BOTTOM_BANNER_MODE = "mode_BottomBanner"
    private const val KEY_BOTTOM_BANNER_UNIT_ID = "AdUnitID_BottomBanner"

    // StateFlows for reactive updates
    private val _downloadRewardMode = MutableStateFlow(0)
    val downloadRewardMode = _downloadRewardMode.asStateFlow()

    private val _downloadRewardUnitId = MutableStateFlow("")
    val downloadRewardUnitId = _downloadRewardUnitId.asStateFlow()

    private val _bottomBannerMode = MutableStateFlow(0)
    val bottomBannerMode = _bottomBannerMode.asStateFlow()

    private val _bottomBannerUnitId = MutableStateFlow("")
    val bottomBannerUnitId = _bottomBannerUnitId.asStateFlow()

    init {
        updateLocalValues()
    }

    fun updateLocalValues() {
        _downloadRewardMode.value = remoteConfig.getLong(KEY_DOWNLOAD_REWARD_MODE).toInt()
        _downloadRewardUnitId.value = remoteConfig.getString(KEY_DOWNLOAD_REWARD_UNIT_ID)
        _bottomBannerMode.value = remoteConfig.getLong(KEY_BOTTOM_BANNER_MODE).toInt()
        _bottomBannerUnitId.value = remoteConfig.getString(KEY_BOTTOM_BANNER_UNIT_ID)
    }

    fun fetchAndActivate(onComplete: (Boolean) -> Unit = {}) {
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                updateLocalValues()
            } else {
            }
            onComplete(task.isSuccessful)
        }
    }

    // Helper functions for BottomModal.kt
    fun shouldShowAds(): Boolean {
        return _downloadRewardMode.value != 0
    }

    fun isDebugMode(): Boolean {
        return _downloadRewardMode.value == 1
    }

    fun getAdUnitId(): String {
        return _downloadRewardUnitId.value
    }

    // Helper functions for banner ads
    fun shouldShowBannerAds(): Boolean {
        return _bottomBannerMode.value != 0
    }

    fun isBannerDebugMode(): Boolean {
        return _bottomBannerMode.value == 1
    }

    fun getBannerAdUnitId(): String {
        return _bottomBannerUnitId.value
    }
}