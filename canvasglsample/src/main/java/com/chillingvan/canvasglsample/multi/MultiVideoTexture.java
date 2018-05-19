package com.chillingvan.canvasglsample.multi;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.glcanvas.RawTexture;
import com.chillingvan.canvasgl.glview.texture.GLMultiTexProducerView;
import com.chillingvan.canvasgl.glview.texture.GLTexture;
import com.chillingvan.canvasgl.glview.texture.gles.EglContextWrapper;
import com.chillingvan.canvasgl.glview.texture.gles.GLThread;

import java.util.List;

/**
 * Created by Chilling on 2018/5/19.
 */
public class MultiVideoTexture extends GLMultiTexProducerView {

    public MultiVideoTexture(Context context) {
        super(context);
    }

    public MultiVideoTexture(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiVideoTexture(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        super.onSurfaceTextureAvailable(surface, width, height);
        if (mGLThread == null) {
            setSharedEglContext(EglContextWrapper.EGL_NO_CONTEXT_WRAPPER);
        }
    }

    @Override
    protected int getInitialTexCount() {
        return 3;
    }

    @Override
    protected int getRenderMode() {
        return GLThread.RENDERMODE_CONTINUOUSLY;
    }

    @Override
    protected void onGLDraw(ICanvasGL canvas, List<GLTexture> producedTextures, List<GLTexture> consumedTextures) {
        int size = producedTextures.size();
        for (int i = 0; i < producedTextures.size(); i++) {
            GLTexture texture = producedTextures.get(i);
            int left = getWidth() * i / size;
            RawTexture rawTexture = texture.getRawTexture();
            rawTexture.setIsFlippedVertically(true);
            canvas.drawSurfaceTexture(rawTexture, texture.getSurfaceTexture(), left, 0, left + getWidth()/size, getHeight());
        }
    }
}
