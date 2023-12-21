package edu.arizona.cast.hannaarnold.glucosemonitor

import android.app.Application

class GlucoseMonitorApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        GlucoseRepository.initialize(this)
    }
}