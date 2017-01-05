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
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.glcanvas.BasicTexture;
import com.chillingvan.canvasgl.glview.texture.gles.EglContextWrapper;

/**
 * Created by Chilling on 2016/11/5.
 */

public abstract class GLSharedContextView extends BaseGLCanvasTextureView {


    protected BasicTexture outsideSharedTexture;
    protected SurfaceTexture outsideSharedSurfaceTexture;

    public GLSharedContextView(Context context) {
        super(context);
    }

    public GLSharedContextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GLSharedContextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setSharedEglContext(EglContextWrapper sharedEglContext) {
        glThreadBuilder.setSharedEglContext(sharedEglContext);
        createGLThread();
    }

    public void setSharedTexture(BasicTexture outsideTexture, SurfaceTexture outsideSurfaceTexture) {
        this.outsideSharedTexture = outsideTexture;
        this.outsideSharedSurfaceTexture = outsideSurfaceTexture;
    }

    /**
     *
     * Will not call until @param surfaceTexture not null
     */
    protected abstract void onGLDraw(ICanvasGL canvas, @Nullable SurfaceTexture sharedSurfaceTexture, BasicTexture sharedTexture);

    @Override
    protected final void onGLDraw(ICanvasGL canvas) {
        onGLDraw(canvas, outsideSharedSurfaceTexture, outsideSharedTexture);
    }

    @Override
    protected void surfaceDestroyed() {
        super.surfaceDestroyed();
        outsideSharedSurfaceTexture = null;
        outsideSharedTexture = null;
    }
}
