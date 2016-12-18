/*
 *
 *  *
 *  *  * Copyright (C) 2016 ChillingVan
 *  *  *
 *  *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  * you may not use this file except in compliance with the License.
 *  *  * You may obtain a copy of the License at
 *  *  *
 *  *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *  *
 *  *  * Unless required by applicable law or agreed to in writing, software
 *  *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  * See the License for the specific language governing permissions and
 *  *  * limitations under the License.
 *  *
 *
 */

package com.chillingvan.canvasgl;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.chillingvan.canvasgl.glcanvas.BasicTexture;
import com.chillingvan.canvasgl.glcanvas.RawTexture;
import com.chillingvan.canvasgl.glview.GLView;
import com.chillingvan.canvasgl.glview.texture.GLSurfaceTextureProducerView;
import com.chillingvan.canvasgl.glview.texture.gles.GLThread;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Chilling on 2016/11/7.
 */

public abstract class OffScreenCanvas implements GLSurfaceView.Renderer {

    protected final GLThread mGLThread;
    private int width;
    private int height;
    protected ICanvasGL mCanvas;
    public GL10 mGL;

    private BasicTexture outsideSharedTexture;
    private SurfaceTexture outsideSharedSurfaceTexture;


    private GLSurfaceTextureProducerView.OnSurfaceTextureSet onSurfaceTextureSet;
    private SurfaceTexture producedSurfaceTexture;
    private RawTexture producedRawTexture;
    private Handler handler;
    private boolean isStart;
    private int producedTextureTarget = GLES20.GL_TEXTURE_2D;

    public OffScreenCanvas() {
        this(0, 0, EGL10.EGL_NO_CONTEXT);
    }

    public OffScreenCanvas(int width, int height) {
        this(width, height, EGL10.EGL_NO_CONTEXT);
    }


    public OffScreenCanvas(Object surface) {
        this(0, 0, EGL10.EGL_NO_CONTEXT, surface);
    }

    public OffScreenCanvas(int width, int height, Object surface) {
        this(width, height, EGL10.EGL_NO_CONTEXT, surface);
    }


    public OffScreenCanvas(int width, int height, EGLContext sharedEglContext, Object surface) {
        this.width = width;
        this.height = height;
        mGLThread = new GLThread.Builder().setRenderMode(getRenderMode())
                .setSharedEglContext(sharedEglContext)
                .setSurface(surface)
                .setRenderer(this).createGLThread();
        handler = new Handler();
    }

    public OffScreenCanvas(int width, int height, EGLContext sharedEglContext) {
        this.width = width;
        this.height = height;
        mGLThread = new GLThread.Builder().setRenderMode(getRenderMode())
                .setSharedEglContext(sharedEglContext)
                .setEglWindowSurfaceFactory(new SurfaceFactory())
                .setRenderer(this).createGLThread();
        handler = new Handler();
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
     * If it is used, it must be called before start() called.
     */
    public void setOnSurfaceTextureSet(GLSurfaceTextureProducerView.OnSurfaceTextureSet onSurfaceTextureSet) {
        this.onSurfaceTextureSet = onSurfaceTextureSet;
    }

    public void setSharedTexture(BasicTexture outsideTexture, @Nullable SurfaceTexture outsideSurfaceTexture) {
        this.outsideSharedTexture = outsideTexture;
        this.outsideSharedSurfaceTexture = outsideSurfaceTexture;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        if (isStart) {
            mGLThread.onWindowResize(width, height);
        }
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
    }

    public void end() {
        if (mGLThread != null) {
            mGLThread.requestExitAndWait();
        }

        if (producedRawTexture != null) {
            producedRawTexture.recycle();
            producedRawTexture = null;
        }
        if (producedSurfaceTexture != null) {
            producedSurfaceTexture.release();
            producedSurfaceTexture = null;
        }
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
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mGL = gl;
        Loggers.d("BaseGLTextureView", "onSurfaceCreated: ");
        mCanvas = new CanvasGL();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Loggers.d("BaseGLTextureView", "onSurfaceChanged: ");
        mCanvas.setSize(width, height);
        if (producedRawTexture == null) {
            producedRawTexture = new RawTexture(width, height, false, producedTextureTarget);
            if (!producedRawTexture.isLoaded()) {
                producedRawTexture.prepare(mCanvas.getGlCanvas());
            }
            producedSurfaceTexture = new SurfaceTexture(producedRawTexture.getId());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (onSurfaceTextureSet != null) {
                        onSurfaceTextureSet.onSet(producedSurfaceTexture, producedRawTexture);
                    }
                }
            });
        } else {
            producedRawTexture.setSize(width, height);
        }

    }


    @Override
    public void onDrawFrame(GL10 gl) {
        producedSurfaceTexture.updateTexImage();
        onGLDraw(mCanvas, producedSurfaceTexture, producedRawTexture, outsideSharedSurfaceTexture, outsideSharedTexture);
    }


    protected int getRenderMode() {
        return GLThread.RENDERMODE_WHEN_DIRTY;
    }

    protected abstract void onGLDraw(ICanvasGL canvas, SurfaceTexture producedSurfaceTexture, RawTexture producedRawTexture, @Nullable SurfaceTexture outsideSharedSurfaceTexture, @Nullable BasicTexture outsideSharedTexture);

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

    public void getDrawingBitmap(final Rect rect, final GLView.GetDrawingCacheCallback getDrawingCacheCallback) {
        final Handler handler = new Handler();

        queueEvent(new Runnable() {
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
        requestRender();
    }
}
