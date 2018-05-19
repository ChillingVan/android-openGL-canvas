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

package com.chillingvan.canvasglsample.textureView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.glcanvas.RawTexture;
import com.chillingvan.canvasgl.glview.texture.GLMultiTexConsumerView;
import com.chillingvan.canvasgl.glview.texture.GLTexture;
import com.chillingvan.canvasgl.textureFilter.BasicTextureFilter;
import com.chillingvan.canvasgl.textureFilter.TextureFilter;
import com.chillingvan.canvasglsample.R;

import java.util.List;

/**
 * Created by Chilling on 2016/11/5.
 */

public class PreviewConsumerTextureView extends GLMultiTexConsumerView {

    private TextureFilter textureFilter = new BasicTextureFilter();
    private Bitmap robot;

    public PreviewConsumerTextureView(Context context) {
        super(context);
    }

    public PreviewConsumerTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PreviewConsumerTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onGLDraw(ICanvasGL canvas, List<GLTexture> consumedTextures) {
        if (!consumedTextures.isEmpty()) {
            GLTexture consumedTexture = consumedTextures.get(0);
            SurfaceTexture sharedSurfaceTexture = consumedTexture.getSurfaceTexture();
            RawTexture sharedTexture = consumedTexture.getRawTexture();
            canvas.drawSurfaceTexture(sharedTexture, sharedSurfaceTexture, 0, 0, sharedTexture.getWidth(), sharedTexture.getHeight(), textureFilter);
            canvas.drawBitmap(robot, 0, 0 , 60, 60);
        }
    }

    public void setTextureFilter(TextureFilter textureFilter) {
        this.textureFilter = textureFilter;
    }

    @Override
    protected void init() {
        super.init();
        robot = BitmapFactory.decodeResource(getResources(), R.drawable.ic_robot);
    }

}
