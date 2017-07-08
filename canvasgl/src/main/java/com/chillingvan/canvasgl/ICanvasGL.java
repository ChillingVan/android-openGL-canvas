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

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.chillingvan.canvasgl.glcanvas.BasicTexture;
import com.chillingvan.canvasgl.glcanvas.BitmapTexture;
import com.chillingvan.canvasgl.glcanvas.GLCanvas;
import com.chillingvan.canvasgl.glcanvas.GLPaint;
import com.chillingvan.canvasgl.glcanvas.RawTexture;
import com.chillingvan.canvasgl.textureFilter.TextureFilter;

/**
 * Created by Matthew on 2016/9/26.
 */

public interface ICanvasGL {

    BitmapTexture bindBitmapToTexture(int whichTexture, Bitmap bitmap);

    void beginRenderTarget(RawTexture texture);

    void endRenderTarget();

    GLCanvas getGlCanvas();

    void drawSurfaceTexture(BasicTexture texture, @Nullable SurfaceTexture surfaceTexture, int left, int top, int right, int bottom);

    void drawSurfaceTexture(BasicTexture texture, @Nullable SurfaceTexture surfaceTexture, int left, int top, int right, int bottom, TextureFilter textureFilter);

    void drawBitmap(Bitmap bitmap, @NonNull CanvasGL.BitmapMatrix matrix);

    void drawBitmap(Bitmap bitmap, CanvasGL.BitmapMatrix matrix, @NonNull TextureFilter textureFilter);

    void drawBitmap(Bitmap bitmap, Rect src, RectF dst);

    void drawBitmap(Bitmap bitmap, int left, int top);

    void drawBitmap(Bitmap bitmap, int left, int top, @NonNull TextureFilter textureFilter);

    void drawBitmap(Bitmap bitmap, Rect src, Rect dst);

    void drawBitmap(Bitmap bitmap, RectF src, RectF dst, @NonNull TextureFilter textureFilter);

    void drawBitmap(Bitmap bitmap, int left, int top, int width, int height);

    void drawBitmap(Bitmap bitmap, int left, int top, int width, int height, @NonNull TextureFilter textureFilter);

    void invalidateTextureContent(Bitmap bitmap);

    void drawCircle(float x, float y, float radius, GLPaint paint);

    void drawLine(float startX, float startY, float stopX, float stopY, GLPaint paint);


    void drawRect(@NonNull RectF rect, @NonNull GLPaint paint);

    void drawRect(@NonNull Rect r, @NonNull GLPaint paint);

    void drawRect(float left, float top, float right, float bottom, GLPaint paint);

    void save();

    void save(int saveFlags);

    void restore();


    void rotate(float degrees);

    void rotate(float degrees, float px, float py);

    void scale(float sx, float sy);

    void scale(float sx, float sy, float px, float py);


    void translate(float dx, float dy);

    void clearBuffer();

    void clearBuffer(int color);

    void setSize(int width, int height);

    int getWidth();

    int getHeight();

    /**
     *  If used in a texture view, make sure the setOpaque(false) is called.
     * @param alpha alpha value
     */
    void setAlpha(@IntRange(from = 0, to = 255) int alpha);
}
