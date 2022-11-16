package com.chillingvan.canvasglsample.testcase.testMatrix;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.androidCanvas.IAndroidCanvasHelper;
import com.chillingvan.canvasgl.glcanvas.RawTexture;
import com.chillingvan.canvasgl.glview.texture.GLSurfaceTextureProducerView;
import com.chillingvan.canvasgl.glview.texture.GLTexture;
import com.chillingvan.canvasgl.textureFilter.BasicTextureFilter;
import com.chillingvan.canvasglsample.util.ScreenUtil;

import androidx.annotation.Nullable;

/**
 * Created by Chilling on 2019/11/23.
 */
public class TestMatrixSurfaceTextureView extends GLSurfaceTextureProducerView {
    private int mMode;
    private IAndroidCanvasHelper androidCanvasHelper = IAndroidCanvasHelper.Factory
            .createAndroidCanvasHelper(IAndroidCanvasHelper.MODE.MODE_ASYNC);
    private Paint textPaint = new Paint();
    private IAndroidCanvasHelper.CanvasPainter canvasPainter = new IAndroidCanvasHelper.CanvasPainter() {
        @Override
        public void draw(Canvas androidCanvas, Bitmap drawBitmap) {
            androidCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            androidCanvas.drawText("Current Mode = " + mMode, 50, 50, textPaint);
        }
    };
    private BasicTextureFilter textureFilter = new BasicTextureFilter();

    {
        textPaint.setColor(Color.BLUE);
        textPaint.setTextSize(ScreenUtil.dpToPx(getContext(), 14));
    }

    public TestMatrixSurfaceTextureView(Context context) {
        super(context);
    }

