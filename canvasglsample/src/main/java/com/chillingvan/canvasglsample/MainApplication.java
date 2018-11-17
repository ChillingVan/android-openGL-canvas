package com.chillingvan.canvasglsample;

import android.app.Application;

import com.chillingvan.canvasgl.util.FileLogger;
import com.chillingvan.canvasgl.util.Loggers;

/**
 * Created by Chilling on 2018/11/17.
 */
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Loggers.DEBUG = true;
        FileLogger.init(getExternalFilesDir(null).getAbsolutePath());
        FileLogger.d("MainActivity", "init -----------------");
    }
}
