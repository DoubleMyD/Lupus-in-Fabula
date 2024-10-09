package com.example.lupusinfabulav1

import android.app.Application
import com.example.lupusinfabulav1.data.AppContainer
import com.example.lupusinfabulav1.data.DefaultAppContainer

class LupusInFabulaApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }

}