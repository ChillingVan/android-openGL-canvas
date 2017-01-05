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
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.glcanvas.BasicTexture;
import com.chillingvan.canvasgl.glcanvas.RawTexture;
import com.chillingvan.canvasgl.glview.texture.gles.EglContextWrapper;
import com.chillingvan.canvasgl.glview.texture.gles.GLThread;

/**
 * Created by Chilling on 2016/11/3.
 */

public abstract class GLSurfaceTextureProducerView extends GLSharedContextView {
    private SurfaceTexture producedSurfaceTexture;
    private OnSurfaceTextureSet onSurfaceTextureSet;
    private RawTexture producedRawTexture;
    private int producedTextureTarget = GLES11Ext.GL_TEXTURE_EXTERNAL_OES;

    public GLSurfaceTextureProducerView(Context context) {
        super(context);
    }

    public GLSurfaceTextureProducerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GLSurfaceTextureProducerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected final void onGLDraw(ICanvasGL canvas, @Nullable SurfaceTexture sharedSurfaceTexture, BasicTexture sharedTexture) {
        onGLDraw(canvas, producedSurfaceTexture, producedRawTexture, sharedSurfaceTexture, sharedTexture);
    }

    protected abstract void onGLDraw(ICanvasGL canvas, SurfaceTexture producedSurfaceTexture, RawTexture producedRawTexture, @Nullable SurfaceTexture sharedSurfaceTexture, @Nullable BasicTexture sharedTexture);

    public void setOnSurfaceTextureSet(OnSurfaceTextureSet onSurfaceTextureSet) {
        this.onSurfaceTextureSet = onSurfaceTextureSet;
    }

    @Override
    protected final int getRenderMode() {
        return GLThread.RENDERMODE_WHEN_DIRTY;
    }


    /**
     * If it is used, it must be called before start() called.
     * @param producedTextureTarget GLES20.GL_TEXTURE_2D or GLES11Ext.GL_TEXTURE_EXTERNAL_OES
     */
    public void setProducedTextureTarget(int producedTextureTarget) {
        this.producedTextureTarget = producedTextureTarget;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        super.onSurfaceTextureAvailable(surface, width, height);
        if (mGLThread == null) {
            setSharedEglContext(EglContextWrapper.EGL_NO_CONTEXT_WRAPPER);
        }
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        super.onSurfaceChanged(width, height);
        if (producedRawTexture == null) {
            producedRawTexture = new RawTexture(width, height, false, producedTextureTarget);
            if (!producedRawTexture.isLoaded()) {
                producedRawTexture.prepare(mCanvas.getGlCanvas());
            }
            producedSurfaceTexture = new SurfaceTexture(producedRawTexture.getId());
            post(new Runnable() {
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
    public void onDrawFrame() {
        if (producedTextureTarget != GLES20.GL_TEXTURE_2D) {
            producedSurfaceTexture.updateTexImage();
        }
        super.onDrawFrame();
    }

    public interface OnSurfaceTextureSet {
        void onSet(SurfaceTexture surfaceTexture, RawTexture surfaceTextureRelatedTexture);
    }

    @Override
    protected void surfaceDestroyed() {
        super.surfaceDestroyed();
        if (producedRawTexture != null) {
            producedRawTexture.recycle();
            producedRawTexture = null;
        }
        if (producedSurfaceTexture != null) {
            producedSurfaceTexture.release();
            producedSurfaceTexture = null;
        }
    }

}
