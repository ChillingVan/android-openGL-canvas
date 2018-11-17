package com.chillingvan.canvasglsample;

import android.app.Application;

import com.chillingvan.canvasgl.util.FileLogger;

/**
 * Created by Chilling on 2018/11/17.
 */
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FileLogger.init(getExternalFilesDir(null).getAbsolutePath());
        FileLogger.d("MainActivity", "init -----------------");
    }
}
