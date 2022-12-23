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

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import androidx.annotation.Nullable;

import com.chillingvan.canvasgl.shapeFilter.DrawShapeFilter;
import com.chillingvan.canvasgl.textureFilter.TextureFilter;

//
// GLCanvas gives a convenient interface to draw using OpenGL.
//
// When a rectangle is specified in this interface, it means the region
// [x, x+width) * [y, y+height)
//
public interface GLCanvas {

    public GLId getGLId();

    // Tells GLCanvas the size of the underlying GL surface. This should be
    // called before first drawing and when the size of GL surface is changed.
    // This is called by GLRoot and should not be called by the clients
    // who only want to draw on the GLCanvas. Both width and height must be
    // nonnegative.
    public abstract void setSize(int width, int height);

    // Clear the drawing buffers.
    public abstract void clearBuffer();

    public abstract void clearBuffer(float[] argb);

    // Sets and gets the current alpha, alpha must be in [0, 1].
    public abstract void setAlpha(float alpha);

    public abstract float getAlpha();

    // (current alpha) = (current alpha) * alpha
    public abstract void multiplyAlpha(float alpha);

    // Change the current transform matrix.
    void translate(float x, float y, float z);

    void translate(float x, float y);

    void scale(float sx, float sy, float sz);

    void rotate(float angle, float x, float y, float z);

    void multiplyMatrix(float[] mMatrix, int offset);

    // Pushes the configuration state (matrix, and alpha) onto
    // a private stack.
    public abstract void save();

    // Same as save(), but only save those specified in saveFlags.
    public abstract void save(int saveFlags);

    public static final int SAVE_FLAG_ALL = 0xFFFFFFFF;
    public static final int SAVE_FLAG_ALPHA = 0x01;
    public static final int SAVE_FLAG_MATRIX = 0x02;

    // Pops from the top of the stack as current configuration state (matrix,
    // alpha, and clip). This call balances a previous call to save(), and is
    // used to remove all modifications to the configuration state since the
    // last save call.
    public abstract void restore();

    void drawCircle(float x, float y, float radius, GLPaint paint, DrawShapeFilter drawShapeFilter);

    // Draws a line using the specified paint from (x1, y1) to (x2, y2).
    // (Both end points are included).
    public abstract void drawLine(float x1, float y1, float x2, float y2, GLPaint paint, DrawShapeFilter drawShapeFilter);

    // Draws a rectangle using the specified paint from (x1, y1) to (x2, y2).
    // (Both end points are included).
    public abstract void drawRect(float x1, float y1, float x2, float y2, GLPaint paint, DrawShapeFilter drawShapeFilter);

    // Fills the specified rectangle with the specified color.
    public abstract void fillRect(float x, float y, float width, float height, int color, DrawShapeFilter drawShapeFilter);

    // Draws a secondBitmap to the specified rectangle.
    public abstract void drawTexture(
            BasicTexture texture, int x, int y, int width, int height, TextureFilter textureFilter, @Nullable ICustomMVPMatrix customMVPMatrix);

    public abstract void drawMesh(BasicTexture tex, int x, int y, int xyBuffer,
                                  int uvBuffer, int indexBuffer, int indexCount, int mode);

    // Draws the source rectangle part of the secondBitmap to the target rectangle.
    public abstract void drawTexture(BasicTexture texture, RectF source, RectF target, TextureFilter textureFilter, @Nullable ICustomMVPMatrix customMVPMatrix);

    // Draw a secondBitmap with a specified secondBitmap transform.
    public abstract void drawTexture(BasicTexture texture, float[] mTextureTransform,
                                     int x, int y, int w, int h, TextureFilter textureFilter,@Nullable ICustomMVPMatrix customMVPMatrix);

    // Draw two textures to the specified rectangle. The actual secondBitmap used is
    // from * (1 - ratio) + to * ratio
    // The two textures must have the same size.
    public abstract void drawMixed(BasicTexture from, int toColor,
                                   float ratio, int x, int y, int w, int h, DrawShapeFilter drawShapeFilter);

    // Draw a region of a secondBitmap and a specified color to the specified
    // rectangle. The actual color used is from * (1 - ratio) + to * ratio.
    // The region of the secondBitmap is defined by parameter "src". The target
    // rectangle is specified by parameter "target".
    public abstract void drawMixed(BasicTexture from, int toColor,
                                   float ratio, RectF src, RectF target, DrawShapeFilter drawShapeFilter);

