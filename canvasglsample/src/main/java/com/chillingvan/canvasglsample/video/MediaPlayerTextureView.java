package com.chillingvan.canvasglsample.video;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.glcanvas.RawTexture;
import com.chillingvan.canvasgl.glview.texture.GLSurfaceTextureProducerView;
import com.chillingvan.canvasgl.glview.texture.GLTexture;
import com.chillingvan.canvasgl.textureFilter.BasicTextureFilter;
import com.chillingvan.canvasgl.textureFilter.TextureFilter;

import androidx.annotation.Nullable;

/**
 * Created by Chilling on 2017/12/16.
 */

public class MediaPlayerTextureView extends GLSurfaceTextureProducerView {

    private TextureFilter textureFilter = new BasicTextureFilter();

    public MediaPlayerTextureView(Context context) {
        super(context);
    }

    public MediaPlayerTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MediaPlayerTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        super.init();
    }

    public void setTextureFilter(TextureFilter textureFilter) {
        this.textureFilter = textureFilter;
    }

    @Override
    protected void onGLDraw(ICanvasGL canvas, GLTexture producedGLTexture, @Nullable GLTexture outsideGLTexture) {
        super.onGLDraw(canvas, producedGLTexture, outsideGLTexture);
        RawTexture producedRawTexture = producedGLTexture.getRawTexture();
        SurfaceTexture producedSurfaceTexture = producedGLTexture.getSurfaceTexture();
        producedRawTexture.setIsFlippedVertically(true);
        producedRawTexture.setIsFlippedHorizontally(true);
        canvas.drawSurfaceTexture(producedRawTexture, producedSurfaceTexture, 0, 0, producedRawTexture.getWidth(), producedRawTexture.getHeight(), textureFilter);
    }
}
