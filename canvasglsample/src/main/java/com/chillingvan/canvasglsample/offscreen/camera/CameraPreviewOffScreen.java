package com.chillingvan.canvasglsample.offscreen.camera;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.support.annotation.Nullable;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.OffScreenCanvas;
import com.chillingvan.canvasgl.glcanvas.BasicTexture;
import com.chillingvan.canvasgl.glcanvas.RawTexture;

/**
 * Created by Chilling on 2016/12/19.
 */

public class CameraPreviewOffScreen extends OffScreenCanvas {

    public CameraPreviewOffScreen(int width, int height) {
        super(width, height);
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!! important
        setProducedTextureTarget(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
    }

    @Override
    protected void onGLDraw(ICanvasGL canvas, SurfaceTexture producedSurfaceTexture, RawTexture producedRawTexture, @Nullable SurfaceTexture outsideSharedSurfaceTexture, @Nullable BasicTexture outsideSharedTexture) {
        canvas.drawSurfaceTexture(producedRawTexture, producedSurfaceTexture, 0, 0, producedRawTexture.getWidth(), producedRawTexture.getHeight());
    }

}
