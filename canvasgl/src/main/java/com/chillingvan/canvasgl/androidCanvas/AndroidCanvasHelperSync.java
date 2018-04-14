package com.chillingvan.canvasgl.androidCanvas;

import android.graphics.Bitmap;
import android.graphics.Canvas;
/**
 * Created by Chilling on 2018/4/14.
 *
 * This will use a bitmap and draw in the thread you use.
 * 这个类不会另起一个线程。
 */
class AndroidCanvasHelperSync implements IAndroidCanvasHelper {

    private int width;
    private int height;
    private Bitmap bitmapBoard;
    private Canvas canvas;

    @Override
    public void init(int width, int height) {
        if (this.width != width || this.height != height) {
            this.width = width;
            this.height = height;
            bitmapBoard = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmapBoard);
        }
    }

    @Override
    public void draw(CanvasPainter canvasPainter) {
        if (canvas == null) {
            throw new IllegalStateException("DrawTextHelper has not init.");
        }
        canvasPainter.draw(canvas);
    }

    @Override
    public Bitmap getOutputBitmap() {
        return bitmapBoard;
    }
}
