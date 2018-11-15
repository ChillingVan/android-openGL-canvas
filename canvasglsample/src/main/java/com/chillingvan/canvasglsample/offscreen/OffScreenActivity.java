/*
 *
 *  *
 *  *  * Copyright (C) 2016 ChillingVan
 *  *  *
 *  *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  * you may not use this file except in compliance with the License.
 *  *  * You may obtain a copy of the License at
 *  *  *
 *  *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *  *
 *  *  * Unless required by applicable law or agreed to in writing, software
 *  *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  * See the License for the specific language governing permissions and
 *  *  * limitations under the License.
 *  *
 *
 */

package com.chillingvan.canvasglsample.offscreen;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.OffScreenCanvas;
import com.chillingvan.canvasgl.glcanvas.BasicTexture;
import com.chillingvan.canvasgl.glcanvas.GLPaint;
import com.chillingvan.canvasgl.glcanvas.RawTexture;
import com.chillingvan.canvasgl.glview.GLView;
import com.chillingvan.canvasgl.glview.texture.GLSurfaceTextureProducerView;
import com.chillingvan.canvasgl.glview.texture.gles.EglContextWrapper;
import com.chillingvan.canvasgl.glview.texture.gles.GLThread;
import com.chillingvan.canvasglsample.R;

public class OffScreenActivity extends Activity {

    private ImageView imageView;
    private OffScreenCanvas offScreenCanvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_off_screen);

        imageView = (ImageView) findViewById(R.id.off_screen_img_v);

        offScreenCanvas = new OffScreenCanvas(400, 400) {
            @Override
            protected void onGLDraw(ICanvasGL canvas, SurfaceTexture producedSurfaceTexture, RawTexture producedRawTexture, SurfaceTexture outsideSharedSurfaceTexture, BasicTexture outsideSharedTexture) {
                canvas.beginRenderTarget(producedRawTexture);
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lenna);
                canvas.drawBitmap(bitmap, 0, 0);
                canvas.endRenderTarget();
            }
        };

        offScreenCanvas.setOnCreateGLContextListener(new GLThread.OnCreateGLContextListener() {
            @Override
            public void onCreate(final EglContextWrapper eglContext) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final OffScreenCanvas secondOffScreenCanvas = new OffScreenCanvas(400, 300, eglContext) {
                            @Override
                            protected void onGLDraw(ICanvasGL canvas, SurfaceTexture producedSurfaceTexture, RawTexture producedRawTexture, @Nullable SurfaceTexture outsideSharedSurfaceTexture, @Nullable BasicTexture outsideSharedTexture) {
                                if (outsideSharedTexture != null) {
                                    canvas.drawSurfaceTexture(outsideSharedTexture, null, 0, 0, outsideSharedTexture.getWidth(), outsideSharedTexture.getHeight());
                                }

                                GLPaint paint = new GLPaint();
                                paint.setColor(Color.RED);
                                paint.setStyle(Paint.Style.FILL);
                                canvas.drawCircle(40, 40, 40, paint);
                            }
                        };

                        offScreenCanvas.setOnSurfaceTextureSet(new GLSurfaceTextureProducerView.OnSurfaceTextureSet() {
                            @Override
                            public void onSet(SurfaceTexture surfaceTexture, RawTexture surfaceTextureRelatedTexture) {
                                secondOffScreenCanvas.setSharedTexture(surfaceTextureRelatedTexture, surfaceTexture);
                                secondOffScreenCanvas.start();
                                secondOffScreenCanvas.getDrawingBitmap(new Rect(0, 0, 300, 300), new GLView.GetDrawingCacheCallback() {
                                    @Override
                                    public void onFetch(final Bitmap bitmap) {
                                        imageView.setImageBitmap(bitmap);
                                    }
                                });
                            }
                        });
                    }
                });

            }
        });


        offScreenCanvas.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        offScreenCanvas.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