    public TestMatrixSurfaceTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TestMatrixSurfaceTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        super.init();
        setOpaque(false);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        super.onSurfaceChanged(width, height);
        androidCanvasHelper.init(width, height);
    }

    @Override
    protected void onGLDraw(ICanvasGL canvas, GLTexture producedGLTexture, @Nullable GLTexture outsideGLTexture) {
        super.onGLDraw(canvas, producedGLTexture, outsideGLTexture);
        RawTexture producedRawTexture = producedGLTexture.getRawTexture();
        SurfaceTexture producedSurfaceTexture = producedGLTexture.getSurfaceTexture();
        switch (mMode) {
            case 0:
                drawOrigin(canvas, producedRawTexture, producedSurfaceTexture);
                break;
            case 1:
                drawTranslate(canvas, producedRawTexture, producedSurfaceTexture);
                break;
            case 2:
                drawTranslateY(canvas, producedRawTexture, producedSurfaceTexture);
                break;
            case 3:
                drawTranslateRotate(canvas, producedRawTexture, producedSurfaceTexture);
                break;
            case 4:
                drawTranslateRotateY(canvas, producedRawTexture, producedSurfaceTexture);
                break;
            case 5:
                drawTranslateRotateZ(canvas, producedRawTexture, producedSurfaceTexture);
                break;
            case 6:
                drawScale(canvas, producedRawTexture, producedSurfaceTexture);
                break;
            case 7:
                drawWithMatrix(canvas, producedRawTexture, producedSurfaceTexture);
                break;
            case 8:
                drawScaleLarge(canvas, producedRawTexture, producedSurfaceTexture);
                break;
        }
        androidCanvasHelper.draw(canvasPainter);
        Bitmap outputBitmap = androidCanvasHelper.getOutputBitmap();
        canvas.invalidateTextureContent(outputBitmap);
        canvas.drawBitmap(outputBitmap, 0, getHeight() - 100);
    }

    public void setMode() {
        mMode = (mMode + 1) % 9;
    }

    private void drawOrigin(ICanvasGL canvas, RawTexture producedRawTexture, SurfaceTexture producedSurfaceTexture) {
        ICanvasGL.BitmapMatrix matrix = new ICanvasGL.BitmapMatrix();
        canvas.drawSurfaceTexture(producedRawTexture, producedSurfaceTexture, 0, 0, 256, 256, matrix, textureFilter);
    }

    private void drawScale(ICanvasGL canvas, RawTexture producedRawTexture, SurfaceTexture producedSurfaceTexture) {
        ICanvasGL.BitmapMatrix matrix = new ICanvasGL.BitmapMatrix();
        matrix.scale(2, 1.4f);
        canvas.drawSurfaceTexture(producedRawTexture, producedSurfaceTexture, 0, 0, 256, 256, matrix, textureFilter);
    }


    private void drawTranslate(ICanvasGL canvas, RawTexture producedRawTexture, SurfaceTexture producedSurfaceTexture) {
        ICanvasGL.BitmapMatrix matrix = new ICanvasGL.BitmapMatrix();
        matrix.translate(110, 0);
        canvas.drawSurfaceTexture(producedRawTexture, producedSurfaceTexture, 0, 0, 256, 256, matrix, textureFilter);
    }

    private void drawTranslateY(ICanvasGL canvas, RawTexture producedRawTexture, SurfaceTexture producedSurfaceTexture) {
        ICanvasGL.BitmapMatrix matrix = new ICanvasGL.BitmapMatrix();
        matrix.translate(0, 200);
        canvas.drawSurfaceTexture(producedRawTexture, producedSurfaceTexture, 0, 0, 256, 256, matrix, textureFilter);
    }

    private void drawTranslateRotate(ICanvasGL canvas, RawTexture producedRawTexture, SurfaceTexture producedSurfaceTexture) {
        ICanvasGL.BitmapMatrix matrix = new ICanvasGL.BitmapMatrix();
        matrix.translate(110, 0);
        matrix.rotateX(45);
        canvas.drawSurfaceTexture(producedRawTexture, producedSurfaceTexture, 0, 0, 256, 256, matrix, textureFilter);
    }

    private void drawTranslateRotateY(ICanvasGL canvas, RawTexture producedRawTexture, SurfaceTexture producedSurfaceTexture) {
        ICanvasGL.BitmapMatrix matrix = new ICanvasGL.BitmapMatrix();
        matrix.translate(100, 150);
        matrix.rotateY(34);
        canvas.drawSurfaceTexture(producedRawTexture, producedSurfaceTexture, 0, 0, 256, 256, matrix, textureFilter);
    }

    private void drawTranslateRotateZ(ICanvasGL canvas, RawTexture producedRawTexture, SurfaceTexture producedSurfaceTexture) {
        ICanvasGL.BitmapMatrix matrix = new ICanvasGL.BitmapMatrix();
        matrix.translate(100, 110);
        matrix.rotateZ(65);
        canvas.drawSurfaceTexture(producedRawTexture, producedSurfaceTexture, 0, 0, 256, 256, matrix, textureFilter);
    }

    private void drawWithMatrix(ICanvasGL canvas, RawTexture producedRawTexture, SurfaceTexture producedSurfaceTexture) {
        ICanvasGL.BitmapMatrix matrix = new ICanvasGL.BitmapMatrix();
        matrix.scale(1.3f, 1.6f);
        matrix.rotateX(34);
        matrix.rotateY(64);
        matrix.rotateZ(30);
        matrix.translate(100, 150);
        canvas.drawSurfaceTexture(producedRawTexture, producedSurfaceTexture, 0, 0, 256, 256, matrix, textureFilter);
    }

    private void drawScaleLarge(ICanvasGL canvas, RawTexture producedRawTexture, SurfaceTexture producedSurfaceTexture) {
        ICanvasGL.BitmapMatrix matrix = new ICanvasGL.BitmapMatrix();
        matrix.scale(15, 4);
        canvas.drawSurfaceTexture(producedRawTexture, producedSurfaceTexture, 0, 0, 256, 256, matrix, textureFilter);
    }
}
