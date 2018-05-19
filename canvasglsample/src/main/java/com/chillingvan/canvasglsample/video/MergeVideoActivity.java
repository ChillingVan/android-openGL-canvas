package com.chillingvan.canvasglsample.video;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.View;

import com.chillingvan.canvasgl.glcanvas.RawTexture;
import com.chillingvan.canvasgl.glview.texture.GLMultiTexProducerView;
import com.chillingvan.canvasgl.glview.texture.GLSurfaceTextureProducerView;
import com.chillingvan.canvasgl.glview.texture.GLTexture;
import com.chillingvan.canvasgl.glview.texture.gles.EglContextWrapper;
import com.chillingvan.canvasgl.glview.texture.gles.GLThread;
import com.chillingvan.canvasglsample.R;

import java.util.ArrayList;
import java.util.List;

import static com.chillingvan.canvasglsample.video.MediaPlayerHelper.TEST_VIDEO_MP4;
import static com.chillingvan.canvasglsample.video.MediaPlayerHelper.TEST_VIDEO_MP4_2;

public class MergeVideoActivity extends AppCompatActivity {

    private List<MediaPlayerHelper> mediaPlayers = new ArrayList<>();
    private List<Surface> mediaSurfaces = new ArrayList<>();
    private MergeVideoTextureView mergeVideoTextureView;
    private List<GLMultiTexProducerView> mediaPlayerTextureViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merge_video);

        final MediaPlayerTextureView mediaPlayerTextureView1 = findViewById(R.id.merge_media_1);
        final MediaPlayerProduceTextureView mediaPlayerTextureView2 = findViewById(R.id.merge_media_2);
        mergeVideoTextureView = findViewById(R.id.merge_media);
        mediaPlayerTextureViews.add(mediaPlayerTextureView1);
        mediaPlayerTextureViews.add(mediaPlayerTextureView2);
        mediaPlayerTextureView1.setOnCreateGLContextListener(new GLThread.OnCreateGLContextListener() {
            @Override
            public void onCreate(EglContextWrapper eglContext) {
                mediaPlayerTextureView2.setSharedEglContext(eglContext);
                mergeVideoTextureView.setSharedEglContext(eglContext);
            }
        });
        mediaPlayerTextureView1.setOnSurfaceTextureSet(new GLSurfaceTextureProducerView.OnSurfaceTextureSet() {
            @Override
            public void onSet(SurfaceTexture surfaceTexture, RawTexture surfaceTextureRelatedTexture) {
                surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                    @Override
                    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                        mediaPlayerTextureView1.requestRenderAndWait();
                    }
                });
                mergeVideoTextureView.addConsumeGLTexture(new GLTexture(surfaceTextureRelatedTexture, surfaceTexture));
                mediaSurfaces.add(new Surface(surfaceTexture));
                mediaPlayers.add(new MediaPlayerHelper(TEST_VIDEO_MP4));
            }
        });
        mediaPlayerTextureView2.setSurfaceTextureCreatedListener(new GLMultiTexProducerView.SurfaceTextureCreatedListener() {
            @Override
            public void onCreated(List<GLTexture> glTextureList) {
                GLTexture glTexture = glTextureList.get(0);
                SurfaceTexture surfaceTexture = glTexture.getSurfaceTexture();
                RawTexture rawTexture = glTexture.getRawTexture();
                surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                    @Override
                    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                        mediaPlayerTextureView2.requestRenderAndWait();
                    }
                });
                mergeVideoTextureView.addConsumeGLTexture(new GLTexture(rawTexture, surfaceTexture));
                mediaSurfaces.add(new Surface(surfaceTexture));
                mediaPlayers.add(new MediaPlayerHelper(TEST_VIDEO_MP4_2));
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();

        for (final GLMultiTexProducerView mediaPlayerTextureView : mediaPlayerTextureViews) {
            mediaPlayerTextureView.onResume();
        }
        mergeVideoTextureView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        for (MediaPlayerHelper mediaPlayer : mediaPlayers) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        }
        mergeVideoTextureView.onPause();
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

    private void playMedia(MediaPlayerHelper mediaPlayer, Surface mediaSurface) {
        mediaPlayer.playMedia(this, mediaSurface);
    }

    public void onClickStart(View view) {
        for (int i = 0; i < mediaPlayers.size(); i++) {
            final MediaPlayerHelper mediaPlayer = mediaPlayers.get(i);
            final Surface mediaSurface = mediaSurfaces.get(i);
            if ((mediaPlayer.isPlaying() || mediaPlayer.isLooping())) {
                continue;
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    playMedia(mediaPlayer, mediaSurface);
                }
            }, 500);
        }
    }
}
