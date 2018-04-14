package com.chillingvan.canvasglsample.text;

import android.graphics.PointF;

import com.chillingvan.canvasglsample.animation.bubble.MovableObj;

/**
 * Created by Chilling on 2018/4/14.
 */
public class Dannmaku extends MovableObj {
    private String text;
    private int color;
    private float length;

    public Dannmaku(PointF point, float vx) {
        super(point, vx, 0, 0, 0);
    }


    public String getText() {
        return text;
    }

    public int getColor() {
        return color;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public float getLength() {
        return length;
    }

    public float getRight() {
        return point.x + length;
    }

    public void reset(float x, float y, float vx) {
        point.x = x;
        point.y = y;
        super.reset(point, vx, 0, vRotate, collisionRadius);
        length = 0;
    }

    @Override
    public String toString() {
        return super.toString() + "Dannmaku{" +
                "text='" + text + '\'' +
                ", color=" + color +
                ", length=" + length +
                '}';
    }
}
