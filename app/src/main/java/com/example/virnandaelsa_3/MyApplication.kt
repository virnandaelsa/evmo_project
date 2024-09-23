package com.example.virnandaelsa_3

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Enable persistence here
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}
