package com.chillingvan.canvasgl;


import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.os.Build;
import android.os.Handler;

import com.chillingvan.canvasgl.glview.GLView;
import com.chillingvan.canvasgl.glview.texture.GLMultiTexProducerView;
import com.chillingvan.canvasgl.glview.texture.GLTexture;
import com.chillingvan.canvasgl.glview.texture.GLViewRenderer;
import com.chillingvan.canvasgl.glview.texture.gles.EglContextWrapper;
import com.chillingvan.canvasgl.glview.texture.gles.GLThread;
import com.chillingvan.canvasgl.util.Loggers;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
/**
 * OffScreenCanvas that support providing multiple textures to Camera or Media.
 * This can also consume textures from other GL zone( Should be in same GL context)
 */
public abstract class MultiTexOffScreenCanvas implements GLViewRenderer {
    private List<GLTexture> producedTextureList = new ArrayList<>();
    protected List<GLTexture> consumedTextures = new ArrayList<>();

    protected final GLThread mGLThread;
    protected int width;
    protected int height;
    protected ICanvasGL mCanvas;


    private GLMultiTexProducerView.SurfaceTextureCreatedListener surfaceTextureCreatedListener;

    private Handler handler;
    private boolean isStart;
    private int producedTextureTarget = GLES20.GL_TEXTURE_2D;
    private int backgroundColor = Color.TRANSPARENT;

    public MultiTexOffScreenCanvas() {
        this(0, 0, EglContextWrapper.EGL_NO_CONTEXT_WRAPPER);
    }

    public MultiTexOffScreenCanvas(int width, int height) {
        this(width, height, EglContextWrapper.EGL_NO_CONTEXT_WRAPPER);
    }


    public MultiTexOffScreenCanvas(Object surface) {
        this(0, 0, EglContextWrapper.EGL_NO_CONTEXT_WRAPPER, surface);
    }

    public MultiTexOffScreenCanvas(int width, int height, Object surface) {
        this(width, height, EglContextWrapper.EGL_NO_CONTEXT_WRAPPER, surface);
    }


    public MultiTexOffScreenCanvas(int width, int height, EglContextWrapper sharedEglContext, Object surface) {
        this.width = width;
        this.height = height;
        mGLThread = new GLThread.Builder().setRenderMode(getRenderMode())
                .setSharedEglContext(sharedEglContext)
                .setSurface(surface)
                .setRenderer(this).createGLThread();
        handler = new Handler();
    }

    public MultiTexOffScreenCanvas(int width, int height, EglContextWrapper sharedEglContext) {
        this.width = width;
        this.height = height;
        mGLThread = new GLThread.Builder().setRenderMode(getRenderMode())
                .setSharedEglContext(sharedEglContext)
                .setEglWindowSurfaceFactory(new SurfaceFactory())
                .setRenderer(this).createGLThread();
        handler = new Handler();
    }

    /**
     *
     * @param glTexture texture from outSide.
     */
    public void addConsumeGLTexture(GLTexture glTexture) {
        consumedTextures.add(glTexture);
    }

    /**
     * Create a new produced texture and upload it to the canvas.
     */
    public GLTexture addProducedGLTexture(int width, int height, boolean opaque, int target) {
        GLTexture glTexture = GLTexture.createRaw(width, height, opaque, target, mCanvas);
        producedTextureList.add(glTexture);
        return glTexture;
    }

    /**
     * If it is used, it must be called before start() called.
     * @param producedTextureTarget GLES20.GL_TEXTURE_2D or GLES11Ext.GL_TEXTURE_EXTERNAL_OES
     */
    public void setProducedTextureTarget(int producedTextureTarget) {
        this.producedTextureTarget = producedTextureTarget;
    }

    /**
     * If it is used, it must be called before start() called.
     */
    public void setOnCreateGLContextListener(GLThread.OnCreateGLContextListener onCreateGLContextListener) {
        mGLThread.setOnCreateGLContextListener(onCreateGLContextListener);
    }


