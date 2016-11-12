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
import android.util.AttributeSet;

/**
 * Created by Chilling on 2016/11/5.
 */

public abstract class GLTextureView extends BaseGLCanvasTextureView {
    public GLTextureView(Context context) {
        super(context);
    }

    public GLTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GLTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        super.onSurfaceTextureAvailable(surface, width, height);
        if (mGLThread == null) {
            createGLThread();
        }
    }
}
