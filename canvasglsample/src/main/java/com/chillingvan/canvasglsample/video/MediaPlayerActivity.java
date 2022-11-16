package com.chillingvan.canvasglsample.video;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.Surface;
import android.view.View;

import com.chillingvan.canvasgl.glcanvas.RawTexture;
import com.chillingvan.canvasgl.glview.texture.GLSurfaceTextureProducerView;
import com.chillingvan.canvasgl.util.Loggers;
import com.chillingvan.canvasglsample.R;

import androidx.appcompat.app.AppCompatActivity;

public class MediaPlayerActivity extends AppCompatActivity {

    private MediaPlayerHelper mediaPlayer = new MediaPlayerHelper();
    private Surface mediaSurface;
    private MediaPlayerTextureView mediaPlayerTextureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        initTextureView();
    }

    private void initTextureView() {
        mediaPlayerTextureView = findViewById(R.id.media_player_texture_view);

        mediaPlayerTextureView.setOnSurfaceTextureSet(new GLSurfaceTextureProducerView.OnSurfaceTextureSet() {
            @Override
            public void onSet(SurfaceTexture surfaceTexture, RawTexture surfaceTextureRelatedTexture) {
                surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                    @Override
                    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                        Loggers.i("MediaPlayerActivity", "onFrameAvailable: ");
                        mediaPlayerTextureView.requestRenderAndWait();
                    }
                });

                mediaSurface = new Surface(surfaceTexture);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayerTextureView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayerTextureView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.release();
        }
    }

    public void onClickStart(View view) {
        if ((mediaPlayer.isPlaying() || mediaPlayer.isLooping())) {
            return;
        }

        playMedia();

    }

    private void playMedia() {
        mediaPlayer.playMedia(this, mediaSurface);
    }
}
