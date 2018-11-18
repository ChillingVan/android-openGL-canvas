package com.chillingvan.canvasgl.matrix;

/**
 * Created by Chilling on 2018/11/17.
 *
 * The output is MVP matrix of OpenGL, which is used to calculate gl_position.
 * gl_position = MVP * [x,y,z,w]
 */
public interface IBitmapMatrix {
    float[] obtainResultMatrix(int viewportW, int viewportH, float x, float y, float drawW, float drawH);
}
