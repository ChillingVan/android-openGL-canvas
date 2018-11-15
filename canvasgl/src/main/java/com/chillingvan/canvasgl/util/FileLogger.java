package com.chillingvan.canvasgl.util;

import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * class description here
 *
 * @author hua.yin
 * @version 2.0.0
 * @since Aug 12, 2011
 */
public final class FileLogger {
    /**
     * Log中的常量是int值，不适合给外面使用，这里统一用这个枚举值进行设置
     */
    public enum LogLevel {
        VERBOSE(Log.VERBOSE),
        DEBUG(Log.DEBUG),
        INFO(Log.INFO),
        WARN(Log.WARN),
        ERROR(Log.ERROR),
        ASSERT(Log.ASSERT);

        private int mValue;

        LogLevel(int value) {
            mValue = value;
        }

        public int getValue() {
            return mValue;
        }
    }

    private static final SimpleDateFormat LOG_DATE_TIME_FORMAT = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");

    private static ExecutorService sLogExecutor = Executors.newSingleThreadExecutor();

    private static boolean sLogEnable = false;
    private static LogLevel sLogLevel = LogLevel.VERBOSE;
    private static LogFileManager sLogFileManager;

    private static Map<String, Integer> limitLogMap = new HashMap<>();

    /**
     * 设置Log开关
     *
     * @param enable 开关项(默认为开).
     */
    public static void setEnable(boolean enable) {
        sLogEnable = enable;
    }

    public static boolean isLogEnable() {
        return sLogEnable;
    }

    public static void setLogLevel(LogLevel level) {
        sLogLevel = level;
    }

    /**
     * 设置写入log的文件夹
     *
     * @param dirPath 文件夹地址
     */
    public static void init(String dirPath) {
        sLogEnable = true;
        File file = new File(dirPath);
        if (!file.exists() || !file.isDirectory()) {
            throw new InvalidParameterException();
        }
        sLogFileManager = new LogFileManager(dirPath);
    }

    /**
     * @param id the id for this log. Must be unique
     * @param cntTimesAfterLogOnce example: 1000 log once, then after 1000 call of this will log again
     */
    public static void limitLog(String id, String tag, String message, int cntTimesAfterLogOnce) {
        if (!limitLogMap.containsKey(id)) {
            limitLogMap.put(id, 0);
        } else {
            Integer currentCnt = limitLogMap.get(id);
            if (currentCnt < cntTimesAfterLogOnce) {
                limitLogMap.put(id, currentCnt+1);
                return;
            } else {
                limitLogMap.put(id, 0);
            }
        }
        d(tag, message);
    }

    /**
     * log for debug
     *
     * @param message log message
     * @param tag     tag
     * @see Log#d(String, String)
     */
    public static void d(String tag, String message) {
        if (sLogEnable) {
            String msg = message;
            Log.d(tag, msg);
            writeToFileIfNeeded(tag, msg, LogLevel.DEBUG);
        }
    }

    /**
     * log for debug
     *
     * @param message   log message
     * @param throwable throwable
     * @param tag       tag
     * @see Log#d(String, String, Throwable)
     */
    public static void d(String tag, String message, Throwable throwable) {
        if (sLogEnable) {
            String msg = message;
            Log.d(tag, msg, throwable);
            writeToFileIfNeeded(tag, msg + "\n" + Log.getStackTraceString(throwable), LogLevel.DEBUG);
        }
    }

    /**
     * log for debug
     *
     * @param tag    tag
     * @param format message format, such as "%d ..."
     * @param params message content params
     * @see Log#d(String, String)
     */
    public static void d(String tag, String format, Object... params) {
        if (sLogEnable) {
            String msg = String.format(format, params);
            Log.d(tag, msg);
            writeToFileIfNeeded(tag, msg, LogLevel.DEBUG);
        }
    }

    /**
     * log for warning
     *
     * @param message log message
     * @param tag     tag
     * @see Log#w(String, String)
     */
    public static void w(String tag, String message) {
        if (sLogEnable) {
            String msg = message;
            Log.w(tag, msg);
            writeToFileIfNeeded(tag, msg, LogLevel.WARN);
        }
    }

