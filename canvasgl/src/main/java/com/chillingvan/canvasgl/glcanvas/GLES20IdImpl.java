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

public class GLES20IdImpl implements GLId {
    private final int[] mTempIntArray = new int[1];

    @Override
    public int generateTexture() {
        GLES20.glGenTextures(1, mTempIntArray, 0);
        GLES20Canvas.checkError();
        return mTempIntArray[0];
    }

    @Override
    public void glGenBuffers(int n, int[] buffers, int offset) {
        GLES20.glGenBuffers(n, buffers, offset);
        GLES20Canvas.checkError();
    }

    @Override
    public void glDeleteTextures(int n, int[] textures, int offset) {
        GLES20.glDeleteTextures(n, textures, offset);
        GLES20Canvas.checkError();
    }


    @Override
    public void glDeleteBuffers(int n, int[] buffers, int offset) {
        GLES20.glDeleteBuffers(n, buffers, offset);
        GLES20Canvas.checkError();
    }

    @Override
    public void glDeleteFramebuffers(int n, int[] buffers, int offset) {
        GLES20.glDeleteFramebuffers(n, buffers, offset);
        GLES20Canvas.checkError();
    }
}
