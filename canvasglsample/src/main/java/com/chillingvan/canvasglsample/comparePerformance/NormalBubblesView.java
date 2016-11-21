package com.chillingvan.canvasglsample.comparePerformance;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.chillingvan.canvasglsample.animation.bubble.Bubble;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import static com.chillingvan.canvasglsample.animation.AnimGLView.INTERNVAL_TIME_MS;
import static com.chillingvan.canvasglsample.comparePerformance.GLBubblesView.createBubble;

/**
 * Created by Chilling on 2016/11/19.
 */

public class NormalBubblesView extends View {

    private Queue<Bubble> bubbles = new LinkedList<>();
    private RefreshTimer refreshTimer = new RefreshTimer();
    private Bitmap bitmap;
    private long last;
    private int cnt;
    private boolean isAdd;

    public NormalBubblesView(Context context) {
        super(context);
        init();
    }

    public NormalBubblesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NormalBubblesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    private void init() {
        refreshTimer.init(new Runnable() {
            @Override
            public void run() {
                doDraw();
            }
        }, INTERNVAL_TIME_MS);
    }


    private void doDraw() {
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        long now = System.currentTimeMillis();
        if (last != 0) {
            doOnDraw(canvas, bubbles, this, bitmap, INTERNVAL_TIME_MS);
        }
        last = now;
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
            create(bubbles, view, bitmap);
            isAdd = false;
        }
    }

    public static void create(Queue<Bubble> bubbles, View view, Bitmap bitmap) {
        if (bubbles.size() < 60) {
            for (int i = 0; i < 3; i++) {
                bubbles.add(createBubble(view, bitmap));
            }
        }
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

    public void setAdd(boolean add) {
        isAdd = add;
    }
}
