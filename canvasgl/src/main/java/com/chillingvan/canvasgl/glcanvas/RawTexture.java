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

package com.chillingvan.canvasgl.glcanvas;

import android.opengl.GLES20;
import android.util.Log;

import javax.microedition.khronos.opengles.GL11;

public class RawTexture extends BasicTexture {
    private static final String TAG = "RawTexture";

    private final boolean mOpaque;
    private boolean mIsFlipped;
    private int target = GL11.GL_TEXTURE_2D;

    public RawTexture(int width, int height, boolean opaque) {
        this(width, height, opaque, GL11.GL_TEXTURE_2D);
    }

    public RawTexture(int width, int height, boolean opaque, int target) {
        mOpaque = opaque;
        setSize(width, height);
        this.target = target;
    }

    @Override
    public boolean isOpaque() {
        return mOpaque;
    }

    @Override
    public boolean isFlippedVertically() {
        return mIsFlipped;
    }

    /**
     *
     * @param isFlipped whether vertically flip this texture
     */
    public void setIsFlippedVertically(boolean isFlipped) {
        mIsFlipped = isFlipped;
    }

    public void prepare(GLCanvas canvas) {
        GLId glId = canvas.getGLId();
        mId = glId.generateTexture();

        if (target == GLES20.GL_TEXTURE_2D) {
            canvas.initializeTextureSize(this, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE);
        }
        canvas.setTextureParameters(this);
        mState = STATE_LOADED;
        setAssociatedCanvas(canvas);
    }

    @Override
    protected boolean onBind(GLCanvas canvas) {
        if (isLoaded()) return true;
        Log.w(TAG, "lost the content due to context change");
        return false;
    }

    @Override
     public void yield() {
         // we cannot free the secondBitmap because we have no backup.
     }

    @Override
    protected int getTarget() {
        return target;
    }
}
