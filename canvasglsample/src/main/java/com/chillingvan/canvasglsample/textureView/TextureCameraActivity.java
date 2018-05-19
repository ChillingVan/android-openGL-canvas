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

package com.chillingvan.canvasglsample.textureView;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chillingvan.canvasgl.glview.texture.GLTexture;
import com.chillingvan.canvasgl.util.Loggers;
import com.chillingvan.canvasgl.glcanvas.RawTexture;
import com.chillingvan.canvasgl.glview.GLView;
import com.chillingvan.canvasgl.glview.texture.GLSurfaceTextureProducerView;
import com.chillingvan.canvasgl.glview.texture.gles.EglContextWrapper;
import com.chillingvan.canvasgl.glview.texture.gles.GLThread;
import com.chillingvan.canvasgl.textureFilter.PixelationFilter;
import com.chillingvan.canvasglsample.R;

import java.io.IOException;

public class TextureCameraActivity extends AppCompatActivity {

    private Camera mCamera;
    private CameraPreviewTextureView cameraTextureView;
    private PreviewConsumerTextureView previewConsumerTextureView;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_texture_canvas);

        imageView = (ImageView) findViewById(R.id.image_v);
    }

    private void initCameraTexture() {
        cameraTextureView = (CameraPreviewTextureView) findViewById(R.id.camera_texture);
        previewConsumerTextureView = (PreviewConsumerTextureView) findViewById(R.id.camera_texture2);
        cameraTextureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraTextureView.getDrawingBitmap(new Rect(0, 0, v.getWidth(), v.getHeight()), new GLView.GetDrawingCacheCallback() {
                    @Override
                    public void onFetch(Bitmap bitmap) {
                        imageView.setImageBitmap(bitmap);
                    }
                });

            }
        });
        previewConsumerTextureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewConsumerTextureView.getDrawingBitmap(new Rect(0, 0, v.getWidth(), v.getHeight()), new GLView.GetDrawingCacheCallback() {
                    @Override
                    public void onFetch(Bitmap bitmap) {
                        imageView.setImageBitmap(bitmap);
                    }
                });
            }
        });

        previewConsumerTextureView.setTextureFilter(new PixelationFilter(15));
        cameraTextureView.setOnCreateGLContextListener(new GLThread.OnCreateGLContextListener() {
            @Override
            public void onCreate(EglContextWrapper eglContext) {
                previewConsumerTextureView.setSharedEglContext(eglContext);
            }
        });
        cameraTextureView.setOnSurfaceTextureSet(new GLSurfaceTextureProducerView.OnSurfaceTextureSet() {
            @Override
            public void onSet(SurfaceTexture surfaceTexture, RawTexture surfaceTextureRelatedTexture) {
                Loggers.d("TextureCameraActivity", String.format("onSet: "));
                previewConsumerTextureView.addConsumeGLTexture(new GLTexture(surfaceTextureRelatedTexture, surfaceTexture));
                surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                    @Override
                    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                        cameraTextureView.requestRenderAndWait();
                        previewConsumerTextureView.requestRenderAndWait();
                    }
                });

                try {
                    mCamera.setPreviewTexture(surfaceTexture);
                } catch (IOException ioe) {
                    throw new RuntimeException(ioe);
                }
                mCamera.startPreview();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        Loggers.d("TextureCameraActivity", String.format("onResume: "));
        openCamera();
        initCameraTexture();
        cameraTextureView.onResume();
        previewConsumerTextureView.onResume();
    }

    private void openCamera() {
        Camera.CameraInfo info = new Camera.CameraInfo();

        // Try to find a front-facing camera (e.g. for videoconferencing).
        int numCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numCameras; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                mCamera = Camera.open(i);
                break;
            }
        }
        if (mCamera == null) {
            mCamera = Camera.open();    // opens first back-facing camera
        }

        Camera.Parameters parms = mCamera.getParameters();

        CameraUtils.choosePreviewSize(parms, 1280, 720);
    }


    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Loggers.d("TextureCameraActivity", String.format("onPause: "));
        releaseCamera();
        cameraTextureView.onPause();
        previewConsumerTextureView.onPause();
    }

    public void onClickChangeSize(View view) {
        if (cameraTextureView.getScaleY() < 1) {
            cameraTextureView.setScaleY(1.5f);
            previewConsumerTextureView.setScaleY(1.5f);
        } else {
            cameraTextureView.setScaleY(0.7f);
            previewConsumerTextureView.setScaleY(0.7f);
        }
    }

    public void onClickChangeLayoutSize(View view) {
        ViewGroup.LayoutParams layoutParams = cameraTextureView.getLayoutParams();
        ViewGroup.LayoutParams consumerLayoutParams = previewConsumerTextureView.getLayoutParams();
        if (layoutParams.height < 500) {
            layoutParams.height += 50;
            cameraTextureView.setLayoutParams(layoutParams);
            consumerLayoutParams.height += 50;
            previewConsumerTextureView.setLayoutParams(consumerLayoutParams);
        } else {
            layoutParams.height -= 50;
            cameraTextureView.setLayoutParams(layoutParams);
            consumerLayoutParams.height -= 50;
            previewConsumerTextureView.setLayoutParams(consumerLayoutParams);
        }
    }
}
