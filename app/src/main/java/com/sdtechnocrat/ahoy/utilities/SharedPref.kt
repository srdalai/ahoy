package com.sdtechnocrat.ahoy.utilities

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

class SharedPref private constructor() {

    companion object {
        private val sharedPref = SharedPref()
        lateinit var sharedPreferences: SharedPreferences
        lateinit var editor : SharedPreferences.Editor

        fun getInstance(context: Context) : SharedPref {
            if (!::sharedPreferences.isInitialized) {
                synchronized(SharedPref::class.java) {
                    if (!::sharedPreferences.isInitialized) {
                        sharedPreferences = context.getSharedPreferences(context.packageName, Activity.MODE_PRIVATE)
                        editor = sharedPreferences.edit()
                    }
                }
            }
            return sharedPref
        }

        private const val NOTIFICATIONS_STATUS = "NOTIFICATIONS_STATUS"
        private const val WIFI_ONLY_STATUS = "WIFI_ONLY_STATUS"
        private const val SMART_DOWNLOAD_STATUS = "SMART_DOWNLOAD_STATUS"

        private const val USER_ID = "USER_ID"
        private const val USER_EMAIL = "USER_EMAIL"
        private const val USER_NAME = "USER_NAME"
        private const val USER_IMAGE = "USER_IMAGE"
    }

    fun setNotificationStatus(status: Boolean) {
        editor.putBoolean(NOTIFICATIONS_STATUS, status).apply()
    }

    fun getNotificationStatus() : Boolean {
        return sharedPreferences.getBoolean(NOTIFICATIONS_STATUS, true)
    }

    fun setWifiOnlyStatus(status: Boolean) {
        editor.putBoolean(WIFI_ONLY_STATUS, status).apply()
    }

    fun getWifiOnlyStatus() : Boolean {
        return sharedPreferences.getBoolean(WIFI_ONLY_STATUS, false)
    }

    fun setSmartDownloadStatus(status: Boolean) {
        editor.putBoolean(SMART_DOWNLOAD_STATUS, status).apply()
    }

    fun getSmartDownloadStatus() : Boolean {
        return sharedPreferences.getBoolean(SMART_DOWNLOAD_STATUS, false)
    }

    fun setUserId(userId: String) {
        editor.putString(USER_ID, userId).apply()
    }

    fun getUserId() : String? {
        return sharedPreferences.getString(USER_ID, "")
    }

    fun setUserEmail(email : String) {
        editor.putString(USER_EMAIL, email).apply()
    }

    fun getUserEmail() : String? {
        return sharedPreferences.getString(USER_EMAIL, "")
    }

    fun setUserName(name : String) {
        editor.putString(USER_NAME, name).apply()
    }

    fun getUserName() : String? {
        return sharedPreferences.getString(USER_NAME, "")
    }

    fun setUserImage(imageUrl : String) {
        editor.putString(USER_IMAGE, imageUrl).apply()
    }

    fun getUserImage() : String? {
        return sharedPreferences.getString(USER_IMAGE, "")
    }

    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
}