package com.chillingvan.canvasglsample.listSample;

import android.opengl.GLES11Ext;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.MultiTexOffScreenCanvas;
import com.chillingvan.canvasgl.glview.texture.GLTexture;

import java.util.List;

/**
 * Created by Chilling on 2019/6/30.
 */
public class ProviderOffScreenCanvas extends MultiTexOffScreenCanvas {

    {
        setProducedTextureTarget(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
    }

    public ProviderOffScreenCanvas(int width, int height) {
        super(width, height);
    }

    @Override
    protected void onGLDraw(ICanvasGL canvas, List<GLTexture> producedTextures, List<GLTexture> consumedTextures) {

    }
}
