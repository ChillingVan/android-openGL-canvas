package com.chillingvan.canvasgl;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.opengl.GLSurfaceView;
import android.os.Handler;

import com.chillingvan.canvasgl.glview.GLView;
import com.chillingvan.canvasgl.glview.texture.gles.GLThread;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Chilling on 2016/11/7.
 */

public abstract class OffScreenCanvas implements GLSurfaceView.Renderer{

    protected final GLThread mGLThread;
    private int width;
    private int height;
    protected ICanvasGL mCanvas;
    public GL10 mGL;

    public OffScreenCanvas() {
        this(0, 0);
    }

    public OffScreenCanvas(int width, int height) {
        this.width = width;
        this.height = height;
        mGLThread = new GLThread.Builder().setRenderMode(getRenderMode())
                .setEglWindowSurfaceFactory(new SurfaceFactory())
                .setRenderer(this).createGLThread();
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void start() {
        mGLThread.start();
        mGLThread.surfaceCreated();
        mGLThread.onWindowResize(width, height);
        mGLThread.requestRender();

    }

    public void end() {
        if (mGLThread != null) {
            mGLThread.requestExitAndWait();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (mGLThread != null) {
                // GLThread may still be running if this view was never
                // attached to a window.
                mGLThread.requestExitAndWait();
            }
        } finally {
            super.finalize();
        }
    }

    private class SurfaceFactory implements GLThread.EGLWindowSurfaceFactory {
        @Override
        public EGLSurface createWindowSurface(EGL10 egl, EGLDisplay display, EGLConfig config, Object nativeWindow) {
            int[] attribList = new int[] {
                    EGL10.EGL_WIDTH, width,
                    EGL10.EGL_HEIGHT, height,
                    EGL10.EGL_NONE
            };
            return egl.eglCreatePbufferSurface(display, config, attribList);
        }

        @Override
        public void destroySurface(EGL10 egl, EGLDisplay display, EGLSurface surface) {
            egl.eglDestroySurface(display, surface);
        }
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Loggers.d("BaseGLTextureView", "onSurfaceCreated: ");
        mCanvas = new CanvasGL();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Loggers.d("BaseGLTextureView", "onSurfaceChanged: ");
        mCanvas.setSize(width, height);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mGL = gl;
        onGLDraw(mCanvas);
    }


    protected int getRenderMode() {
        return GLThread.RENDERMODE_WHEN_DIRTY;
    }

    protected abstract void onGLDraw(ICanvasGL canvas);


    public void getDrawingBitmap(final Rect rect, final GLView.GetDrawingCacheCallback getDrawingCacheCallback) {
        final Handler handler = new Handler();

        mGLThread.queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mGL == null) {
                    return;
                }
                onDrawFrame(mGL);
                onDrawFrame(mGL);
                final Bitmap bitmapFromGLSurface = OpenGLUtil.createBitmapFromGLSurface(rect.left, rect.top, rect.right - rect.left, rect.bottom - rect.top, mGL, height);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        getDrawingCacheCallback.onFetch(bitmapFromGLSurface);
                    }
                });
            }
        });
        mGLThread.requestRender();
    }
}
