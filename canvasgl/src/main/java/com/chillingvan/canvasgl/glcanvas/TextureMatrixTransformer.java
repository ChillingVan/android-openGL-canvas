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

import android.graphics.RectF;

/**
 * Created by Chilling on 2016/11/6.
 */

public class TextureMatrixTransformer {
    // This function changes the source coordinate to the secondBitmap coordinates.
    // It also clips the source and target coordinates if it is beyond the
    // bound of the secondBitmap.
    public static void convertCoordinate(RectF source, BasicTexture texture) {
        int width = texture.getWidth();
        int height = texture.getHeight();
        int texWidth = texture.getTextureWidth();
        int texHeight = texture.getTextureHeight();
        // Convert to secondBitmap coordinates
        source.left /= texWidth;
        source.right /= texWidth;
        source.top /= texHeight;
        source.bottom /= texHeight;

        // Clip if the rendering range is beyond the bound of the secondBitmap.
        float xBound = (float) width / texWidth;
        if (source.right > xBound) {
            source.right = xBound;
        }
        float yBound = (float) height / texHeight;
        if (source.bottom > yBound) {
            source.bottom = yBound;
        }
    }

    public static void setTextureMatrix(RectF source, float[] textureMatrix) {
        textureMatrix[0] = source.width();
        textureMatrix[5] = source.height();
        textureMatrix[12] = source.left;
        textureMatrix[13] = source.top;
    }

    public static void copyTextureCoordinates(BasicTexture texture, RectF outRect) {
        int left = 0;
        int top = 0;
        int right = texture.getWidth();
        int bottom = texture.getHeight();
        if (texture.hasBorder()) {
            left = 1;
            top = 1;
            right -= 1;
            bottom -= 1;
        }
        outRect.set(left, top, right, bottom);
    }
}
