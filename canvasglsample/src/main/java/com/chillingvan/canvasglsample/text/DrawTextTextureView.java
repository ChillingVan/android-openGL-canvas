package com.chillingvan.canvasglsample.text;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.androidCanvas.IAndroidCanvasHelper;
import com.chillingvan.canvasgl.glcanvas.BasicTexture;
import com.chillingvan.canvasgl.glcanvas.RawTexture;
import com.chillingvan.canvasgl.glview.texture.gles.GLThread;
import com.chillingvan.canvasglsample.util.ScreenUtil;
import com.chillingvan.canvasglsample.video.MediaPlayerTextureView;

import java.util.List;

/**
 * Created by Chilling on 2018/4/14.
 */
public class DrawTextTextureView extends MediaPlayerTextureView {
    private IAndroidCanvasHelper drawTextHelper = IAndroidCanvasHelper.Factory.createAndroidCanvasHelper(IAndroidCanvasHelper.MODE.MODE_ASYNC);
    private ObjectFactory<Dannmaku> dannmakuFactory;
    private Paint textPaint;
    private boolean isStart;
    private long frameDrawCnt;
    private long startTime;

    public DrawTextTextureView(Context context) {
        super(context);
    }

    public DrawTextTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawTextTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        super.onSurfaceChanged(width, height);
        dannmakuFactory  = new DannmakuFactory(width, height);
        dannmakuFactory.book(100);
        drawTextHelper.init(width, height);
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(ScreenUtil.dpToPx(getContext(), 15));
    }

    public void start() {
        isStart = true;
        startTime = SystemClock.elapsedRealtime();
    }

    @Override
    public void onPause() {
        super.onPause();
        isStart = false;
        frameDrawCnt = 0;
    }

    @Override
    protected void onGLDraw(ICanvasGL canvas, SurfaceTexture producedSurfaceTexture, RawTexture producedRawTexture, @Nullable SurfaceTexture sharedSurfaceTexture, @Nullable BasicTexture sharedTexture) {
        super.onGLDraw(canvas, producedSurfaceTexture, producedRawTexture, sharedSurfaceTexture, sharedTexture);
        if (isStart) {
            drawTextHelper.draw(new IAndroidCanvasHelper.CanvasPainter() {
                @Override
                public void draw(Canvas canvas) {
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    List<Dannmaku> availableItems = dannmakuFactory.getAvailableItems();
                    for (Dannmaku dannmaku : availableItems) {
                        if (dannmaku.getLength() == 0) {
                            float length = textPaint.measureText(dannmaku.getText());
                            dannmaku.setLength(length);
                        }
                        dannmaku.updatePosition(16);
                        textPaint.setColor(dannmaku.getColor());
                        canvas.drawText(dannmaku.getText(), dannmaku.point.x, dannmaku.point.y, textPaint);
                        if (shouldRecycle(dannmaku)) {
                            dannmakuFactory.release(dannmaku);
                            int bookingCnt = 1;
                            dannmakuFactory.book(bookingCnt);
                        }
                    }
                    frameDrawCnt++;

                }
            });
            Bitmap outputBitmap = drawTextHelper.getOutputBitmap();
            canvas.invalidateTextureContent(outputBitmap);
            canvas.drawBitmap(outputBitmap, 0, 0);
        }
    }

    public long getFrameRate() {
        long timeSpan = (SystemClock.elapsedRealtime() - startTime) / 1000;
        return timeSpan == 0 ? 0 : frameDrawCnt/ timeSpan;
    }

    private static boolean shouldRecycle(Dannmaku dannmaku) {
        if (dannmaku.getRight() < 0) {
            return true;
        }
        return false;
    }

    // Anim for dannmaku should be 50 frames.
    @Override
    protected int getRenderMode() {
        return GLThread.RENDERMODE_CONTINUOUSLY;
    }
}
