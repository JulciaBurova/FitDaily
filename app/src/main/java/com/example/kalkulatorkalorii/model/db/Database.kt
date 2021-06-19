package com.example.kalkulatorkalorii.model.db

import android.content.Context
import androidx.room.Room


object Database {
    var database: AppDatabase? = null

    fun init(context: Context) {
        database = Room.databaseBuilder(context, AppDatabase::class.java, "database.db")
            .allowMainThreadQueries()
            .build()
    }
}