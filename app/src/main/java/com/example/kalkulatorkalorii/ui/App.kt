package com.example.kalkulatorkalorii.ui

import android.app.Application
import com.example.kalkulatorkalorii.model.db.Database

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Database.init(applicationContext)
    }
}