    /**
     * log for warning
     *
     * @param tag       tag
     * @param throwable throwable
     * @see Log#w(String, Throwable)
     */
    public static void w(String tag, Throwable throwable) {
        if (sLogEnable) {
            Log.w(tag, throwable);
            writeToFileIfNeeded(tag, Log.getStackTraceString(throwable), LogLevel.WARN);
        }
    }

    /**
     * log for warning
     *
     * @param message   log message
     * @param throwable throwable
     * @param tag       tag
     * @see Log#w(String, String, Throwable)
     */
    public static void w(String tag, String message, Throwable throwable) {
        if (sLogEnable) {
            String msg = message;
            Log.w(tag, msg, throwable);
            writeToFileIfNeeded(tag, msg + "\n" + Log.getStackTraceString(throwable), LogLevel.WARN);
        }
    }

    /**
     * log for warning
     *
     * @param tag    tag
     * @param format message format, such as "%d ..."
     * @param params message content params
     * @see Log#w(String, String)
     */
    public static void w(String tag, String format, Object... params) {
        if (sLogEnable) {
            String msg = String.format(format, params);
            Log.w(tag, msg);
            writeToFileIfNeeded(tag, msg, LogLevel.WARN);
        }
    }

    /**
     * log for error
     *
     * @param message message
     * @param tag     tag
     * @see Log#i(String, String)
     */
    public static void e(String tag, String message) {
        if (sLogEnable) {
            String msg = message;
            Log.e(tag, msg);
            writeToFileIfNeeded(tag, msg, LogLevel.ERROR);
        }
    }

    /**
     * log for error
     *
     * @param message   log message
     * @param throwable throwable
     * @param tag       tag
     * @see Log#i(String, String, Throwable)
     */
    public static void e(String tag, String message, Throwable throwable) {
        if (sLogEnable) {
            String msg = message;
            Log.e(tag, msg, throwable);
            writeToFileIfNeeded(tag, msg + "\n" + Log.getStackTraceString(throwable), LogLevel.ERROR);
        }
    }

    /**
     * log for error
     *
     * @param tag    tag
     * @param format message format, such as "%d ..."
     * @param params message content params
     * @see Log#e(String, String)
     */
    public static void e(String tag, String format, Object... params) {
        if (sLogEnable) {
            String msg = String.format(format, params);
            Log.e(tag, msg);
            writeToFileIfNeeded(tag, msg, LogLevel.ERROR);
        }
    }

    /**
     * log for information
     *
     * @param message message
     * @param tag     tag
     * @see Log#i(String, String)
     */
    public static void i(String tag, String message) {
        if (sLogEnable) {
            String msg = message;
            Log.i(tag, msg);
            writeToFileIfNeeded(tag, msg, LogLevel.INFO);
        }
    }

    /**
     * log for information
     *
     * @param message   log message
     * @param throwable throwable
     * @param tag       tag
     * @see Log#i(String, String, Throwable)
     */
    public static void i(String tag, String message, Throwable throwable) {
        if (sLogEnable) {
            String msg = message;
            Log.i(tag, msg, throwable);
            writeToFileIfNeeded(tag, msg + "\n" + Log.getStackTraceString(throwable), LogLevel.INFO);
        }
    }

    /**
     * log for information
     *
     * @param tag    tag
     * @param format message format, such as "%d ..."
     * @param params message content params
     * @see Log#i(String, String)
     */
    public static void i(String tag, String format, Object... params) {
        if (sLogEnable) {
            String msg = String.format(format, params);
            Log.i(tag, msg);
            writeToFileIfNeeded(tag, msg, LogLevel.INFO);
        }
    }

    /**
     * log for verbos
     *
     * @param message log message
     * @param tag     tag
     * @see Log#v(String, String)
     */
    public static void v(String tag, String message) {
        if (sLogEnable) {
            String msg = message;
            Log.v(tag, msg);
            writeToFileIfNeeded(tag, msg, LogLevel.VERBOSE);
        }
    }

