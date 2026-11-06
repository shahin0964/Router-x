package com.router.x

import android.app.Application

class RouterXApp : Application() {

    companion object {
        lateinit var instance: RouterXApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
