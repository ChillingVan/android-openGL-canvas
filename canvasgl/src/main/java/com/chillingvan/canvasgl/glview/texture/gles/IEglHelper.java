package com.chillingvan.canvasgl.glview.texture.gles;

/**
 * Created by Chilling on 2016/12/29.
 */
public interface IEglHelper {
    EGLContextWrapper start(EGLContextWrapper eglContext);

    boolean createSurface(Object surface);

    int swap();

    void destroySurface();

    void finish();

    void setPresentationTime(long nsecs);
}
