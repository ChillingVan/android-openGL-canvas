package com.chillingvan.canvasglsample.text;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.View;

import com.chillingvan.canvasgl.glcanvas.RawTexture;
import com.chillingvan.canvasgl.glview.texture.GLSurfaceTextureProducerView;
import com.chillingvan.canvasgl.util.Loggers;
import com.chillingvan.canvasglsample.R;
import com.chillingvan.canvasglsample.video.MediaPlayerHelper;

public class DrawTextActivity extends AppCompatActivity {

    private MediaPlayerHelper mediaPlayer = new MediaPlayerHelper();
    private Surface mediaSurface;
    private DrawTextTextureView drawTextTextureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_text);
        initTextureView();
    }

    private void initTextureView() {
        drawTextTextureView = findViewById(R.id.media_player_texture_view);

        drawTextTextureView.setOnSurfaceTextureSet(new GLSurfaceTextureProducerView.OnSurfaceTextureSet() {
            @Override
            public void onSet(SurfaceTexture surfaceTexture, RawTexture surfaceTextureRelatedTexture) {
                surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                    @Override
                    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                        Loggers.i("MediaPlayerActivity", "onFrameAvailable: ");
                        drawTextTextureView.requestRenderAndWait();
                    }
                });

                mediaSurface = new Surface(surfaceTexture);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        drawTextTextureView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        drawTextTextureView.onPause();
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
        drawTextTextureView.start();
    }
}
