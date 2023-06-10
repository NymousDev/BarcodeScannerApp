package com.nymousdevapps.barcodescannerapp

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Resources
import com.nymousdevapps.barcodescannerapp.data.room.db.BarcodeScannerDB
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BarcodeScannerAppApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        resource = resources
        barcodeScannerDB = BarcodeScannerDB.create(this)
        sharedPref = getSharedPreferences("preferences", MODE_PRIVATE)
    }

    companion object {
        var resource: Resources? = null
        var barcodeScannerDB: BarcodeScannerDB? = null
        var sharedPref : SharedPreferences? = null
    }
}