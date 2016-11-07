/*
 *
 *  * Copyright (C) 2012 CyberAgent
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.chillingvan.canvasgl.glview.texture;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.glcanvas.BasicTexture;

import javax.microedition.khronos.egl.EGLContext;

/**
 * Created by Chilling on 2016/11/5.
 */

public abstract class GLSharedContextView extends BaseGLTextureView {


    private BasicTexture sharedTexture;
    private SurfaceTexture sharedSurfaceTexture;

    public GLSharedContextView(Context context) {
        super(context);
    }

    public GLSharedContextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GLSharedContextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setSharedEglContext(EGLContext sharedEglContext) {
        glThreadBuilder.setSharedEglContext(sharedEglContext);
        createGLThread();
    }

    public void setSharedTexture(BasicTexture outsideTexture, SurfaceTexture outsideSurfaceTexture) {
        this.sharedTexture = outsideTexture;
        this.sharedSurfaceTexture = outsideSurfaceTexture;
    }

    /**
     *
     * Will not call until @param surfaceTexture not null
     */
    protected abstract void onGLDraw(ICanvasGL canvas, SurfaceTexture sharedSurfaceTexture, BasicTexture sharedTexture);

    @Override
    protected void onGLDraw(ICanvasGL canvas) {
        if (sharedSurfaceTexture != null) {
            onGLDraw(canvas, sharedSurfaceTexture, sharedTexture);
        }
    }
}
