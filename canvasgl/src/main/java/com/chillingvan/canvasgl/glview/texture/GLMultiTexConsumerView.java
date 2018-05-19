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
import android.util.AttributeSet;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.glview.texture.gles.EglContextWrapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class is used to accept eglContext and textures from outside. Then it can use them to draw.
 */
public abstract class GLMultiTexConsumerView extends BaseGLCanvasTextureView {

    protected List<GLTexture> consumedTextures = new ArrayList<>();

    public GLMultiTexConsumerView(Context context) {
        super(context);
    }

    public GLMultiTexConsumerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GLMultiTexConsumerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * @param sharedEglContext The openGL context from other or {@link EglContextWrapper#EGL_NO_CONTEXT_WRAPPER}
     */
    public void setSharedEglContext(EglContextWrapper sharedEglContext) {
        glThreadBuilder.setSharedEglContext(sharedEglContext);
        createGLThread();
    }


    /**
     *
     * @param glTexture texture from outSide.
     */
    public void addConsumeGLTexture(GLTexture glTexture) {
        consumedTextures.add(glTexture);
    }

    /**
     *
     * Will not call until @param surfaceTexture not null
     */
    protected abstract void onGLDraw(ICanvasGL canvas, List<GLTexture> consumedTextures);

    @Override
    protected final void onGLDraw(ICanvasGL canvas) {
        Iterator<GLTexture> iterator = consumedTextures.iterator();
        while (iterator.hasNext()) {
            GLTexture next =  iterator.next();
            if (next.getRawTexture().isRecycled()) {
                iterator.remove();
            }
        }
        onGLDraw(canvas, consumedTextures);
    }

    @Override
    protected void surfaceDestroyed() {
        super.surfaceDestroyed();
        consumedTextures.clear();
    }
}
