package com.example.lupusinfabulav1

import android.app.Application
//import com.example.lupusinfabulav1.data.AppContainer
//import com.example.lupusinfabulav1.data.DefaultAppContainer
import com.example.lupusinfabulav1.data.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class LupusInFabulaApplication : Application() {

    //lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidContext(this@LupusInFabulaApplication)
            androidLogger()

            modules(appModule)
        }
        //container = DefaultAppContainer(this)
    }

}