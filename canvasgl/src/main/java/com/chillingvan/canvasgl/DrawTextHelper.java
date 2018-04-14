package com.chillingvan.canvasgl;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Chilling on 2018/4/14.
 */
public class DrawTextHelper {

    private int width;
    private int height;
    private Bitmap bitmapBoard;
    private Canvas canvas;

    public void init(int width, int height) {
        if (this.width != width || this.height != height) {
            this.width = width;
            this.height = height;
            bitmapBoard = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmapBoard);
        }
    }

    public void draw(TextDrawee textDrawee) {
        if (canvas == null) {
            throw new IllegalStateException("DrawTextHelper has not init.");
        }
        textDrawee.draw(canvas);
    }

    public interface TextDrawee {
        void draw(Canvas canvas);
    }

    public Bitmap getOutputBitmap() {
        return bitmapBoard;
    }
}
