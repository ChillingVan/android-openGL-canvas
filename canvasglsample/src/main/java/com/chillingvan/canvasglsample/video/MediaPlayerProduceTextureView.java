package com.chillingvan.canvasglsample.video;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.glcanvas.RawTexture;
import com.chillingvan.canvasgl.glview.texture.GLMultiTexProducerView;
import com.chillingvan.canvasgl.glview.texture.GLTexture;
import com.chillingvan.canvasgl.textureFilter.BasicTextureFilter;
import com.chillingvan.canvasgl.textureFilter.TextureFilter;

import java.util.List;

/**
 * Created by Chilling on 2017/12/16.
 */

public class MediaPlayerProduceTextureView extends GLMultiTexProducerView {

    private TextureFilter textureFilter = new BasicTextureFilter();

    public MediaPlayerProduceTextureView(Context context) {
        super(context);
    }

    public MediaPlayerProduceTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MediaPlayerProduceTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onGLDraw(ICanvasGL canvas, List<GLTexture> producedTextures, List<GLTexture> consumedTextures) {
        GLTexture glTexture = producedTextures.get(0);
        RawTexture producedRawTexture = glTexture.getRawTexture();
        SurfaceTexture surfaceTexture = glTexture.getSurfaceTexture();
        producedRawTexture.setIsFlippedVertically(true);
        canvas.drawSurfaceTexture(producedRawTexture, surfaceTexture, 0, 0, producedRawTexture.getWidth(), producedRawTexture.getHeight(), textureFilter);
    }

    @Override
    protected void init() {
        super.init();
    }

    public void setTextureFilter(TextureFilter textureFilter) {
        this.textureFilter = textureFilter;
    }

}
