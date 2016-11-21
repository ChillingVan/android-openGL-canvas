package com.chillingvan.canvasglsample.comparePerformance;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.chillingvan.canvasglsample.animation.bubble.Bubble;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import static com.chillingvan.canvasglsample.animation.AnimGLView.INTERNVAL_TIME_MS;

/**
 * Created by Chilling on 2016/11/20.
 */

public class SurfaceBubblesView extends SurfaceView implements SurfaceHolder.Callback{

    private Queue<Bubble> bubbles = new LinkedList<>();
    private Bitmap bitmap;
    private final SurfaceHolder mHolder;
    private RefreshTimer refreshTimer = new RefreshTimer();
    private int cnt;
    private long last;
    private boolean isAdd;

    public SurfaceBubblesView(Context context) {
        super(context);
        mHolder = this.getHolder();
        init();
    }

    public SurfaceBubblesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHolder = this.getHolder();
        init();
    }

    public SurfaceBubblesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHolder = this.getHolder();
        init();
    }

    private void init() {
        setZOrderMediaOverlay(true);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mHolder.addCallback(this);
        refreshTimer.init(new Runnable() {

            @Override
            public void run() {
                doDraw();
            }
        }, INTERNVAL_TIME_MS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        refreshTimer.end();
    }

    private boolean doDraw() {
        Canvas canvas = mHolder.lockCanvas();
        if (canvas != null) {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

            long now = System.currentTimeMillis();
            if (last != 0) {
                doOnDraw(canvas, bubbles, this, bitmap, INTERNVAL_TIME_MS);
            }
            last = now;

            mHolder.unlockCanvasAndPost(canvas);
            return true;
        }
        return false;
    }

    public void onResume() {
        refreshTimer.run(INTERNVAL_TIME_MS);
    }

    public void onPause() {
        refreshTimer.stop();
    }

    public void destroy() {
        refreshTimer.end();
    }

    public int getCnt() {
        return cnt;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    private void doOnDraw(Canvas canvas, Queue<Bubble> bubbles, View view, Bitmap bitmap, long interval) {
        for (Bubble bubble : bubbles) {
            bubble.normalDraw(canvas);
            bubble.updatePosition((int) interval);
        }

        Iterator<Bubble> iterator = bubbles.iterator();
        while (iterator.hasNext()) {
            Bubble bubble = iterator.next();
            if (bubble.point.y < 0) {
                iterator.remove();
                cnt++;
            }
        }

        if (isAdd) {
            NormalBubblesView.create(bubbles, view, bitmap);
            isAdd = false;
        }
    }

    public void setAdd(boolean add) {
        isAdd = add;
    }
}
