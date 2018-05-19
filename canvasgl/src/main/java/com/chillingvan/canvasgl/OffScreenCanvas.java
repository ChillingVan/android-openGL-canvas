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

import android.graphics.SurfaceTexture;
import android.support.annotation.Nullable;

import com.chillingvan.canvasgl.glcanvas.BasicTexture;
import com.chillingvan.canvasgl.glcanvas.RawTexture;
import com.chillingvan.canvasgl.glview.texture.GLMultiTexProducerView;
import com.chillingvan.canvasgl.glview.texture.GLSurfaceTextureProducerView;
import com.chillingvan.canvasgl.glview.texture.GLTexture;
import com.chillingvan.canvasgl.glview.texture.gles.EglContextWrapper;

import java.util.List;

/**
 * @deprecated use {@link MultiTexOffScreenCanvas} instead
 */
public abstract class OffScreenCanvas extends MultiTexOffScreenCanvas {

    private BasicTexture outsideSharedTexture;
    private SurfaceTexture outsideSharedSurfaceTexture;


    public OffScreenCanvas() {
    }

    public OffScreenCanvas(int width, int height) {
        super(width, height);
    }

    public OffScreenCanvas(Object surface) {
        super(surface);
    }

    public OffScreenCanvas(int width, int height, Object surface) {
        super(width, height, surface);
    }

    public OffScreenCanvas(int width, int height, EglContextWrapper sharedEglContext, Object surface) {
        super(width, height, sharedEglContext, surface);
    }

    public OffScreenCanvas(int width, int height, EglContextWrapper sharedEglContext) {
        super(width, height, sharedEglContext);
    }

    /**
     * If it is used, it must be called before start() called.
     */
    public void setOnSurfaceTextureSet(final GLSurfaceTextureProducerView.OnSurfaceTextureSet onSurfaceTextureSet) {
        setSurfaceTextureCreatedListener(new GLMultiTexProducerView.SurfaceTextureCreatedListener() {
            @Override
            public void onCreated(List<GLTexture> glTextureList) {
                GLTexture glTexture = glTextureList.get(0);
                onSurfaceTextureSet.onSet(glTexture.getSurfaceTexture(), glTexture.getRawTexture());
            }
        });
    }

    public void setSharedTexture(RawTexture outsideTexture, @Nullable SurfaceTexture outsideSurfaceTexture) {
        this.outsideSharedTexture = outsideTexture;
        this.outsideSharedSurfaceTexture = outsideSurfaceTexture;
        if (consumedTextures.isEmpty()) {
            consumedTextures.add(new GLTexture(outsideTexture, outsideSurfaceTexture));
        }
    }


    @Override
    protected final void onGLDraw(ICanvasGL canvas, List<GLTexture> producedTextures, List<GLTexture> consumedTextures) {
        GLTexture glTexture = producedTextures.get(0);
        if (!consumedTextures.isEmpty()) {
            GLTexture consumeTexture = consumedTextures.get(0);
            onGLDraw(canvas, glTexture.getSurfaceTexture(), glTexture.getRawTexture(), consumeTexture.getSurfaceTexture(), consumeTexture.getRawTexture());
        } else {
            onGLDraw(canvas, glTexture.getSurfaceTexture(), glTexture.getRawTexture(), null, null);
        }
    }

    protected abstract void onGLDraw(ICanvasGL canvas, SurfaceTexture producedSurfaceTexture, RawTexture producedRawTexture, @Nullable SurfaceTexture outsideSharedSurfaceTexture, @Nullable BasicTexture outsideSharedTexture);
}
