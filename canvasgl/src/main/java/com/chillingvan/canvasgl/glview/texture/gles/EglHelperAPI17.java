package com.chillingvan.canvasgl.glview.texture.gles;

import android.nfc.Tag;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.chillingvan.canvasgl.util.FileLogger;

import static com.chillingvan.canvasgl.glview.texture.gles.EglHelper.formatEglError;

/**
 * Created by Chilling on 2016/12/29.
 */

@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class EglHelperAPI17 implements IEglHelper {
    private static final String TAG = "EglHelperAPI17";

    private GLThread.EGLConfigChooser eglConfigChooser;
    private GLThread.EGLContextFactory eglContextFactory;
    private GLThread.EGLWindowSurfaceFactory eglWindowSurfaceFactory;
    private EGLDisplay mEglDisplay;
    private EGLConfig mEglConfig;
    private EGLContext mEglContext;
    private EGLSurface mEglSurface;

    public EglHelperAPI17(GLThread.EGLConfigChooser configChooser, GLThread.EGLContextFactory eglContextFactory
            , GLThread.EGLWindowSurfaceFactory eglWindowSurfaceFactory) {
        this.eglConfigChooser = configChooser;
        this.eglContextFactory = eglContextFactory;
        this.eglWindowSurfaceFactory = eglWindowSurfaceFactory;
    }

    @Override
    public EglContextWrapper start(EglContextWrapper eglContext) {
        FileLogger.w(TAG, "start() tid=" + Thread.currentThread().getId());
        /*
         * Get an EGL instance
         */

        /*
         * Get to the default display.
         */
        mEglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);

        if (mEglDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("eglGetDisplay failed");
        }

        /*
         * We can now initialize EGL for that display
         */
        int[] version = new int[2];
        if (!EGL14.eglInitialize(mEglDisplay, version, 0, version, 1)) {
            mEglDisplay = null;
            throw new RuntimeException("eglInitialize failed");
        }
        mEglConfig = eglConfigChooser.chooseConfig(mEglDisplay, false);

            /*
            * Create an EGL context. We want to do this as rarely as we can, because an
            * EGL context is a somewhat heavy object.
            */
        mEglContext = eglContextFactory.createContextAPI17(mEglDisplay, mEglConfig, eglContext.getEglContext());
        if (mEglContext == null || mEglContext == EGL14.EGL_NO_CONTEXT) {
            FileLogger.d(TAG, "mEglContext:" + mEglContext);
            mEglContext = null;
            throwEglException("; createContext");
        }
        FileLogger.w(TAG, "createContext " + mEglContext + " tid=" + Thread.currentThread().getId());

        mEglSurface = null;


        EglContextWrapper eglContextWrapper = new EglContextWrapper();
        eglContextWrapper.setEglContext(mEglContext);
        return eglContextWrapper;
    }

    @Override
    public boolean createSurface(Object surface) {
        FileLogger.w(TAG, "createSurface()  tid=" + Thread.currentThread().getId());

        if (mEglDisplay == null) {
            throw new RuntimeException("eglDisplay not initialized");
        }
        if (mEglConfig == null) {
            throw new RuntimeException("mEglConfig not initialized");
        }

        destroySurfaceImp();
        /*
         * Create an EGL surface we can render into.
         */
        mEglSurface = eglWindowSurfaceFactory.createWindowSurface(mEglDisplay, mEglConfig, surface);

        if (mEglSurface == null || mEglSurface == EGL14.EGL_NO_SURFACE) {
            int error = EGL14.eglGetError();
            if (error == EGL14.EGL_BAD_NATIVE_WINDOW) {
                FileLogger.e(TAG, "createWindowSurface returned EGL_BAD_NATIVE_WINDOW.");
            }
            FileLogger.e(TAG, "EGL_NO_SURFACE");
            return false;
        }

        /*
         * Before we can issue GL commands, we need to make sure
         * the context is current and bound to a surface.
         */
        if (!EGL14.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)) {
            /*
             * Could not make the context current, probably because the underlying
             * SurfaceView surface has been destroyed.
             */
            logEglErrorAsWarning(TAG, "eglMakeCurrent", EGL14.eglGetError());
            return false;
        }

        return true;
    }


    @Override
    public int swap() {
        if (!EGL14.eglSwapBuffers(mEglDisplay, mEglSurface)) {
            FileLogger.w(TAG, String.format("swap: start get error"));
            return EGL14.eglGetError();
        }
        return EGL14.EGL_SUCCESS;
    }

    @Override
    public void destroySurface() {
        FileLogger.w(TAG, "destroySurface()  tid=" + Thread.currentThread().getId());
        destroySurfaceImp();
    }

    private void destroySurfaceImp() {
        if (mEglSurface != null && mEglSurface != EGL14.EGL_NO_SURFACE) {
            EGL14.eglMakeCurrent(mEglDisplay, EGL14.EGL_NO_SURFACE,
                    EGL14.EGL_NO_SURFACE,
                    EGL14.EGL_NO_CONTEXT);
            eglWindowSurfaceFactory.destroySurface(mEglDisplay, mEglSurface);
            mEglSurface = null;
        }
    }

    @Override
    public void finish() {
        FileLogger.w(TAG, "finish() tid=" + Thread.currentThread().getId());
        if (mEglContext != null) {
            eglContextFactory.destroyContext(mEglDisplay, mEglContext);
            mEglContext = null;
        }
        if (mEglDisplay != null) {
            EGL14.eglTerminate(mEglDisplay);
            mEglDisplay = null;
        }
    }


    /**
     * Sends the presentation time stamp to EGL.
     *
     * @param nsecs Timestamp, in nanoseconds.
     */
    @Override
    public void setPresentationTime(long nsecs) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && nsecs != 0) {
            EGLExt.eglPresentationTimeANDROID(mEglDisplay, mEglSurface, nsecs);
        }
    }

    public static void logEglErrorAsWarning(String tag, String function, int error) {
        FileLogger.w(tag, formatEglError(function, error));
    }

    private void throwEglException(String function) {
        throwEglException(function, EGL14.eglGetError());
    }

    public static void throwEglException(String function, int error) {
        String message = formatEglError(function, error);
        FileLogger.e(TAG, "throwEglException tid=" + Thread.currentThread().getId() + " " + message);
        throw new RuntimeException(message);
    }
}
