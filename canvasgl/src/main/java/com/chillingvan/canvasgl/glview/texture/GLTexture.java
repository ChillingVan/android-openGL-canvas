package com.chillingvan.canvasgl.glview.texture;

import android.graphics.SurfaceTexture;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.glcanvas.RawTexture;

/**
 * Created by Chilling on 2018/5/19.
 */
public class GLTexture {

    private RawTexture rawTexture;
    private SurfaceTexture surfaceTexture;

    public GLTexture(RawTexture outsideTexture, SurfaceTexture outsideSurfaceTexture) {
        this.rawTexture = outsideTexture;
        this.surfaceTexture = outsideSurfaceTexture;
    }

    public RawTexture getRawTexture() {
        return rawTexture;
    }

    public SurfaceTexture getSurfaceTexture() {
        return surfaceTexture;
    }

    public static GLTexture createRaw(int width, int height, boolean opaque, int target, ICanvasGL canvasGL) {
        RawTexture rawTexture = new RawTexture(width, height, false, target);
        if (!rawTexture.isLoaded()) {
            rawTexture.prepare(canvasGL.getGlCanvas());
        }
        SurfaceTexture surfaceTexture = new SurfaceTexture(rawTexture.getId());
        return new GLTexture(rawTexture, surfaceTexture);
    }

}
