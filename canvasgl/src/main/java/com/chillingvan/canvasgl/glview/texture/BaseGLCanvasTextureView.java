package com.chillingvan.canvasgl.glview.texture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.chillingvan.canvasgl.CanvasGL;
import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.OpenGLUtil;
import com.chillingvan.canvasgl.glview.GLView;
import com.chillingvan.canvasgl.util.Loggers;

import androidx.annotation.ColorInt;

/**
 *
 * From init to run: onSizeChange --> onSurfaceTextureAvailable --> createGLThread --> createSurface --> onSurfaceCreated --> onSurfaceChanged
 * From pause to run: onResume --> createSurface --> onSurfaceChanged
 * From stop to run: onResume --> onSurfaceTextureAvailable --> createGLThread --> createSurface  --> onSurfaceCreated --> onSurfaceChanged
 */
abstract class BaseGLCanvasTextureView extends BaseGLTextureView implements GLViewRenderer {


    private static final String TAG = "BaseGLCanvasTextureView";
    protected ICanvasGL mCanvas;
    private int backgroundColor = Color.TRANSPARENT;

    public BaseGLCanvasTextureView(Context context) {
        super(context);
    }

    public BaseGLCanvasTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseGLCanvasTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        super.init();
        setRenderer(this);
    }

    @Override
    public void onSurfaceCreated() {
        Loggers.d(TAG, "onSurfaceCreated: ");
        mCanvas = new CanvasGL();
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        Loggers.d(TAG, "onSurfaceChanged: ");
        mCanvas.setSize(width, height);

    }

    @Override
    public void onDrawFrame() {
        mCanvas.clearBuffer(backgroundColor);
        onGLDraw(mCanvas);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCanvas != null) {
            mCanvas.pause();
        }
    }

    /**
     * Force clear texture and bitmap cache of the canvas. This is not necessary needed.
     * The canvas uses weak HashMap to reference bitmap and will recycle the texture when finalize
     */
    public void clearTextureCache() {
        if (mCanvas != null) {
            mCanvas.clearBitmapCache();
        }
    }

    protected abstract void onGLDraw(ICanvasGL canvas);


    /**
     * If setOpaque(true) used, this method will not work.
     */
    public void setRenderBackgroundColor(@ColorInt int color) {
        this.backgroundColor = color;
    }


    public void getDrawingBitmap(final Rect rect, final GLView.GetDrawingCacheCallback getDrawingCacheCallback) {

        queueEvent(new Runnable() {
            @Override
            public void run() {
                onDrawFrame();
                onDrawFrame();
                final Bitmap bitmapFromGLSurface = OpenGLUtil.createBitmapFromGLSurface(rect.left, rect.top, rect.right - rect.left, rect.bottom - rect.top, getHeight());

                post(new Runnable() {
                    @Override
                    public void run() {
                        getDrawingCacheCallback.onFetch(bitmapFromGLSurface);
                    }
                });
            }
        });
        requestRender();
    }
}
