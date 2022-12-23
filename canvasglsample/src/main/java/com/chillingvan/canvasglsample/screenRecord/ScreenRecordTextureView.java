package com.chillingvan.canvasglsample.screenRecord;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.androidCanvas.IAndroidCanvasHelper;
import com.chillingvan.canvasgl.glview.texture.GLSurfaceTextureProducerView;
import com.chillingvan.canvasgl.glview.texture.GLTexture;
import com.chillingvan.canvasglsample.util.ScreenUtil;

import androidx.annotation.Nullable;

/**
 * Created by Chilling on 2020/3/7.
 */
public class ScreenRecordTextureView extends GLSurfaceTextureProducerView {

    private IAndroidCanvasHelper drawTextHelper = IAndroidCanvasHelper.Factory.createAndroidCanvasHelper(IAndroidCanvasHelper.MODE.MODE_ASYNC);
    private IAndroidCanvasHelper.CanvasPainter mCanvasPainter;
    private Paint textPaint;
    private int drawCnt = 0;

    public ScreenRecordTextureView(Context context) {
        super(context);
    }

    public ScreenRecordTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScreenRecordTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        super.init();
        textPaint = new Paint();
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(ScreenUtil.dpToPx(getContext(), 15));
        mCanvasPainter = new IAndroidCanvasHelper.CanvasPainter() {
            @Override
            public void draw(Canvas androidCanvas, Bitmap drawBitmap) {
                drawCnt++;
                androidCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                androidCanvas.drawText("Cnt: " + drawCnt, 45, 45, textPaint);
            }
        };
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        super.onSurfaceChanged(width, height);
        drawTextHelper.init(width, height);
    }

    @Override
    protected void onGLDraw(ICanvasGL canvas, GLTexture producedGLTexture, @Nullable GLTexture outsideGLTexture) {
        super.onGLDraw(canvas, producedGLTexture, outsideGLTexture);
        canvas.drawSurfaceTexture(producedGLTexture.getRawTexture(), producedGLTexture.getSurfaceTexture(), 0, 0, getWidth(), getHeight());
        drawTextHelper.draw(mCanvasPainter);
        Bitmap outputBitmap = drawTextHelper.getOutputBitmap();
        canvas.invalidateTextureContent(outputBitmap);
        canvas.drawBitmap(outputBitmap, 0, 0);
    }
}
