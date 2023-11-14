package com.jhy.project.schoollibrary

import android.app.Application
import com.google.firebase.FirebaseApp
import com.jhy.project.schoollibrary.constanta.RemoteConfigHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SchoolLibraryApp : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        RemoteConfigHelper().initialize()
    }

}
