package com.chillingvan.canvasglsample.comparePerformance;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.glview.GLContinuousView;
import com.chillingvan.canvasglsample.animation.bubble.Bubble;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import static com.chillingvan.canvasglsample.animation.AnimGLView.INTERNVAL_TIME_MS;

/**
 * Created by Chilling on 2016/11/19.
 */

public class GLBubblesView extends GLContinuousView {

    private Queue<Bubble> bubbles = new LinkedList<>();
    private Bitmap bitmap;
    private long last;
    private int cnt;
    private boolean isAdd;


    public GLBubblesView(Context context) {
        super(context);
    }

    public GLBubblesView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init() {
        super.init();
    }


    @Override
    protected void onGLDraw(ICanvasGL canvas) {
        long now = System.currentTimeMillis();
        if (last == 0) {
            last = now;
            return;
        }
        for (Bubble bubble : bubbles) {
            bubble.glDraw(canvas);
            bubble.updatePosition((int) (INTERNVAL_TIME_MS));
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
            NormalBubblesView.create(bubbles, this, bitmap);
            isAdd = false;
        }
        last = now;
    }

    public static Bubble createBubble(View view, Bitmap bitmap) {
        float vx = 0;
        float vy = -0.3f;
        float vRotate = 1f;

        return new Bubble(new PointF(view.getWidth()/2, view.getHeight()), vx, vy, vRotate, bitmap, null);
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public int getCnt() {
        return cnt;
    }

    public void setAdd(boolean add) {
        isAdd = add;
    }

}
