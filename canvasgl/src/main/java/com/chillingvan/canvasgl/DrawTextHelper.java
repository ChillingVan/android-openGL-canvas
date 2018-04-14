package com.chillingvan.canvasgl;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Chilling on 2018/4/14.
 */
public class DrawTextHelper {

    private int width;
    private int height;
    private Bitmap bitmapBoard;
    private Bitmap bitmapBoardCached;
    private Canvas canvas;

    private ExecutorService executors = Executors.newSingleThreadExecutor();
    // If textDrawee is not drawing, then it is available.
    private volatile boolean isAvailable = true;
    private Lock lock = new ReentrantLock();

    public void init(int width, int height) {
        if (this.width != width || this.height != height) {
            this.width = width;
            this.height = height;
            bitmapBoard = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmapBoardCached = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmapBoard);
        }
    }

    /**
     * This must be in the same thread as {@link #getOutputBitmap()}
     */
    public void draw(final TextDrawee textDrawee) {
        if (canvas == null) {
            throw new IllegalStateException("DrawTextHelper has not init.");
        }
        if (!isAvailable) {
            return;
        }
        swap();
        executors.execute(new Runnable() {
            @Override
            public void run() {
                lock.lock();
                isAvailable = false;
                textDrawee.draw(canvas);
                isAvailable = true;
                lock.unlock();
            }
        });
    }

    /**
     * Swap bitmapBoard and bitmapBoardCached.
     */
    private void swap() {
        lock.lock();
        Bitmap temp = bitmapBoardCached;
        bitmapBoardCached = bitmapBoard;
        bitmapBoard = temp;
        canvas.setBitmap(bitmapBoard);
        lock.unlock();
    }

    public interface TextDrawee {
        void draw(Canvas androidCanvas);
    }

    public Bitmap getOutputBitmap() {
        return bitmapBoardCached;
    }

}
