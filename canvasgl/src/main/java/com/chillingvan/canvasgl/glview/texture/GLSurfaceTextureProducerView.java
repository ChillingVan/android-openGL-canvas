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
import android.util.AttributeSet;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.glcanvas.RawTexture;
import com.chillingvan.canvasgl.glview.texture.gles.GLThread;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Chilling on 2016/11/3.
 */

public abstract class GLSurfaceTextureProducerView extends GLTextureView {
    private SurfaceTexture inputSurfaceTexture;
    private OnSurfaceTextureSet onSurfaceTextureSet;
    private RawTexture surfaceTextureHolderTexture;

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
    protected final void onGLDraw(ICanvasGL canvas) {
        onGLDraw(canvas, inputSurfaceTexture, surfaceTextureHolderTexture);
    }

    protected abstract void onGLDraw(ICanvasGL canvas, SurfaceTexture surfaceTexture, RawTexture surfaceTextureHolder);

    public void setOnSurfaceTextureSet(OnSurfaceTextureSet onSurfaceTextureSet) {
        this.onSurfaceTextureSet = onSurfaceTextureSet;
    }

    @Override
    protected final int getRenderMode() {
        return GLThread.RENDERMODE_WHEN_DIRTY;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        if (surfaceTextureHolderTexture == null) {
            surfaceTextureHolderTexture = new RawTexture(width, height, false, GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
            if (!surfaceTextureHolderTexture.isLoaded()) {
                surfaceTextureHolderTexture.prepare(mCanvas.getGlCanvas());
            }
            inputSurfaceTexture = new SurfaceTexture(surfaceTextureHolderTexture.getId());
            post(new Runnable() {
                @Override
                public void run() {
                    if (onSurfaceTextureSet != null) {
                        onSurfaceTextureSet.onSet(inputSurfaceTexture, surfaceTextureHolderTexture);
                    }
                }
            });
        } else {
            surfaceTextureHolderTexture.setSize(width, height);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        inputSurfaceTexture.updateTexImage();
        super.onDrawFrame(gl);
    }

    public interface OnSurfaceTextureSet {
        void onSet(SurfaceTexture surfaceTexture, RawTexture surfaceTextureRelatedTexture);
    }

    @Override
    protected void surfaceDestroyed() {
        super.surfaceDestroyed();
        if (surfaceTextureHolderTexture != null) {
            surfaceTextureHolderTexture.recycle();
            surfaceTextureHolderTexture = null;
        }
        if (inputSurfaceTexture != null) {
            inputSurfaceTexture.release();
            inputSurfaceTexture = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
