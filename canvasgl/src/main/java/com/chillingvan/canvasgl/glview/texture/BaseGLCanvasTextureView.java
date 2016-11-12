package com.chillingvan.canvasgl.glview.texture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.opengl.GLSurfaceView;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;

import com.chillingvan.canvasgl.CanvasGL;
import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.OpenGLUtil;
import com.chillingvan.canvasgl.glview.GLView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Chilling on 2016/11/11.
 */

abstract class BaseGLCanvasTextureView extends BaseGLTextureView implements GLSurfaceView.Renderer {


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
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mCanvas = new CanvasGL();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mCanvas.setSize(width, height);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mGL = gl;
        mCanvas.clearBuffer(backgroundColor);
        onGLDraw(mCanvas);
    }

    protected abstract void onGLDraw(ICanvasGL canvas);


    /**
     * If setOpaque(false) used, this method will not work.
     */
    public void setRenderBackgroundColor(@ColorInt int color) {
        this.backgroundColor = color;
    }


    public void getDrawingBitmap(final Rect rect, final GLView.GetDrawingCacheCallback getDrawingCacheCallback) {

        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mGL == null) {
                    return;
                }
                onDrawFrame(mGL);
                onDrawFrame(mGL);
                final Bitmap bitmapFromGLSurface = OpenGLUtil.createBitmapFromGLSurface(rect.left, rect.top, rect.right - rect.left, rect.bottom - rect.top, mGL, getHeight());

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
