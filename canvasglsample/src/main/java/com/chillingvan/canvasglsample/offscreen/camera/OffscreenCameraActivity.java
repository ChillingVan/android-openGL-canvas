package com.chillingvan.canvasglsample.offscreen.camera;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.chillingvan.canvasgl.glcanvas.RawTexture;
import com.chillingvan.canvasgl.glview.GLView;
import com.chillingvan.canvasgl.glview.texture.GLSurfaceTextureProducerView;
import com.chillingvan.canvasgl.glview.texture.gles.EglContextWrapper;
import com.chillingvan.canvasgl.glview.texture.gles.GLThread;
import com.chillingvan.canvasglsample.R;
import com.chillingvan.canvasglsample.textureView.CameraUtils;

import java.io.IOException;

public class OffscreenCameraActivity extends AppCompatActivity {

    private Camera mCamera;
    private CameraPreviewOffScreen cameraPreviewOffScreen;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_offscreen);
        imageView = (ImageView) findViewById(R.id.off_screen_img_v);
    }

    private void initCameraTexture() {
        cameraPreviewOffScreen = new CameraPreviewOffScreen(400, 400);
        cameraPreviewOffScreen.start();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraPreviewOffScreen.getDrawingBitmap(new Rect(0, 0, v.getWidth(), v.getHeight()), new GLView.GetDrawingCacheCallback() {
                    @Override
                    public void onFetch(Bitmap bitmap) {
                        imageView.setImageBitmap(bitmap);
                    }
                });

            }
        });

        cameraPreviewOffScreen.setOnCreateGLContextListener(new GLThread.OnCreateGLContextListener() {
            @Override
            public void onCreate(EglContextWrapper eglContext) {
            }
        });
        cameraPreviewOffScreen.setOnSurfaceTextureSet(new GLSurfaceTextureProducerView.OnSurfaceTextureSet() {
            @Override
            public void onSet(SurfaceTexture surfaceTexture, RawTexture surfaceTextureRelatedTexture) {
                surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                    @Override
                    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                        cameraPreviewOffScreen.requestRender();
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
        openCamera();
        initCameraTexture();
        cameraPreviewOffScreen.onResume();
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
        releaseCamera();
        cameraPreviewOffScreen.onPause();
    }
}
