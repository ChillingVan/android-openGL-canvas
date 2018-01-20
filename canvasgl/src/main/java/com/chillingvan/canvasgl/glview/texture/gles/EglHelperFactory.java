package com.chillingvan.canvasgl.glview.texture.gles;

import android.os.Build;

/**
 * Created by Chilling on 2017/1/2.
 */

public class EglHelperFactory {

    public static IEglHelper create(GLThread.EGLConfigChooser configChooser, GLThread.EGLContextFactory eglContextFactory
            , GLThread.EGLWindowSurfaceFactory eglWindowSurfaceFactory) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            return new EglHelperAPI17(configChooser, eglContextFactory, eglWindowSurfaceFactory);
        } else {
            return new EglHelper(configChooser, eglContextFactory, eglWindowSurfaceFactory);
        }
    }

}
