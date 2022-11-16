package com.chillingvan.canvasglsample

import android.app.Application
import com.chillingvan.canvasgl.util.FileLogger

/**
 * Created by Chilling on 2018/11/17.
 */
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //        Loggers.DEBUG = true;
        FileLogger.init(getExternalFilesDir(null)!!.absolutePath)
        FileLogger.d("MainActivity", "init -----------------")
    }
}