    /**
     */
    public void setSurfaceTextureCreatedListener(GLMultiTexProducerView.SurfaceTextureCreatedListener surfaceTextureCreatedListener) {
        this.surfaceTextureCreatedListener = surfaceTextureCreatedListener;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        if (isStart) {
            mGLThread.onWindowResize(width, height);
        }
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void start() {
        mGLThread.start();
        mGLThread.surfaceCreated();
        mGLThread.onWindowResize(width, height);
        isStart = true;
    }

    public void onResume() {
        if(mGLThread != null) {
            mGLThread.onResume();
        }
    }

    public void onPause() {
        if(mGLThread != null) {
            mGLThread.onPause();
        }
        recycleProduceTexture();
    }

    public void end() {
        if (mGLThread != null) {
            mGLThread.requestExitAndWait();
        }
        recycleProduceTexture();
    }

    private void recycleProduceTexture() {
        for (GLTexture glTexture : producedTextureList) {
            if (!glTexture.getRawTexture().isRecycled()) {
                glTexture.getRawTexture().recycle();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!glTexture.getSurfaceTexture().isReleased()) {
                    glTexture.getSurfaceTexture().release();
                }
            } else {
                glTexture.getSurfaceTexture().release();
            }
        }
        producedTextureList.clear();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            end();
        } finally {
            super.finalize();
        }
    }

    private class SurfaceFactory implements GLThread.EGLWindowSurfaceFactory {
        @Override
        public EGLSurface createWindowSurface(EGL10 egl, EGLDisplay display, EGLConfig config, Object nativeWindow) {
            int[] attribList = new int[]{
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

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public android.opengl.EGLSurface createWindowSurface(android.opengl.EGLDisplay display, android.opengl.EGLConfig config, Object nativeWindow) {
            int[] attribList = new int[]{
                    EGL14.EGL_WIDTH, width,
                    EGL14.EGL_HEIGHT, height,
                    EGL14.EGL_NONE
            };
            return EGL14.eglCreatePbufferSurface(display, config, attribList, 0);
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public void destroySurface(android.opengl.EGLDisplay display, android.opengl.EGLSurface surface) {
            EGL14.eglDestroySurface(display, surface);
        }
    }

    /**
     *
     * @return The initial produced texture count
     */
    protected int getInitialTexCount() {
        return 1;
    }


    @Override
    public void onSurfaceCreated() {
        Loggers.d("OffScreenCanvas", "onSurfaceCreated: ");
        mCanvas = new CanvasGL();
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        Loggers.d("OffScreenCanvas", "onSurfaceChanged: ");
        mCanvas.setSize(width, height);

        if (producedTextureList.isEmpty()) {
            for (int i = 0; i < getInitialTexCount(); i++) {
                producedTextureList.add(GLTexture.createRaw(width, height, false, producedTextureTarget, mCanvas));
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (surfaceTextureCreatedListener != null) {
                        surfaceTextureCreatedListener.onCreated(producedTextureList);
                    }
                }
            });
        } else {
            for (GLTexture glTexture : producedTextureList) {
                glTexture.getRawTexture().setSize(width, height);
            }
        }
    }


    @Override
    public void onDrawFrame() {
        mCanvas.clearBuffer(backgroundColor);
        if (producedTextureTarget != GLES20.GL_TEXTURE_2D) {
            for (GLTexture glTexture : producedTextureList) {
                glTexture.getSurfaceTexture().updateTexImage();
            }
        }
        onGLDraw(mCanvas, producedTextureList, consumedTextures);
    }


    protected int getRenderMode() {
        return GLThread.RENDERMODE_WHEN_DIRTY;
    }

    protected abstract void onGLDraw(ICanvasGL canvas, List<GLTexture> producedTextures, List<GLTexture> consumedTextures);

    public void queueEvent(Runnable r) {
        if (mGLThread == null) {
            return;
        }
        mGLThread.queueEvent(r);
    }

    public void requestRender() {
        if (mGLThread != null) {
            mGLThread.requestRender();
        }
    }

    public void requestRenderAndWait() {
        if (mGLThread != null) {
            mGLThread.requestRenderAndWait();
        }
    }

    public void getDrawingBitmap(final Rect rect, final GLView.GetDrawingCacheCallback getDrawingCacheCallback) {
        final Handler handler = new Handler();

        queueEvent(new Runnable() {
            @Override
            public void run() {
                onDrawFrame();
                onDrawFrame();
                final Bitmap bitmapFromGLSurface = OpenGLUtil.createBitmapFromGLSurface(rect.left, rect.top, rect.right - rect.left, rect.bottom - rect.top, height);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        getDrawingCacheCallback.onFetch(bitmapFromGLSurface);
                    }
                });
            }
        });
        requestRender();
    }
}