    /**
     * log for verbose
     *
     * @param message   log message
     * @param throwable throwable
     * @param tag       tag
     * @see Log#v(String, String, Throwable)
     */
    public static void v(String tag, String message, Throwable throwable) {
        if (sLogEnable) {
            String msg = message;
            Log.v(tag, msg, throwable);
            writeToFileIfNeeded(tag, msg + "\n" + Log.getStackTraceString(throwable), LogLevel.VERBOSE);
        }
    }

    /**
     * log for verbose
     *
     * @param tag    tag
     * @param format message format, such as "%d ..."
     * @param params message content params
     * @see Log#v(String, String)
     */
    public static void v(String tag, String format, Object... params) {
        if (sLogEnable) {
            String msg = String.format(format, params);
            Log.v(tag, msg);
            writeToFileIfNeeded(tag, msg, LogLevel.VERBOSE);
        }
    }

    private static void writeToFileIfNeeded(final String tag, final String msg, LogLevel logLevel) {
        final StringBuilder strBuilder = new StringBuilder();
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        int methodStackCnt = 2;
        strBuilder
                .append(" ")
                .append(" tid=").append(Thread.currentThread().getId())
                .append(" ")
                .append(stackTrace[methodStackCnt].getFileName())
                .append("[").append(stackTrace[methodStackCnt].getLineNumber())
                .append("] ").append("; ")
                .append(stackTrace[methodStackCnt].getMethodName())
                .append(": ")
        ;

        if (logLevel.getValue() < sLogLevel.getValue() || sLogFileManager == null) {
            return;
        }
        sLogExecutor.execute(new Runnable() {
            @Override
            public void run() {
                appendLog(strBuilder.toString() + tag, msg);
            }
        });
    }

    private static void appendLog(String tag, String msg) {
        String logMsg = formatLog(tag, msg);
        flushLogToFile(logMsg);
    }

    private static void flushLogToFile(String logMsg) {
        sLogFileManager.writeLogToFile(logMsg);
    }

    private static String formatLog(String tag, String msg) {
        return String.format(Locale.CHINA, "%s pid=%d %s; %s\n", LOG_DATE_TIME_FORMAT.format(new Date()), android.os.Process.myPid(), tag, msg);
    }

    public static class LogFileManager {
        private static final int LOG_FILES_MAX_NUM = 5; //文件最多有5个
        private static final int LOG_FILE_MAX_SIZE = 1000 * 1000 * 20; //文件最大20MB

        private static final SimpleDateFormat LOG_FILE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        public static final String PREFIX = "Log";

        private File mCurrentLogFile;
        private String mLogFileDir;

        LogFileManager(String logFileDir) {
            mLogFileDir = logFileDir;
        }

        public void writeLogToFile(String logMessage) {
            if (mCurrentLogFile == null || mCurrentLogFile.length() >= LOG_FILE_MAX_SIZE) {
                mCurrentLogFile = getNewLogFile();
            }
            FileUtil.writeToFile(logMessage, mCurrentLogFile.getPath());
        }

        private File getNewLogFile() {
            File dir = new File(mLogFileDir);
            File[] files = dir.listFiles(fileFilter);
            if (files == null || files.length == 0) {
                // 创建新文件
                return createNewLogFileIfNeed();
            }
            List<File> sortedFiles = sortFiles(files);

            if (files.length > LOG_FILES_MAX_NUM) {
                // 删掉最老的文件
                FileUtil.delete(sortedFiles.get(0));
            }
            return createNewLogFileIfNeed();
        }

        private File createNewLogFileIfNeed() {
            return FileUtil.createFile(mLogFileDir + File.separator + PREFIX + LOG_FILE_DATE_FORMAT.format(new Date()) + ".txt");
        }

        private List<File> sortFiles(File[] files) {
            List<File> fileList = Arrays.asList(files);
            Collections.sort(fileList, new FileComparator());
            return fileList;
        }

        private class FileComparator implements Comparator<File> {
            public int compare(File file1, File file2) {
                if (file1.lastModified() < file2.lastModified()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }

        private FileFilter fileFilter = new FileFilter() {
            public boolean accept(File file) {
                String tmp = file.getName().toLowerCase();
                if (tmp.startsWith("log") && tmp.endsWith(".txt")) {
                    return true;
                }
                return false;
            }
        };
    }
}