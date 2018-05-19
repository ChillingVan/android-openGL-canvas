package com.chillingvan.canvasglsample.multi;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.Toast;

import com.chillingvan.canvasgl.glview.texture.GLMultiTexProducerView;
import com.chillingvan.canvasgl.glview.texture.GLTexture;
import com.chillingvan.canvasglsample.R;
import com.chillingvan.canvasglsample.video.MediaPlayerHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MultiTextureActivity extends AppCompatActivity {

    private List<Surface> mediaSurfaces = new ArrayList<>();
    private List<MediaPlayerHelper> mediaPlayers = new ArrayList<>();
    private String mCameraId;
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mCaptureSession;
    private MultiVideoTexture multiVideoTexture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_texture);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Toast.makeText(this, "This only support >= Lollipop, 21", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

    }

    private void initTextures() {
        multiVideoTexture = findViewById(R.id.multi_texture);
        multiVideoTexture.setSurfaceTextureCreatedListener(new GLMultiTexProducerView.SurfaceTextureCreatedListener() {
            @Override
            public void onCreated(List<GLTexture> glTextureList) {
                mediaPlayers.add(new MediaPlayerHelper(MediaPlayerHelper.TEST_VIDEO_MP4));
                mediaPlayers.add(new MediaPlayerHelper(MediaPlayerHelper.TEST_VIDEO_MP4_2));
                for (int i = 0; i < mediaPlayers.size(); i++) {
                    GLTexture glTexture = glTextureList.get(i);
                    mediaSurfaces.add(new Surface(glTexture.getSurfaceTexture()));
                }

                createCameraPreviewSession(glTextureList.get(2).getSurfaceTexture());
            }
        });
    }

    private void createCameraPreviewSession(SurfaceTexture texture) {
        try {
            assert texture != null;
            // We configure the size of default buffer to be the size of camera preview we want.
//            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

            // This is the output Surface we need to start preview.
            Surface surface = new Surface(texture);

            // We set up a CaptureRequest.Builder with the output Surface.
            final CaptureRequest.Builder mPreviewRequestBuilder
                    = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);

            // Here, we create a CameraCaptureSession for camera preview.
            mCameraDevice.createCaptureSession(Arrays.asList(surface),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            // The camera is already closed
                            if (null == mCameraDevice) {
                                return;
                            }

                            // When the session is ready, we start displaying the preview.
                            mCaptureSession = cameraCaptureSession;
                            try {
                                // Auto focus should be continuous for camera preview.
                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

                                // Finally, we start displaying the camera preview.
                                CaptureRequest mPreviewRequest = mPreviewRequestBuilder.build();
                                mCaptureSession.setRepeatingRequest(mPreviewRequest,
                                        null, null);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(
                                @NonNull CameraCaptureSession cameraCaptureSession) {
                            Toast.makeText(MultiTextureActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }, null
            );
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        openCamera(1280, 720);
        initTextures();
        multiVideoTexture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeCamera();
        multiVideoTexture.onPause();
        for (MediaPlayerHelper mediaPlayer : mediaPlayers) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (MediaPlayerHelper mediaPlayer : mediaPlayers) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.release();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void openCamera(int width, int height) {
        setUpCameraOutputs(width, height);
        Activity activity = this;
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            manager.openCamera(mCameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    mCameraDevice = camera;

                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    camera.close();
                    mCameraDevice = null;

                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    camera.close();

                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void closeCamera() {
        if (null != mCaptureSession) {
            mCaptureSession.close();
            mCaptureSession = null;
        }
        if (null != mCameraDevice) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }


    private void setUpCameraOutputs(int width, int height) {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : manager.getCameraIdList()) {
                // 获取指定摄像头的特性
                CameraCharacteristics characteristics
                        = manager.getCameraCharacteristics(cameraId);

                // We don't use a back facing camera in this sample.
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK) {
                    continue;
                }

                // 获取摄像头支持的配置属性
                StreamConfigurationMap map = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) {
                    continue;
                }


                Point displaySize = new Point();
                getWindowManager().getDefaultDisplay().getSize(displaySize);
                int rotatedPreviewWidth = width;
                int rotatedPreviewHeight = height;
                int maxPreviewWidth = displaySize.x;
                int maxPreviewHeight = displaySize.y;

                // Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
                // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
                // garbage capture data.   获取最佳的预览尺寸
//                Size mPreviewSize = CameraUtils.chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
//                        rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth,
//                        maxPreviewHeight, largest);

                mCameraId = cameraId;
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
            Log.e("TAG", e.getMessage());
        }
    }

    public void onClickStart(View view) {
        for (int i = 0; i < mediaPlayers.size(); i++) {
            final MediaPlayerHelper mediaPlayer = mediaPlayers.get(i);
            final Surface mediaSurface = mediaSurfaces.get(i);
            if ((mediaPlayer.isPlaying() || mediaPlayer.isLooping())) {
                continue;
            }
            playMedia(mediaPlayer, mediaSurface);
        }
    }

    private void playMedia(MediaPlayerHelper mediaPlayer, Surface mediaSurface) {
        mediaPlayer.playMedia(this, mediaSurface);
    }
}