    // Unloads the specified secondBitmap from the canvas. The resource allocated
    // to draw the secondBitmap will be released. The specified secondBitmap will return
    // to the unloaded state. This function should be called only from
    // BasicTexture or its descendant
    public abstract boolean unloadTexture(BasicTexture texture);

    // Delete the specified buffer object, similar to unloadTexture.
    public abstract void deleteBuffer(int bufferId);

    // Delete the textures and buffers in GL side. This function should only be
    // called in the GL thread.
    public abstract void deleteRecycledResources();

    // Dump statistics information and clear the counters. For debug only.
    public abstract void dumpStatisticsAndClear();

    public abstract void beginRenderTarget(RawTexture texture);

    public abstract void endRenderTarget();

    /**
     * Sets secondBitmap parameters to use GL_CLAMP_TO_EDGE for both
     * GL_TEXTURE_WRAP_S and GL_TEXTURE_WRAP_T. Sets secondBitmap parameters to be
     * GL_LINEAR for GL_TEXTURE_MIN_FILTER and GL_TEXTURE_MAG_FILTER.
     * bindTexture() must be called prior to this.
     *
     * @param texture The secondBitmap to set parameters on.
     */
    public abstract void setTextureParameters(BasicTexture texture);

    /**
     * Initializes the secondBitmap to a size by calling texImage2D on it.
     *
     * @param texture The secondBitmap to initialize the size.
     * @param format The secondBitmap format (e.g. GL_RGBA)
     * @param type The secondBitmap type (e.g. GL_UNSIGNED_BYTE)
     */
    public abstract void initializeTextureSize(BasicTexture texture, int format, int type);

    /**
     * Initializes the secondBitmap to a size by calling texImage2D on it.
     *
     * @param texture The secondBitmap to initialize the size.
     * @param bitmap The bitmap to initialize the bitmap with.
     */
    public abstract void initializeTexture(BasicTexture texture, Bitmap bitmap);

    /**
     * Calls glTexSubImage2D to upload a bitmap to the secondBitmap.
     *
     * @param texture The target secondBitmap to write to.
     * @param xOffset Specifies a texel offset in the x direction within the
     *            secondBitmap array.
     * @param yOffset Specifies a texel offset in the y direction within the
     *            secondBitmap array.
     * @param format The secondBitmap format (e.g. GL_RGBA)
     * @param type The secondBitmap type (e.g. GL_UNSIGNED_BYTE)
     */
    public abstract void texSubImage2D(BasicTexture texture, int xOffset, int yOffset,
                                       Bitmap bitmap,
                                       int format, int type);

    /**
     * Generates buffers and uploads the buffer data.
     *
     * @param buffer The buffer to upload
     * @return The buffer ID that was generated.
     */
    public abstract int uploadBuffer(java.nio.FloatBuffer buffer);

    /**
     * Generates buffers and uploads the element array buffer data.
     *
     * @param buffer The buffer to upload
     * @return The buffer ID that was generated.
     */
    public abstract int uploadBuffer(java.nio.ByteBuffer buffer);

    /**
     * After LightCycle makes GL calls, this method is called to restore the GL
     * configuration to the one expected by GLCanvas.
     */
    public abstract void recoverFromLightCycle();

    /**
     * Gets the bounds given by x, y, width, and height as well as the internal
     * matrix state. There is no special handling for non-90-degree rotations.
     * It only considers the lower-left and upper-right corners as the bounds.
     *
     * @param bounds The output bounds to write to.
     * @param x The left side of the input rectangle.
     * @param y The bottom of the input rectangle.
     * @param width The width of the input rectangle.
     * @param height The height of the input rectangle.
     */
    public abstract void getBounds(Rect bounds, int x, int y, int width, int height);

    void setOnPreDrawTextureListener(GLES20Canvas.OnPreDrawTextureListener l);

    void setOnPreDrawShapeListener(OnPreDrawShapeListener l);

    interface OnPreDrawTextureListener {
        void onPreDraw(int textureProgram, BasicTexture texture, TextureFilter textureFilter);
    }

    interface OnPreDrawShapeListener {
        void onPreDraw(int program, DrawShapeFilter drawShapeFilter);
    }

    interface ICustomMVPMatrix {
        float[] getMVPMatrix(int viewportW, int viewportH, float x, float y, float drawW, float drawH);
    }
}
