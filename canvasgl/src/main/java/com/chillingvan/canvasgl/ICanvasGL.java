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
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.chillingvan.canvasgl.glcanvas.BasicTexture;
import com.chillingvan.canvasgl.glcanvas.BitmapTexture;
import com.chillingvan.canvasgl.glcanvas.GLCanvas;
import com.chillingvan.canvasgl.glcanvas.GLES20Canvas;
import com.chillingvan.canvasgl.glcanvas.GLPaint;
import com.chillingvan.canvasgl.glcanvas.RawTexture;
import com.chillingvan.canvasgl.textureFilter.TextureFilter;

import java.util.Arrays;

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
     * If used in a texture view, make sure the setOpaque(false) is called.
     *
     * @param alpha alpha value
     */
    void setAlpha(@IntRange(from = 0, to = 255) int alpha);

    /**
     * Created by Chilling on 2017/8/19.
     */
    class BitmapMatrix {
        public static final int TRANSLATE_X = 0;
        public static final int TRANSLATE_Y = 1;
        public static final int SCALE_X = 2;
        public static final int SCALE_Y = 3;
        public static final int ROTATE_X = 4;
        public static final int ROTATE_Y = 5;
        public static final int ROTATE_Z = 6;
        private float[] transform = new float[7];

        public static final int MATRIX_SIZE = 16;

        private float[] tempMultiplyMatrix4 = new float[MATRIX_SIZE];

        private float[] mViewMatrix = new float[MATRIX_SIZE];
        private float[] mProjectionMatrix = new float[MATRIX_SIZE];
        private float[] mModelMatrix = new float[MATRIX_SIZE];
        private float[] viewProjectionMatrix = new float[MATRIX_SIZE];
        private float[] mvp = new float[MATRIX_SIZE];

        final static float NEAR = 1;
        final static float FAR = 10;
        final static float EYEZ = 5;
        final static float Z_RATIO = (FAR + NEAR) / 2 / NEAR;


        public BitmapMatrix() {
            reset();
        }

        public void reset() {
            Matrix.setIdentityM(mViewMatrix, 0);
            Matrix.setIdentityM(mProjectionMatrix, 0);
            Matrix.setIdentityM(mModelMatrix, 0);
            Matrix.setIdentityM(viewProjectionMatrix, 0);
            Matrix.setIdentityM(mvp, 0);

            Matrix.setIdentityM(tempMultiplyMatrix4, 0);
            Arrays.fill(transform, 0);
            transform[SCALE_X] = 1;
            transform[SCALE_Y] = 1;
        }

        public void translate(float dx, float dy) {
            transform[TRANSLATE_X] += dx;
            transform[TRANSLATE_Y] += dy;
        }

        public void scale(float sx, float sy) {
            transform[SCALE_X] *= sx;
            transform[SCALE_Y] *= sy;
        }

        public void rotateX(float degrees) {
            transform[ROTATE_X] += degrees;
        }

        public void rotateY(float degrees) {
            transform[ROTATE_Y] += degrees;
        }

        public void rotateZ(float degrees) {
            transform[ROTATE_Z] += degrees;
        }

        public float[] obtainResultMatrix(int viewportW, int viewportH, float x, float y, float drawW, float drawH) {

            float ratio = (float) viewportW / viewportH;

            transform[TRANSLATE_X] += x;
            transform[TRANSLATE_Y] += y;

            int viewportX = (int) (drawW / 2 - viewportW + transform[TRANSLATE_X]);
            int viewportY = (int) (-drawH / 2 - transform[TRANSLATE_Y]);
            GLES20.glViewport(viewportX, viewportY, 2 * viewportW, 2 * viewportH);

            Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, NEAR, FAR);
            Matrix.setLookAtM(mViewMatrix, 0,
                    0, 0, EYEZ,
                    0, 0, 0,
                    0, 1, 0);
            Matrix.multiplyMM(viewProjectionMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
            Matrix.scaleM(mModelMatrix, 0, 1, -1, 1);
            GLES20Canvas.printMatrix("model -2:", mModelMatrix, 0);


            Matrix.rotateM(mModelMatrix, 0, transform[ROTATE_X], 1f, 0, 0);
            Matrix.rotateM(mModelMatrix, 0, transform[ROTATE_Y], 0, 1f, 0);
            Matrix.rotateM(mModelMatrix, 0, transform[ROTATE_Z], 0, 0, 1f);
            GLES20Canvas.printMatrix("model -1.5:", mModelMatrix, 0);


            float realW = drawW / viewportW * Z_RATIO * 2 * ratio / 2;
            float realH = drawH / viewportH * Z_RATIO * 2 / 2;
            Matrix.translateM(tempMultiplyMatrix4, 0, mModelMatrix, 0, -realW/2, -realH/2, -Z_RATIO + EYEZ);
            GLES20Canvas.printMatrix("model -1:", tempMultiplyMatrix4, 0);


            Matrix.scaleM(tempMultiplyMatrix4, 0, transform[SCALE_X] * realW, transform[SCALE_Y] * realH, 1);
            GLES20Canvas.printMatrix("model:", tempMultiplyMatrix4, 0);

            Matrix.multiplyMM(mvp, 0, viewProjectionMatrix, 0, tempMultiplyMatrix4, 0);
            GLES20Canvas.printMatrix("ultra matrix:", mvp, 0);

            return mvp;
        }

    }
}
