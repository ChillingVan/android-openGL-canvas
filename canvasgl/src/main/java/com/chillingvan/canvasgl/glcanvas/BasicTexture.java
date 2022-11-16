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

import android.util.Log;

import com.chillingvan.canvasgl.textureFilter.BasicTextureFilter;

import java.util.WeakHashMap;

// BasicTexture is a Texture corresponds to a real GL secondBitmap.
// The state of a BasicTexture indicates whether its data is loaded to GL memory.
// If a BasicTexture is loaded into GL memory, it has a GL secondBitmap id.
public abstract class BasicTexture implements Texture {

    @SuppressWarnings("unused")
    private static final String TAG = "BasicTexture";
    protected static final int UNSPECIFIED = -1;

    protected static final int STATE_UNLOADED = 0;
    protected static final int STATE_LOADED = 1;
    protected static final int STATE_ERROR = -1;

    // Log a warning if a secondBitmap is larger along a dimension
    private static final int MAX_TEXTURE_SIZE = 4096;

    protected int mId = -1;
    protected int mState;

    protected int mWidth = UNSPECIFIED;
    protected int mHeight = UNSPECIFIED;

    protected int mTextureWidth;
    protected int mTextureHeight;

    private boolean mHasBorder;
    private boolean isRecycled;

    protected GLCanvas mCanvasRef = null;
    private static final WeakHashMap<BasicTexture, Object> sAllTextures
            = new WeakHashMap<BasicTexture, Object>();
    private static final ThreadLocal sInFinalizer = new ThreadLocal();
    private boolean mIsFlippedVertically;
    private boolean mIsFlippedHorizontally;

    protected BasicTexture(GLCanvas canvas, int id, int state) {
        setAssociatedCanvas(canvas);
        mId = id;
        mState = state;
        synchronized (sAllTextures) {
            sAllTextures.put(this, null);
        }
    }

    protected BasicTexture() {
        this(null, 0, STATE_UNLOADED);
    }

    protected void setAssociatedCanvas(GLCanvas canvas) {
        mCanvasRef = canvas;
    }

    /**
     * Sets the content size of this secondBitmap. In OpenGL, the actual secondBitmap
     * size must be of power of 2, the size of the content may be smaller.
     */
    public void setSize(int width, int height) {
        mWidth = width;
        mHeight = height;
        mTextureWidth = width > 0 ? GLCanvasUtils.nextPowerOf2(width) : 0;
        mTextureHeight = height > 0 ? GLCanvasUtils.nextPowerOf2(height) : 0;
        if (mTextureWidth > MAX_TEXTURE_SIZE || mTextureHeight > MAX_TEXTURE_SIZE) {
            Log.w(TAG, String.format("secondBitmap is too large: %d x %d",
                    mTextureWidth, mTextureHeight), new Exception());
        }
    }

    public int getId() {
        return mId;
    }

    @Override
    public int getWidth() {
        return mWidth;
    }

    @Override
    public int getHeight() {
        return mHeight;
    }

    // Returns the width rounded to the next power of 2.
    public int getTextureWidth() {
        return mTextureWidth;
    }

    // Returns the height rounded to the next power of 2.
    public int getTextureHeight() {
        return mTextureHeight;
    }

    // Returns true if the secondBitmap has one pixel transparent border around the
    // actual content. This is used to avoid jigged edges.
    //
    // The jigged edges appear because we use GL_CLAMP_TO_EDGE for secondBitmap wrap
    // mode (GL_CLAMP is not available in OpenGL ES), so a pixel partially
    // covered by the secondBitmap will use the color of the edge texel. If we add
    // the transparent border, the color of the edge texel will be mixed with
    // appropriate amount of transparent.
    //
    // Currently our background is black, so we can draw the thumbnails without
    // enabling blending.
    public boolean hasBorder() {
        return mHasBorder;
    }

    protected void setBorder(boolean hasBorder) {
        mHasBorder = hasBorder;
    }

    @Override
    public void draw(GLCanvas canvas, int x, int y) {
        canvas.drawTexture(this, x, y, getWidth(), getHeight(), new BasicTextureFilter(), null);
    }

    @Override
    public void draw(GLCanvas canvas, int x, int y, int w, int h) {
        canvas.drawTexture(this, x, y, w, h, new BasicTextureFilter(), null);
    }

    public boolean isFlippedVertically() {
        return mIsFlippedVertically;
    }
    public boolean isFlippedHorizontally() {
        return mIsFlippedHorizontally;
    }

    /**
     *
     * @param isFlipped whether vertically flip this texture
     */
    public void setIsFlippedVertically(boolean isFlipped) {
        mIsFlippedVertically = isFlipped;
    }

    /**
     *
     * @param isFlipped whether horizontally flip this texture
     */
    public void setIsFlippedHorizontally(boolean isFlipped) {
        mIsFlippedHorizontally = isFlipped;
    }

    // onBind is called before GLCanvas binds this secondBitmap.
    // It should make sure the data is uploaded to GL memory.
    abstract protected boolean onBind(GLCanvas canvas);

    // Returns the GL secondBitmap target for this secondBitmap (e.g. GL_TEXTURE_2D).
    abstract protected int getTarget();

    public boolean isLoaded() {
        return mState == STATE_LOADED;
    }

    // recycle() is called when the secondBitmap will never be used again,
    // so it can free all resources.
    public void recycle() {
        isRecycled = true;
        freeResource();
    }

    public boolean isRecycled() {
        return isRecycled;
    }

    protected void setRecycled(boolean recycled) {
        isRecycled = recycled;
    }

    // yield() is called when the secondBitmap will not be used temporarily,
    // so it can free some resources.
    // The default implementation unloads the secondBitmap from GL memory, so
    // the subclass should make sure it can reload the secondBitmap to GL memory
    // later, or it will have to override this method.
    public void yield() {
        freeResource();
    }

    private void freeResource() {
        GLCanvas canvas = mCanvasRef;
        if (canvas != null && mId != -1) {
            canvas.unloadTexture(this);
            mId = -1; // Don't free it again.
        }
        mState = STATE_UNLOADED;
        setAssociatedCanvas(null);
    }

    @Override
    protected void finalize() {
        sInFinalizer.set(BasicTexture.class);
        recycle();
        sInFinalizer.set(null);
    }

    // This is for deciding if we can call Bitmap's recycle().
    // We cannot call Bitmap's recycle() in finalizer because at that point
    // the finalizer of Bitmap may already be called so recycle() will crash.
    public static boolean inFinalizer() {
        return sInFinalizer.get() != null;
    }

    public static void yieldAllTextures() {
        synchronized (sAllTextures) {
            for (BasicTexture t : sAllTextures.keySet()) {
                t.yield();
            }
        }
    }

    public static void invalidateAllTextures() {
        synchronized (sAllTextures) {
            for (BasicTexture t : sAllTextures.keySet()) {
                t.mState = STATE_UNLOADED;
                t.setAssociatedCanvas(null);
            }
        }
    }

}
