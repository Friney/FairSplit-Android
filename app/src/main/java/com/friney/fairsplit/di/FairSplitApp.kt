package com.friney.fairsplit.di

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FairSplitApp : Application() {
    companion object {
        lateinit var appContext: Context
            private set
    }
}