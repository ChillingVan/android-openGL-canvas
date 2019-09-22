package com.chillingvan.canvasglsample.testcase.testMatrix;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.glview.texture.GLTextureView;
import com.chillingvan.canvasgl.textureFilter.ContrastFilter;
import com.chillingvan.canvasglsample.R;

/**
 * Created by Chilling on 2019/8/17.
 */
public class TestMatrixTextureView extends GLTextureView {

    private Bitmap baboon;

    public TestMatrixTextureView(Context context) {
        super(context);
    }

    public TestMatrixTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TestMatrixTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        super.init();
        baboon = BitmapFactory.decodeResource(getResources(), R.drawable.baboon);
        setOpaque(false);
        setRenderBackgroundColor(0xFFFFFFFF);
    }

    @Override
    protected void onGLDraw(ICanvasGL canvas) {
        drawBitmapOrigin(canvas);
        drawBitmapTranslate(canvas);
        drawBitmapTranslateY(canvas);
        drawBitmapTranslateRotate(canvas);
        drawBitmapTranslateRotateY(canvas);
        drawBitmapTranslateRotateZ(canvas);
        drawBitmapScale(canvas);
//        drawBitmapScaleLarge(canvas);
        drawBitmapWithMatrix(canvas);
    }

    private void drawBitmapOrigin(ICanvasGL canvas) {
        ICanvasGL.BitmapMatrix matrix = new ICanvasGL.BitmapMatrix();
        canvas.drawBitmap(baboon, matrix);
    }

    private void drawBitmapScale(ICanvasGL canvas) {
        ICanvasGL.BitmapMatrix matrix = new ICanvasGL.BitmapMatrix();
        matrix.translate(690, 300);
        matrix.scale(2, 1.4f);
        canvas.drawBitmap(baboon, matrix, new ContrastFilter(1));
    }


    private void drawBitmapTranslate(ICanvasGL canvas) {
        ICanvasGL.BitmapMatrix matrix = new ICanvasGL.BitmapMatrix();
        matrix.translate(690, 0);
        canvas.drawBitmap(baboon, matrix, new ContrastFilter(1.5f));
    }

    private void drawBitmapTranslateY(ICanvasGL canvas) {
        ICanvasGL.BitmapMatrix matrix = new ICanvasGL.BitmapMatrix();
        matrix.translate(0, 800);
        canvas.drawBitmap(baboon, matrix, new ContrastFilter(2));
    }

    private void drawBitmapTranslateRotate(ICanvasGL canvas) {
        ICanvasGL.BitmapMatrix matrix = new ICanvasGL.BitmapMatrix();
        matrix.translate(250, 0);
        matrix.rotateX(45);
        canvas.drawBitmap(baboon, matrix, new ContrastFilter(2.5f));
    }

    private void drawBitmapTranslateRotateY(ICanvasGL canvas) {
        ICanvasGL.BitmapMatrix matrix = new ICanvasGL.BitmapMatrix();
        matrix.translate(350, 550);
        matrix.rotateY(34);
        canvas.drawBitmap(baboon, matrix, new ContrastFilter(3));
    }

    private void drawBitmapTranslateRotateZ(ICanvasGL canvas) {
        ICanvasGL.BitmapMatrix matrix = new ICanvasGL.BitmapMatrix();
        matrix.translate(400, 700);
        matrix.rotateZ(65);
        canvas.drawBitmap(baboon, matrix, new ContrastFilter(3.5f));
    }

    private void drawBitmapScaleLarge(ICanvasGL canvas) {
        ICanvasGL.BitmapMatrix matrix = new ICanvasGL.BitmapMatrix();
        matrix.scale(15, 4);
        matrix.translate(-500, 180);
        canvas.drawBitmap(baboon, matrix);
    }

    private void drawBitmapWithMatrix(ICanvasGL canvas) {
        ICanvasGL.BitmapMatrix matrix = new ICanvasGL.BitmapMatrix();
        matrix.scale(1.3f, 1.6f);
        matrix.rotateX(34);
        matrix.rotateY(64);
        matrix.rotateZ(30);
        matrix.translate(100, 850);
        canvas.drawBitmap(baboon, matrix, new ContrastFilter(4));
    }
}
