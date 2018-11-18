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
import com.chillingvan.canvasgl.matrix.BaseBitmapMatrix;
import com.chillingvan.canvasgl.matrix.IBitmapMatrix;
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

    void drawBitmap(Bitmap bitmap, @NonNull IBitmapMatrix matrix);

    void drawBitmap(Bitmap bitmap, IBitmapMatrix matrix, @NonNull TextureFilter textureFilter);

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
     * Default bitmap matrix. It uses perspective projection type. So it can have 3D feel. As a result, rotateX and rotateY are supported.
     */
    class BitmapMatrix extends BaseBitmapMatrix {


        public BitmapMatrix() {
            reset();
        }

        public void translate(float dx, float dy) {
            transform[TRANSLATE_X] += dx;
            transform[TRANSLATE_Y] += dy;
        }

        /**
         * Should not be larger than 2 * GLES20.GL_MAX_VIEWPORT_DIMS
         * @param sx
         * @param sy
         */
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

        @Override
        public float[] obtainResultMatrix(int viewportW, int viewportH, float x, float y, float drawW, float drawH) {

            float ratio = (float) viewportW / viewportH;

            transform[TRANSLATE_X] += x;
            transform[TRANSLATE_Y] += y;

            int viewPortRatio = 2; // The ratio is for even this view port contains the real view port, so that the picture won't be interrupted.
            // Move view port to make sure the picture in the center of the view port.
            final float absTransX = Math.abs(transform[TRANSLATE_X]); // Make sure viewportX + realViewportW >= viewportW
            final float absTransY = Math.abs(transform[TRANSLATE_Y]); // Make sure realViewportH - viewportY >= viewportH
            int viewportX = (int) (drawW / 2 - (((float)viewPortRatio/2)) * viewportW + transform[TRANSLATE_X] - absTransX);
            int viewportY = (int) -((drawH / 2 + transform[TRANSLATE_Y] + absTransY) + ((float)viewPortRatio-2)/2 * viewportH);//The origin is (0,h)
            final int realViewportW = (int) (viewPortRatio * viewportW + 2*absTransX);
            final int realViewportH = (int) (viewPortRatio * viewportH + 2*absTransY);
            GLES20.glViewport(viewportX, viewportY, realViewportW, realViewportH);

            Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, NEAR, FAR);
            Matrix.setLookAtM(mViewMatrix, 0,
                    0, 0, EYEZ,
                    0, 0, 0,
                    0, 1, 0);
            Matrix.multiplyMM(viewProjectionMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

            // reverse Y
            Matrix.scaleM(mModelMatrix, 0, 1, -1, 1);
            GLES20Canvas.printMatrix("model init:", mModelMatrix, 0);


            Matrix.rotateM(mModelMatrix, 0, transform[ROTATE_X], 1f, 0, 0);
            Matrix.rotateM(mModelMatrix, 0, transform[ROTATE_Y], 0, 1f, 0);
            Matrix.rotateM(mModelMatrix, 0, transform[ROTATE_Z], 0, 0, 1f);
            GLES20Canvas.printMatrix("model rotated:", mModelMatrix, 0);


            // Translate to the middle of the projection
            // realW,realH are the w,h in the middleZ of the projection
            float realW = ratio * drawW / viewportW * Z_RATIO * 2 / 2;
            float realH = drawH / viewportH * Z_RATIO * 2 / 2;
            // Need to middle X, Y of the plane, too. The middle of the plane is (0, 0, -Z_RATIO + EYEZ)
            Matrix.translateM(tempMultiplyMatrix4, 0, mModelMatrix, 0, -realW/2, -realH/2, -Z_RATIO + EYEZ);
            GLES20Canvas.printMatrix("model translated:", tempMultiplyMatrix4, 0);


            Matrix.scaleM(tempMultiplyMatrix4, 0, transform[SCALE_X] * realW, transform[SCALE_Y] * realH, 1);
            GLES20Canvas.printMatrix("model scaled:", tempMultiplyMatrix4, 0);

            Matrix.multiplyMM(mvp, 0, viewProjectionMatrix, 0, tempMultiplyMatrix4, 0);
            GLES20Canvas.printMatrix("ultra matrix:", mvp, 0);

            return mvp;
        }

    }



    /**
     * Orthographic bitmap matrix. It uses orthographic projection type. So it only support rotateX and rotateY.
     */
    class OrthoBitmapMatrix extends BaseBitmapMatrix {

        public OrthoBitmapMatrix() {
            reset();
        }


        public void translate(float dx, float dy) {
            transform[TRANSLATE_X] += dx;
            transform[TRANSLATE_Y] += dy;
        }

        public void scale(float sx, float sy) {
            transform[SCALE_X] *= sx;
            transform[SCALE_Y] *= sy;
        }

        public void rotateZ(float degrees) {
            transform[ROTATE_Z] += degrees;
        }

        @Override
        public float[] obtainResultMatrix(int viewportW, int viewportH, float x, float y, float drawW, float drawH) {
            float ratio = (float) viewportW / viewportH;

            transform[TRANSLATE_X] += x;
            transform[TRANSLATE_Y] += y;

            GLES20.glViewport(0, 0, viewportW, viewportH);

            Matrix.orthoM(mProjectionMatrix, 0, 0, ratio, 0, 1, -1, 1);
            Matrix.multiplyMM(viewProjectionMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

            Matrix.scaleM(mModelMatrix, 0, 1, -1, 1);
            GLES20Canvas.printMatrix("model init:", mModelMatrix, 0);

            Matrix.rotateM(mModelMatrix, 0, transform[ROTATE_Z], 0, 0, 1f);
            GLES20Canvas.printMatrix("model rotated:", mModelMatrix, 0);


            final float transX = transform[TRANSLATE_X] / viewportW;
            final float transY = transform[TRANSLATE_Y] / viewportH -1;
            Matrix.translateM(tempMultiplyMatrix4, 0, mModelMatrix, 0, transX, transY, 0);
            GLES20Canvas.printMatrix("model translated:", tempMultiplyMatrix4, 0);

            Matrix.scaleM(tempMultiplyMatrix4, 0, transform[SCALE_X] * drawW/viewportW * ratio, transform[SCALE_Y] * drawH/viewportH, 1);
            GLES20Canvas.printMatrix("model scaled:", tempMultiplyMatrix4, 0);


            Matrix.multiplyMM(mvp, 0, viewProjectionMatrix, 0, tempMultiplyMatrix4, 0);
            GLES20Canvas.printMatrix("ultra matrix:", mvp, 0);

            return mvp;
        }

    }
}
