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
import com.chillingvan.canvasgl.glview.texture.gles.EglContextWrapper;

import java.util.List;

/**
 * <p>
 * This will generate a texture which is in the eglContext of the CanvasGL. And the texture can be used outside.
 * The {@link #setSharedEglContext(EglContextWrapper)} will be called automatically when {@link #onSurfaceTextureAvailable(SurfaceTexture, int, int)}
 * For example, the generated texture can be used in camera preview texture or {@link GLMultiTexConsumerView}.
 * </p>
 * From pause to run: onResume --> createSurface --> onSurfaceChanged
 */
public abstract class GLSurfaceTextureProducerView extends GLMultiTexProducerView {

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
    protected final int getInitialTexCount() {
        return 1;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        super.onSurfaceTextureAvailable(surface, width, height);
        if (mGLThread == null) {
            setSharedEglContext(EglContextWrapper.EGL_NO_CONTEXT_WRAPPER);
        }
    }

    public void setOnSurfaceTextureSet(final GLSurfaceTextureProducerView.OnSurfaceTextureSet onSurfaceTextureSet) {
        setSurfaceTextureCreatedListener(new SurfaceTextureCreatedListener() {
            @Override
            public void onCreated(List<GLTexture> glTextureList) {
                GLTexture glTexture = glTextureList.get(0);
                onSurfaceTextureSet.onSet(glTexture.getSurfaceTexture(), glTexture.getRawTexture());
            }
        });
    }

    public interface OnSurfaceTextureSet {
        void onSet(SurfaceTexture surfaceTexture, RawTexture surfaceTextureRelatedTexture);
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

    protected abstract void onGLDraw(ICanvasGL canvas, SurfaceTexture producedSurfaceTexture, RawTexture producedRawTexture, @Nullable SurfaceTexture outsideSurfaceTexture, @Nullable BasicTexture outsideTexture);

}
