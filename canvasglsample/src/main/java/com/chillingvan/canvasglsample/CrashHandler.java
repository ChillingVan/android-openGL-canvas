package com.chillingvan.canvasglsample;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ling on 16-3-27.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static CrashHandler INSTANCE = new CrashHandler();
    private static Context mContext;
    public static String CRASH_PATH;

    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private CrashHandler() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public static CrashHandler init(Context applicationContext) {
        if (mContext == null) {
            mContext = applicationContext;
            CRASH_PATH = mContext.getExternalFilesDir(null) +  File.separator + "crash";
        }
        return INSTANCE;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        handleException(ex);
        mDefaultHandler.uncaughtException(thread, ex);
    }

    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        saveErrorInfo(ex);

        return true;
    }

    private void saveErrorInfo(Throwable ex) {
        File path = new File(CRASH_PATH);
        if (!path.exists()) {
            path.mkdirs();
        }

        File file = new File(CRASH_PATH + File.separator + convertYYMMDDHHmm(System.currentTimeMillis()) + ".txt");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            if (!file.exists()) {
                file.createNewFile();
            }
            Writer writer = new StringWriter();
            PrintWriter pw = new PrintWriter(writer);
            ex.printStackTrace(pw);
            pw.close();
            String error= writer.toString();
            fos.write(error.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String convertYYMMDDHHmm(long time) {
        Date date = new Date(time);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }
}
