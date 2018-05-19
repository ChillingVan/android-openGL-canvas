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
import com.chillingvan.canvasgl.glcanvas.RawTexture;

import java.util.List;

/**
 * This class is used to accept eglContext and texture from outside. Then it can use them to draw.
 * @deprecated Use {@link GLMultiTexConsumerView} instead.
 */
public abstract class GLSharedContextView extends GLMultiTexConsumerView {


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

    public void setSharedTexture(RawTexture outsideTexture, SurfaceTexture outsideSurfaceTexture) {
        this.outsideSharedTexture = outsideTexture;
        this.outsideSharedSurfaceTexture = outsideSurfaceTexture;
        if (consumedTextures.isEmpty()) {
            consumedTextures.add(new GLTexture(outsideTexture, outsideSurfaceTexture));
        }
    }

    /**
     *
     * Will not call until @param surfaceTexture not null
     */
    protected abstract void onGLDraw(ICanvasGL canvas, @Nullable SurfaceTexture sharedSurfaceTexture, BasicTexture sharedTexture);

    @Override
    protected final void onGLDraw(ICanvasGL canvas, List<GLTexture> consumedTextures) {
        if (outsideSharedTexture != null && outsideSharedTexture.isRecycled()) {
            outsideSharedTexture = null;
            outsideSharedSurfaceTexture = null;
        }
        onGLDraw(canvas, outsideSharedSurfaceTexture, outsideSharedTexture);
    }


    @Override
    protected void surfaceDestroyed() {
        super.surfaceDestroyed();
        outsideSharedSurfaceTexture = null;
        outsideSharedTexture = null;
    }
}
