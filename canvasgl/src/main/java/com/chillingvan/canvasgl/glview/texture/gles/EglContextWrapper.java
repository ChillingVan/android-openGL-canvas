package com.chillingvan.canvasgl.glview.texture.gles;

import android.annotation.TargetApi;
import android.opengl.EGL14;
import android.os.Build;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;

/**
 * Created by Chilling on 2016/12/29.
 */

public class EglContextWrapper {

    EGLContext eglContextOld;
    android.opengl.EGLContext eglContext;

    public EGLContext getEglContextOld() {
        return eglContextOld;
    }

    public void setEglContextOld(EGLContext eglContextOld) {
        this.eglContextOld = eglContextOld;
    }

    public android.opengl.EGLContext getEglContext() {
        return eglContext;
    }

    public void setEglContext(android.opengl.EGLContext eglContext) {
        this.eglContext = eglContext;
    }

    public static EGLContext getNoContextOld() {
        return EGL10.EGL_NO_CONTEXT;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static android.opengl.EGLContext getNoContext() {
        return EGL14.EGL_NO_CONTEXT;
    }
}
