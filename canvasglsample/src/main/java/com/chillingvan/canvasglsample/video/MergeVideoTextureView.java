package com.chillingvan.canvasglsample.video;

import android.content.Context;
import android.util.AttributeSet;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.glview.texture.GLMultiTexConsumerView;
import com.chillingvan.canvasgl.glview.texture.GLTexture;
import com.chillingvan.canvasgl.glview.texture.gles.GLThread;

import java.util.List;

/**
 * Only used to consume textures from others.
 */
public class MergeVideoTextureView extends GLMultiTexConsumerView {

    public MergeVideoTextureView(Context context) {
        super(context);
    }

    public MergeVideoTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MergeVideoTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected int getRenderMode() {
        return GLThread.RENDERMODE_CONTINUOUSLY;
    }

    @Override
    protected void onGLDraw(ICanvasGL canvas, List<GLTexture> consumedTextures) {
        int size = consumedTextures.size();
        for (int i = 0; i < consumedTextures.size(); i++) {
            GLTexture texture = consumedTextures.get(i);
            int left = getWidth() * i / size;
            canvas.drawSurfaceTexture(texture.getRawTexture(), texture.getSurfaceTexture(), left, 0, left + getWidth()/size, getHeight());
        }

    }
}
