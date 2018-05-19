package com.chillingvan.canvasglsample.offscreen;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.MultiTexOffScreenCanvas;
import com.chillingvan.canvasgl.glcanvas.RawTexture;
import com.chillingvan.canvasgl.glview.GLView;
import com.chillingvan.canvasgl.glview.texture.GLMultiTexProducerView;
import com.chillingvan.canvasgl.glview.texture.GLTexture;
import com.chillingvan.canvasgl.glview.texture.gles.GLThread;
import com.chillingvan.canvasglsample.R;
import com.chillingvan.canvasglsample.textureView.CameraUtils;
import com.chillingvan.canvasglsample.util.ScreenUtil;
import com.chillingvan.canvasglsample.video.MediaPlayerHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MultiTexOffScreenActivity extends AppCompatActivity {

    private List<Surface> mediaSurfaces = new ArrayList<>();
    private List<MediaPlayerHelper> mediaPlayers = new ArrayList<>();
    private MultiTexOffScreenCanvas multiTexOffScreenCanvas;
    private ImageView imageView;
    private Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_tex_off_screen);
        imageView = findViewById(R.id.off_screen_img_v);

    }

    private void initCanvas() {
        multiTexOffScreenCanvas = new MultiTexOffScreenCanvas(ScreenUtil.getScreenWidth(this), ScreenUtil.dpToPxInt(this, 150)) {
            {
                setProducedTextureTarget(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
            }

            @Override
            protected int getInitialTexCount() {
                return 3;
            }

            // Cannot set to continously when you are using the old camera API.
            @Override
            protected int getRenderMode() {
                return GLThread.RENDERMODE_WHEN_DIRTY;
            }

            @Override
            protected void onGLDraw(ICanvasGL canvas, List<GLTexture> producedTextures, List<GLTexture> consumedTextures) {
                int size = producedTextures.size();
                for (int i = 0; i < producedTextures.size(); i++) {
                    GLTexture texture = producedTextures.get(i);
                    int left = width * i / size;
                    RawTexture rawTexture = texture.getRawTexture();
                    rawTexture.setIsFlippedVertically(true);
                    canvas.drawSurfaceTexture(rawTexture, texture.getSurfaceTexture(), left, 0, left + width/size, height);
                }
            }
        };

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multiTexOffScreenCanvas.getDrawingBitmap(new Rect(0, 0, v.getWidth(), v.getHeight()), new GLView.GetDrawingCacheCallback() {
                    @Override
                    public void onFetch(Bitmap bitmap) {
                        imageView.setImageBitmap(bitmap);
                    }
                });

            }
        });

        multiTexOffScreenCanvas.setSurfaceTextureCreatedListener(new GLMultiTexProducerView.SurfaceTextureCreatedListener() {
            @Override
            public void onCreated(List<GLTexture> producedTextureList) {
                mediaPlayers.add(new MediaPlayerHelper(MediaPlayerHelper.TEST_VIDEO_MP4));
                mediaPlayers.add(new MediaPlayerHelper(MediaPlayerHelper.TEST_VIDEO_MP4_2));
                for (int i = 0; i < mediaPlayers.size(); i++) {
                    GLTexture glTexture = producedTextureList.get(i);
                    mediaSurfaces.add(new Surface(glTexture.getSurfaceTexture()));
                }

                // camera preview
                SurfaceTexture surfaceTexture = producedTextureList.get(2).getSurfaceTexture();
                surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                    @Override
                    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                        multiTexOffScreenCanvas.requestRender();
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
        multiTexOffScreenCanvas.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        openCamera();
        initCanvas();
        multiTexOffScreenCanvas.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
        multiTexOffScreenCanvas.onPause();
        for (MediaPlayerHelper mediaPlayer : mediaPlayers) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (MediaPlayerHelper mediaPlayer : mediaPlayers) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.release();
            }
        }
    }
}
