package com.chillingvan.canvasgl.matrix;

/**
 * Created by Chilling on 2018/11/17.
 */
public interface IBitmapMatrix {
    float[] obtainResultMatrix(int viewportW, int viewportH, float x, float y, float drawW, float drawH);
}
