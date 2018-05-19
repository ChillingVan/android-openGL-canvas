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

package com.chillingvan.canvasgl.glview.texture;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.glview.texture.gles.EglContextWrapper;
import com.chillingvan.canvasgl.glview.texture.gles.GLThread;
import com.chillingvan.canvasgl.util.Loggers;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to generate multiple textures or consume textures from others.
 * This will not create {@link GLThread} automatically. You need to call {@link #setSharedEglContext(EglContextWrapper)} to trigger it.
 * Support providing multiple textures to Camera or Media. <br>
 * This can also consume textures from other GL zone( Should be in same GL context) <br>
 */
public abstract class GLMultiTexProducerView extends GLMultiTexConsumerView {
    private static final String TAG = "GLMultiTexProducerView";
    private int producedTextureTarget = GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
    private List<GLTexture> producedTextureList = new ArrayList<>();
    private SurfaceTextureCreatedListener surfaceTextureCreatedListener;

    public GLMultiTexProducerView(Context context) {
        super(context);
    }

    public GLMultiTexProducerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GLMultiTexProducerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected final void onGLDraw(ICanvasGL canvas, List<GLTexture> consumedTextures) {
        onGLDraw(canvas, producedTextureList, consumedTextures);
    }



    @Override
    protected int getRenderMode() {
        return GLThread.RENDERMODE_WHEN_DIRTY;
    }

    /**
     *
     * @return The initial produced texture count
     */
    protected int getInitialTexCount() {
        return 1;
    }

    /**
     * If it is used, it must be called before {@link GLThread#start()} called.
     * @param producedTextureTarget GLES20.GL_TEXTURE_2D or GLES11Ext.GL_TEXTURE_EXTERNAL_OES
     */
    public void setProducedTextureTarget(int producedTextureTarget) {
        this.producedTextureTarget = producedTextureTarget;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        super.onSurfaceTextureAvailable(surface, width, height);
    }


    /**
     * Create a new produced texture and upload it to the canvas.
     */
    public GLTexture addProducedGLTexture(int width, int height, boolean opaque, int target) {
        GLTexture glTexture = GLTexture.createRaw(width, height, opaque, target, mCanvas);
        producedTextureList.add(glTexture);
        return glTexture;
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        super.onSurfaceChanged(width, height);
        Loggers.d(TAG, "onSurfaceChanged: ");
        if (producedTextureList.isEmpty()) {
            for (int i = 0; i < getInitialTexCount(); i++) {
                producedTextureList.add(GLTexture.createRaw(width, height, false, producedTextureTarget, mCanvas));
            }
            post(new Runnable() {
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
        if (producedTextureTarget != GLES20.GL_TEXTURE_2D) {
            for (GLTexture glTexture : producedTextureList) {
                glTexture.getSurfaceTexture().updateTexImage();
            }
        }
        super.onDrawFrame();
    }

    @Override
    public void onPause() {
        super.onPause();
        recycleProduceTexture();
        if (mGLThread == null) {
            Log.w(TAG, "!!!!!! You may not call setShareEglContext !!!");
        }
    }

    @Override
    protected void surfaceDestroyed() {
        super.surfaceDestroyed();
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

    public void setSurfaceTextureCreatedListener(SurfaceTextureCreatedListener surfaceTextureCreatedListener) {
        this.surfaceTextureCreatedListener = surfaceTextureCreatedListener;
    }

    /**
     * Listen when the produced textures created.
     */
    public interface SurfaceTextureCreatedListener {
        void onCreated(List<GLTexture> producedTextureList);
    }

    /**
     * If {@link #setSharedEglContext(EglContextWrapper)} is not called, this will not be triggered.
     * @param canvas
     * @param producedTextures
     * @param consumedTextures
     */
    protected abstract void onGLDraw(ICanvasGL canvas, List<GLTexture> producedTextures, List<GLTexture> consumedTextures);
}
