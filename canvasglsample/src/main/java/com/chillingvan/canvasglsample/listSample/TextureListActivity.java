package com.chillingvan.canvasglsample.listSample;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;

import com.chillingvan.canvasgl.glview.texture.GLMultiTexConsumerView;
import com.chillingvan.canvasgl.glview.texture.GLMultiTexProducerView;
import com.chillingvan.canvasgl.glview.texture.GLTexture;
import com.chillingvan.canvasgl.glview.texture.gles.EglContextWrapper;
import com.chillingvan.canvasgl.glview.texture.gles.GLThread;
import com.chillingvan.canvasglsample.R;
import com.chillingvan.canvasglsample.textureView.CameraUtils;
import com.chillingvan.canvasglsample.textureView.PreviewConsumerTextureView;
import com.chillingvan.canvasglsample.util.adapter.CommonRecyclerAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TextureListActivity extends AppCompatActivity {

    private PreviewConsumerTextureView currentConsumerTextureView;
    private Camera mCamera;
    private ProviderOffScreenCanvas offScreenCanvas;

    private GLTexture mProvidedTexture;
    private EglContextWrapper mEGLContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_texture_list);
    }

    private void initView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_texture);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

        List<TextureItemEntity> entityList = new ArrayList<>();
        TextureItemEntity.OnClickShowCallback onClickShowCallback = new TextureItemEntity.OnClickShowCallback() {
            @Override
            public void onShow(GLMultiTexConsumerView textureView) {
                if (currentConsumerTextureView != null) {
                    currentConsumerTextureView.onPause();
                    currentConsumerTextureView.clearConsumedTextures();
                }
                currentConsumerTextureView = (PreviewConsumerTextureView) textureView;
                currentConsumerTextureView.setSharedEglContext(mEGLContext);
                currentConsumerTextureView.addConsumeGLTexture(new GLTexture(mProvidedTexture.getRawTexture(), mProvidedTexture.getSurfaceTexture()));
                currentConsumerTextureView.onResume();
            }
        };
        entityList.add(new TextureItemEntity(onClickShowCallback));
        entityList.add(new TextureItemEntity(onClickShowCallback));
        entityList.add(new TextureItemEntity(onClickShowCallback));
        recyclerView.setAdapter(new CommonRecyclerAdapter<>(entityList));

        offScreenCanvas = new ProviderOffScreenCanvas(1280, 720);
        offScreenCanvas.setOnCreateGLContextListener(new GLThread.OnCreateGLContextListener() {

            @Override
            public void onCreate(EglContextWrapper eglContext) {
                mEGLContext = eglContext;
            }
        });

        offScreenCanvas.setSurfaceTextureCreatedListener(new GLMultiTexProducerView.SurfaceTextureCreatedListener() {
            @Override
            public void onCreated(List<GLTexture> producedTextureList) {
                mProvidedTexture = producedTextureList.get(0);
                SurfaceTexture surfaceTexture = mProvidedTexture.getSurfaceTexture();

                surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                    @Override
                    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                        offScreenCanvas.requestRender();
                        if (currentConsumerTextureView != null) {
                            currentConsumerTextureView.requestRenderAndWait();
                        }
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
        offScreenCanvas.start();
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

    @Override
    protected void onResume() {
        super.onResume();
        openCamera();
        initView();
        if (currentConsumerTextureView != null) {
            currentConsumerTextureView.onResume();
        }
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
        if (currentConsumerTextureView != null) {
            currentConsumerTextureView.onPause();
        }
        releaseCamera();
    }
}
