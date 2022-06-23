package com.example.coiffearch.preferencias

import android.app.Application

class CoiffearchApplication:Application() {

    companion object{
        lateinit var prefs:Prefs
    }

    override fun onCreate() {
        super.onCreate()
        prefs = Prefs(applicationContext)

    }
}