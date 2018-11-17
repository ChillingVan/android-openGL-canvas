package com.chillingvan.canvasgl.matrix;

import android.opengl.Matrix;

import java.util.Arrays;

/**
 * Created by Chilling on 2018/11/17.
 */
public abstract class BaseBitmapMatrix implements IBitmapMatrix {

    public static final int TRANSLATE_X = 0;
    public static final int TRANSLATE_Y = 1;
    public static final int SCALE_X = 2;
    public static final int SCALE_Y = 3;
    public static final int ROTATE_X = 4;
    public static final int ROTATE_Y = 5;
    public static final int ROTATE_Z = 6;
    public static final int MATRIX_SIZE = 16;
    public final static float NEAR = 1;
    public final static float FAR = 10; // -10
    public final static float EYEZ = 5;
    public final static float Z_RATIO = (FAR + NEAR) / 2 / NEAR; // The scale ratio when the picture moved to the middle of the perspective projection.
    protected float[] transform = new float[7];
    protected float[] tempMultiplyMatrix4 = new float[MATRIX_SIZE];
    protected float[] mViewMatrix = new float[MATRIX_SIZE];
    protected float[] mProjectionMatrix = new float[MATRIX_SIZE];
    protected float[] mModelMatrix = new float[MATRIX_SIZE];
    protected float[] viewProjectionMatrix = new float[MATRIX_SIZE];
    protected float[] mvp = new float[MATRIX_SIZE];

    public void reset() {
        Matrix.setIdentityM(mViewMatrix, 0);
        Matrix.setIdentityM(mProjectionMatrix, 0);
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.setIdentityM(viewProjectionMatrix, 0);
        Matrix.setIdentityM(mvp, 0);

        Matrix.setIdentityM(tempMultiplyMatrix4, 0);
        Arrays.fill(transform, 0);
        transform[SCALE_X] = 1;
        transform[SCALE_Y] = 1;
    }
}
